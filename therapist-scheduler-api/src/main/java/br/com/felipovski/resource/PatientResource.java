package br.com.felipovski.resource;

import br.com.felipovski.model.Patient;
import br.com.felipovski.service.MockDataStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource {

    @Inject
    MockDataStore store;

    @GET
    public List<Patient> listAll() {
        return store.getAllPatients();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Patient patient = store.getPatient(id);
        if (patient == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Patient not found", "id", id))
                    .build();
        }
        return Response.ok(patient).build();
    }

    @GET
    @Path("/{id}/appointments")
    public Response getPatientAppointments(@PathParam("id") Long id) {
        Patient patient = store.getPatient(id);
        if (patient == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Patient not found", "id", id))
                    .build();
        }
        var appointments = store.getAllAppointments().stream()
                .filter(a -> a.patient.id.equals(id))
                .toList();
        return Response.ok(appointments).build();
    }
}
