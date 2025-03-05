package com.example.firstTry.mappers;

import com.example.firstTry.dto.DoctorRequestDTO;
import com.example.firstTry.dto.DoctorResponseDTO;
import com.example.firstTry.dto.SpecializationDTO;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.Specialization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DoctorMapper {

    public static Doctor toDoctor(DoctorRequestDTO dto) {
        Doctor doctor = new Doctor();

        // Map fields from User superclass
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setEmail(dto.getEmail());
        doctor.setPassword(dto.getPassword());

        // Map Doctor-specific fields
        doctor.setCin(dto.getCin());
        doctor.setGender(dto.getGender());
        doctor.setAge(dto.getAge());
        doctor.setPhoneNumber(dto.getPhoneNumber());
        doctor.setBillingAddress(dto.getBillingAddress());
        //doctor.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        //doctor.setAptitudeCertificateUrl(dto.getAptitudeCertificateUrl());

        // Map complex objects
        doctor.setOffice(OfficeMapper.toEntity(dto.getOffice()));

        // Map JSON arrays
        if (dto.getEducation() != null) {
            doctor.setEducation(dto.getEducation().stream()
                    .map(EducationMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        if (dto.getExperience() != null) {
            doctor.setExperience(dto.getExperience().stream()
                    .map(ExperienceMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        // Direct collections
        doctor.setLanguages(dto.getLanguages());
        doctor.setPaymentMethods(dto.getPaymentMethods());

        return doctor;
    }

    public static DoctorResponseDTO toDoctorResponseDTO(Doctor doctor) {
        DoctorResponseDTO dto = new DoctorResponseDTO();

        // Map fields from User superclass
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setEmail(doctor.getEmail());

        // Map Doctor-specific fields
        dto.setCin(doctor.getCin());
        dto.setGender(doctor.getGender());
        dto.setAge(doctor.getAge());
        dto.setPhoneNumber(doctor.getPhoneNumber());
        dto.setBillingAddress(doctor.getBillingAddress());
        dto.setProfilePhotoUrl(doctor.getProfilePhotoUrl());
        dto.setAptitudeCertificateUrl(doctor.getAptitudeCertificateUrl());

        // Map complex objects
        dto.setOffice(OfficeMapper.toDto(doctor.getOffice()));

        // Map relationships
        if (doctor.getSpecializations() != null) {
            Set<Specialization> specializations = new HashSet<>(doctor.getSpecializations());
            dto.setSpecializations(specializations.stream()
                    .map(SpecializationMapper::toDto)
                    .collect(Collectors.toList()));
        }


        // Map JSON arrays
        if (doctor.getEducation() != null) {
            dto.setEducation(doctor.getEducation().stream()
                    .map(EducationMapper::toDto)
                    .collect(Collectors.toList()));
        }

        if (doctor.getExperience() != null) {
            dto.setExperience(doctor.getExperience().stream()
                    .map(ExperienceMapper::toDto)
                    .collect(Collectors.toList()));
        }

        // Direct collections
        dto.setLanguages(doctor.getLanguages());
        dto.setPaymentMethods(doctor.getPaymentMethods());

        return dto;
    }
}