package br.com.felipovski.model;

public class ScheduleSlotRequest {

    public String patientName;
    public String date;       // formato: "2026-06-20"
    public String time;       // formato: "14:00"
    public String sessionType;
    public Integer durationMinutes;
    public String notes;
}
