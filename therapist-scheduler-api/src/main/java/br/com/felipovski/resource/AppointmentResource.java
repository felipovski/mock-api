package br.com.felipovski.resource;

import br.com.felipovski.model.Appointment;
import br.com.felipovski.model.AppointmentStatus;
import br.com.felipovski.model.CreateAppointmentRequest;
import br.com.felipovski.service.MockDataStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/appointments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppointmentResource {

    @Inject
    MockDataStore store;

    @GET
    public List<Appointment> listAll(@QueryParam("status") AppointmentStatus status) {
        List<Appointment> all = store.getAllAppointments();
        if (status != null) {
            return all.stream().filter(a -> a.status == status).toList();
        }
        return all;
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Appointment appointment = store.getAppointment(id);
        if (appointment == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Appointment not found", "id", id))
                    .build();
        }
        return Response.ok(appointment).build();
    }

    @POST
    public Response create(CreateAppointmentRequest request) {
        if (request.patientId == null || request.dateTime == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "patientId and dateTime are required"))
                    .build();
        }

        var patient = store.getPatient(request.patientId);
        if (patient == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Patient not found", "patientId", request.patientId))
                    .build();
        }

        var appointment = new Appointment();
        appointment.patient = patient;
        appointment.dateTime = request.dateTime;
        appointment.durationMinutes = request.durationMinutes != null ? request.durationMinutes : 50;
        appointment.sessionType = request.sessionType != null ? request.sessionType : "Individual";
        appointment.status = AppointmentStatus.SCHEDULED;
        appointment.notes = request.notes;

        Appointment saved = store.saveAppointment(appointment);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @POST
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, Map<String, String> body) {
        String statusValue = body.get("status");
        if (statusValue == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Field 'status' is required"))
                    .build();
        }

        AppointmentStatus newStatus;
        try {
            newStatus = AppointmentStatus.valueOf(statusValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid status. Valid values: SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW"))
                    .build();
        }

        Appointment updated = store.updateAppointmentStatus(id, newStatus);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Appointment not found", "id", id))
                    .build();
        }
        return Response.ok(updated).build();
    }
}
