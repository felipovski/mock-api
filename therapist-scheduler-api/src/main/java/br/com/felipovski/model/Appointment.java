package br.com.felipovski.model;

import java.time.LocalDateTime;

public class Appointment {

    public Long id;
    public Patient patient;
    public LocalDateTime dateTime;
    public Integer durationMinutes;
    public AppointmentStatus status;
    public String sessionType;
    public String notes;

    public Appointment() {}

    public Appointment(Long id, Patient patient, LocalDateTime dateTime,
                       Integer durationMinutes, AppointmentStatus status,
                       String sessionType, String notes) {
        this.id = id;
        this.patient = patient;
        this.dateTime = dateTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.sessionType = sessionType;
        this.notes = notes;
    }
}
