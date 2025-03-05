package com.example.firstTry.model;

import com.example.firstTry.Enums.Gender;
import com.example.firstTry.Enums.PaymentMethod;
import com.example.firstTry.Enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.util.*;


@Entity
@Table(name = "doctors")
@Data
@EqualsAndHashCode(callSuper = false)
public class Doctor extends User {

    private String cin;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "profile_photo")
    private String profilePhotoUrl;

    @Column(name = "aptitude")
    private String aptitudeCertificateUrl;


    @Type(value = JsonType.class)
    @Column(columnDefinition = "json")
    private List<Education> education = new ArrayList<>();

    @Type(value = JsonType.class)
    @Column(columnDefinition = "json")
    private List<Experience> experience = new ArrayList<>();

    @Embedded
    private Office office;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    //@JsonManagedReference
    private Set<Specialization> specializations = new HashSet<>();

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "doctor_payment_methods",
            joinColumns = @JoinColumn(name = "doctor_id")
    )
    @Enumerated(EnumType.STRING)
    private Set<PaymentMethod> paymentMethods = new HashSet<>();

    //schedule--------------------------------------------------------------------------------------------------------
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference("doctor-working-hours")
    private Set<WorkingHours> workingHours = new HashSet<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.EAGER)
    @JsonManagedReference("doctor-appointments")
    private Set<Appointment> appointments = new HashSet<>();
    //schedule------------------------------------------------------------------------------------------------------



    public Doctor() {
        this.setRole(Role.ROLE_DOCTOR);
        this.setEnabled(false); // Disabled until admin approval
    }

    @Override
    public Role getRole() {
        return Role.ROLE_DOCTOR;
    }

    public Doctor(Doctor other) {
        this.setId(other.getId());
        this.setFirstName(other.getFirstName());
        this.setLastName(other.getLastName());
        this.setEmail(other.getEmail());
        this.setEnabled(other.isEnabled());
        this.cin = other.cin;
        this.gender = other.gender;
        this.age = other.age;
        this.phoneNumber = other.phoneNumber;
        this.billingAddress = other.billingAddress;
        this.profilePhotoUrl = other.profilePhotoUrl;
        this.aptitudeCertificateUrl = other.aptitudeCertificateUrl;
        this.office = other.office;
        this.languages = new ArrayList<>(other.languages); // Defensive copy
        this.paymentMethods = new HashSet<>(other.paymentMethods); // Defensive copy
        this.setRole(Role.ROLE_DOCTOR);
        this.setEnabled(false);
        this.specializations = new HashSet<>(); // Initialize, don't addAll
        this.education = new ArrayList<>(other.education); // Defensive copy
        this.experience = new ArrayList<>(other.experience); // Defensive copy
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(),getFirstName(), getLastName(), getEmail());
    }
    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", cin='" + cin + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", profilePhotoUrl='" + profilePhotoUrl + '\'' +
                ", aptitudeCertificateUrl='" + aptitudeCertificateUrl + '\'' +
                ", office=" + office +
                ", languages=" + languages +
                ", paymentMethods=" + paymentMethods +
                '}';
    }
}
