package br.com.felipovski.service;

import br.com.felipovski.model.Appointment;
import br.com.felipovski.model.AppointmentStatus;
import br.com.felipovski.model.Patient;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class MockDataStore {

    private final Map<Long, Patient> patients = new ConcurrentHashMap<>();
    private final Map<Long, Appointment> appointments = new ConcurrentHashMap<>();
    private final AtomicLong appointmentSeq = new AtomicLong(100);

    public MockDataStore() {
        seedPatients();
        seedAppointments();
    }

    private void seedPatients() {
        patients.put(1L, new Patient(1L, "Ana Oliveira",   "ana.oliveira@email.com",   "(11) 98765-4321", "Ansiedade, primeira consulta em 2024"));
        patients.put(2L, new Patient(2L, "Carlos Mendes",  "carlos.mendes@email.com",  "(11) 91234-5678", "Depressão leve, acompanhamento semanal"));
        patients.put(3L, new Patient(3L, "Beatriz Santos", "beatriz@email.com",        "(21) 99876-5432", "Terapia de casal — aguarda parceiro"));
        patients.put(4L, new Patient(4L, "Rafael Lima",    "rafael.lima@email.com",    "(31) 98888-7777", "Síndrome de burnout"));
        patients.put(5L, new Patient(5L, "Fernanda Costa", "fernanda.costa@email.com", "(11) 97777-6666", "Fobia social"));
    }

    private void seedAppointments() {
        LocalDateTime base = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        appointments.put(1L, new Appointment(1L, patients.get(1L),
                base.plusDays(1).withHour(9),  50, AppointmentStatus.CONFIRMED,  "Individual", null));
        appointments.put(2L, new Appointment(2L, patients.get(2L),
                base.plusDays(1).withHour(10), 50, AppointmentStatus.SCHEDULED,  "Individual", "Retomar TREC"));
        appointments.put(3L, new Appointment(3L, patients.get(3L),
                base.plusDays(2).withHour(14), 80, AppointmentStatus.CONFIRMED,  "Casal",      null));
        appointments.put(4L, new Appointment(4L, patients.get(4L),
                base.plusDays(3).withHour(16), 50, AppointmentStatus.SCHEDULED,  "Individual", null));
        appointments.put(5L, new Appointment(5L, patients.get(5L),
                base.minusDays(2).withHour(9), 50, AppointmentStatus.COMPLETED,  "Individual", "Boa evolução"));
        appointments.put(6L, new Appointment(6L, patients.get(1L),
                base.minusDays(7).withHour(9), 50, AppointmentStatus.COMPLETED,  "Individual", null));
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients.values());
    }

    public Patient getPatient(Long id) {
        return patients.get(id);
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    public Appointment getAppointment(Long id) {
        return appointments.get(id);
    }

    public Appointment saveAppointment(Appointment appointment) {
        long newId = appointmentSeq.incrementAndGet();
        appointment.id = newId;
        appointments.put(newId, appointment);
        return appointment;
    }

    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment a = appointments.get(id);
        if (a == null) return null;
        a.status = status;
        return a;
    }
}
