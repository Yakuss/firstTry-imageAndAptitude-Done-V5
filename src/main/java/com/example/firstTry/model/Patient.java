package com.example.firstTry.model;

import com.example.firstTry.Enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "patients")
@Data
@EqualsAndHashCode(callSuper = false)
public class Patient extends User {
    private String insuranceId;
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "patient")
    @JsonManagedReference("patient-appointments") // Child side
    private Set<Appointment> appointments = new HashSet<>();

    public Patient() {
        this.setRole(Role.ROLE_PATIENT);
    }

    @Override
    public Role getRole() {
        return Role.ROLE_PATIENT;
    }



    @Override
    public int hashCode() {
        return Objects.hash(getId(),getFirstName(), getLastName(), getEmail());
    }
}
