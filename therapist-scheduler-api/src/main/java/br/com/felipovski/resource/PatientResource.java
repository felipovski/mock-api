package br.com.felipovski.resource;

import br.com.felipovski.model.Patient;
import br.com.felipovski.service.MockDataStore;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

@Tag(name = "Patients")
@Path("/patients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource {

    private final MockDataStore store;

    public PatientResource(MockDataStore store) {
        this.store = store;
    }

    @Operation(summary = "Lista todos os pacientes")
    @GET
    public List<Patient> listAll() {
        return store.getAllPatients();
    }

    @Operation(summary = "Busca paciente por ID")
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

    @Operation(summary = "Lista consultas de um paciente")
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
