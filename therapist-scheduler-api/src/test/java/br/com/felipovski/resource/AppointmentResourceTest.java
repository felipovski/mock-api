package br.com.felipovski.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class AppointmentResourceTest {

    @Test
    void listAllAppointments_returns200WithItems() {
        given()
            .when().get("/appointments")
            .then()
                .statusCode(200)
                .body("size()", org.hamcrest.Matchers.greaterThan(0));
    }

    @Test
    void getAppointmentById_existingId_returns200() {
        given()
            .when().get("/appointments/1")
            .then()
                .statusCode(200)
                .body("id", is(1));
    }

    @Test
    void getAppointmentById_unknownId_returns404() {
        given()
            .when().get("/appointments/9999")
            .then()
                .statusCode(404);
    }

    @Test
    void filterByStatus_returnsMatchingAppointments() {
        given()
            .queryParam("status", "CONFIRMED")
            .when().get("/appointments")
            .then()
                .statusCode(200)
                .body("status", everyItem(is("CONFIRMED")));
    }

    @Test
    void createAppointment_validRequest_returns201() {
        String body = """
                {
                  "patientId": 2,
                  "dateTime": "2026-07-01T10:00:00",
                  "durationMinutes": 50,
                  "sessionType": "Individual"
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when().post("/appointments")
            .then()
                .statusCode(201)
                .body("status", is("SCHEDULED"))
                .body("patient.id", is(2));
    }

    @Test
    void createAppointment_missingFields_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
            .when().post("/appointments")
            .then()
                .statusCode(400);
    }

    @Test
    void updateStatus_validStatus_returns200() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"status\": \"CONFIRMED\"}")
            .when().post("/appointments/2/status")
            .then()
                .statusCode(200)
                .body("status", is("CONFIRMED"));
    }

    @Test
    void updateStatus_invalidStatus_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"status\": \"INVALID\"}")
            .when().post("/appointments/1/status")
            .then()
                .statusCode(400);
    }
}
