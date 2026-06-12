# syntax=docker/dockerfile:1.7
# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build
#   Usa imagem completa do JDK/Maven apenas neste estágio.
#   Nada do build toolchain chega à imagem final.
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copia somente os descritores de dependência primeiro.
# Camada separada: muda raramente, maximiza cache de build.
COPY therapist-scheduler-api/pom.xml ./
COPY therapist-scheduler-api/.mvn/ .mvn/
COPY therapist-scheduler-api/mvnw ./
RUN chmod +x mvnw

# Baixa dependências sem compilar o código-fonte.
# --mount=type=cache: cache do Maven fica fora da imagem (sem inchar layers).
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    ./mvnw dependency:go-offline --no-transfer-progress -q

# Copia o código e compila.
COPY therapist-scheduler-api/src/ src/
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    ./mvnw package -DskipTests --no-transfer-progress -q

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runtime
#   Imagem mínima UBI9 com apenas o JRE (sem JDK, sem Maven, sem shell util).
#   UID 185 é o usuário não-root padrão desta imagem Red Hat.
# ─────────────────────────────────────────────────────────────────────────────
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24

# OCI labels — rastreabilidade da imagem
LABEL org.opencontainers.image.title="therapist-scheduler-api" \
      org.opencontainers.image.description="Mock REST API – agenda de terapeuta" \
      org.opencontainers.image.vendor="felipovski" \
      org.opencontainers.image.licenses="MIT" \
      org.opencontainers.image.base.name="registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24"

# Sem segredos em ENV — apenas configurações não sensíveis.
ENV LANGUAGE="en_US:en" \
    JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager" \
    JAVA_APP_JAR="/deployments/quarkus-run.jar"

# Quatro camadas de COPY separadas:
#   lib/    → dependências externas (muda raramente)
#   *.jar   → quarkus-run.jar + manifestos (muda raramente)
#   app/    → bytecode da aplicação (muda a cada build)
#   quarkus/ → framework gerado (muda com upgrades de versão)
# --chown garante que nenhum arquivo pertence a root.
COPY --from=build --chown=185:185 /build/target/quarkus-app/lib/     /deployments/lib/
COPY --from=build --chown=185:185 /build/target/quarkus-app/*.jar     /deployments/
COPY --from=build --chown=185:185 /build/target/quarkus-app/app/      /deployments/app/
COPY --from=build --chown=185:185 /build/target/quarkus-app/quarkus/  /deployments/quarkus/

# Executa como usuário não-root (UID:GID 185:185).
USER 185:185

# Declara apenas a porta necessária.
EXPOSE 8080

# Healthcheck: verifica se a API responde.
# --start-period dá tempo para a JVM aquecer antes de marcar como unhealthy.
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD bash -c 'echo > /dev/tcp/localhost/8080' 2>/dev/null || exit 1

ENTRYPOINT ["/opt/jboss/container/java/run/run-java.sh"]

# ─────────────────────────────────────────────────────────────────────────────
# Flags de segurança recomendadas no `docker run`:
#
#   --security-opt=no-new-privileges:true
#   --cap-drop=ALL
#   --read-only
#   --tmpfs /tmp:rw,noexec,nosuid,size=64m
#   --memory=256m --cpus=0.5
# ─────────────────────────────────────────────────────────────────────────────
