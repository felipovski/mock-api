package br.com.felipovski.resource;

import br.com.felipovski.model.ScheduleSlotRequest;
import br.com.felipovski.model.ScheduleSlotResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Tag(name = "Schedule")
@SecurityScheme(
        securitySchemeName = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleResource {

    static final String MOCK_TOKEN = "mock-token-biketrack-2026";
    private static final AtomicLong seq = new AtomicLong(1000);

    @Operation(
            summary = "Agenda um horário",
            description = "Cria um novo agendamento. Requer Bearer token: `mock-token-biketrack-2026`"
    )
    @SecurityRequirement(name = "BearerAuth")
    @POST
    public Response schedule(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
                             ScheduleSlotRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Authorization header ausente ou inválido"))
                    .build();
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (!MOCK_TOKEN.equals(token)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Token inválido"))
                    .build();
        }

        if (request == null || request.patientName == null || request.date == null || request.time == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Os campos patientName, date e time são obrigatórios"))
                    .build();
        }

        var response = new ScheduleSlotResponse();
        response.scheduleId     = seq.incrementAndGet();
        response.patientName    = request.patientName;
        response.date           = request.date;
        response.time           = request.time;
        response.sessionType    = request.sessionType != null ? request.sessionType : "Individual";
        response.durationMinutes = request.durationMinutes != null ? request.durationMinutes : 50;
        response.notes          = request.notes;
        response.status         = "SCHEDULED";
        response.confirmedAt    = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
