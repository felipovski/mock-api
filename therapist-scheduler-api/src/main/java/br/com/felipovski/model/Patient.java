package br.com.felipovski.model;

public class Patient {

    public Long id;
    public String name;
    public String email;
    public String phone;
    public String notes;

    public Patient() {}

    public Patient(Long id, String name, String email, String phone, String notes) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.notes = notes;
    }
}
