package com.example.firstTry.dto;

import com.example.firstTry.Enums.Gender;
import com.example.firstTry.Enums.PaymentMethod;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DoctorResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String cin;
    private Gender gender;
    private Integer age;
    private String phoneNumber;
    private String email;
    private String billingAddress;
    private OfficeDTO office;
    private List<SpecializationDTO> specializations;
    private List<String> languages;
    private Set<PaymentMethod> paymentMethods;
    private List<EducationDTO> education;
    private List<ExperienceDTO> experience;
    private String profilePhotoUrl;
    private String aptitudeCertificateUrl;
}
