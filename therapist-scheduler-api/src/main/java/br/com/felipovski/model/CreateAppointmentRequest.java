package br.com.felipovski.model;

import java.time.LocalDateTime;

public class CreateAppointmentRequest {

    public Long patientId;
    public LocalDateTime dateTime;
    public Integer durationMinutes;
    public String sessionType;
    public String notes;
}
