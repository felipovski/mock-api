package br.com.felipovski.resource;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Therapist Scheduler API",
        version = "1.0.0",
        description = "Mock REST API para agenda de terapeuta — gerencia pacientes e consultas.",
        contact = @Contact(name = "felipovski", url = "https://github.com/felipovski")
    ),
    tags = {
        @Tag(name = "Appointments", description = "Agendamento e gerenciamento de consultas"),
        @Tag(name = "Patients",     description = "Cadastro de pacientes")
    }
)
public class OpenApiConfig extends Application {}
