package com.example.firstTry.services;

import com.example.firstTry.Enums.NotificationType;
import com.example.firstTry.dto.*;
import com.example.firstTry.mappers.DoctorMapper;
import com.example.firstTry.mappers.EducationMapper;
import com.example.firstTry.mappers.ExperienceMapper;
import com.example.firstTry.model.*;
import com.example.firstTry.repository.AdminRepository;
import com.example.firstTry.repository.DoctorRepository;
import com.example.firstTry.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final AdminRepository adminRepository;
    private final FileStorageService fileStorageService;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @Transactional
    public DoctorResponseDTO createDoctor(
            DoctorRequestDTO request,
            MultipartFile aptitudeCert,
            MultipartFile profilePhoto
    ) throws IOException {
        // Check email uniqueness
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        // Convert DTO to entity
        Doctor doctor = DoctorMapper.toDoctor(request);
        doctor.setPassword(passwordEncoder.encode(request.getPassword()));
        doctor.setSpecializations(getSpecializations(request.getSpecializationIds()));

        // Initial save to generate ID
        Doctor savedDoctor = doctorRepository.save(doctor);

        try {
            // Handle required aptitude certificate
            String aptitudeFilename = fileStorageService.storeFile(
                    aptitudeCert,
                    "aptitude",
                    savedDoctor.getId()
            );
            savedDoctor.setAptitudeCertificateUrl(aptitudeFilename);

            // Handle optional profile photo
            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                String profileFilename = fileStorageService.storeFile(
                        profilePhoto,
                        "profile",
                        savedDoctor.getId()
                );
                savedDoctor.setProfilePhotoUrl(profileFilename);
            }

            // Notify admins
            adminRepository.findAll().forEach(admin -> {
                notificationService.notifyAdmin(admin,
                        "New doctor registration: " + savedDoctor.getEmail(),
                        NotificationType.DOCTOR_PENDING_APPROVAL);
            });

            return DoctorMapper.toDoctorResponseDTO(savedDoctor);

        } catch (IOException ex) {
            // Cleanup files if entity creation fails
            if (savedDoctor.getId() != null) {
                fileStorageService.deleteFile(savedDoctor.getAptitudeCertificateUrl());
                if (savedDoctor.getProfilePhotoUrl() != null) {
                    fileStorageService.deleteFile(savedDoctor.getProfilePhotoUrl());
                }
            }
            throw ex;
        }
    }

    @Transactional
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO request) {
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        // Update basic fields
        existingDoctor.setFirstName(request.getFirstName());
        existingDoctor.setLastName(request.getLastName());
        existingDoctor.setCin(request.getCin());
        existingDoctor.setGender(request.getGender());
        existingDoctor.setAge(request.getAge());
        existingDoctor.setPhoneNumber(request.getPhoneNumber());
        existingDoctor.setBillingAddress(request.getBillingAddress());

        // Update relationships
        if (request.getSpecializationIds() != null) {
            Set<Specialization> specializations = getSpecializations(request.getSpecializationIds());
            existingDoctor.setSpecializations(specializations);
        }

        // Update JSON fields
        if (request.getEducation() != null) {
            existingDoctor.setEducation(request.getEducation().stream()
                    .map(EducationMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        if (request.getExperience() != null) {
            existingDoctor.setExperience(request.getExperience().stream()
                    .map(ExperienceMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        // Save updated doctor
        Doctor updatedDoctor = doctorRepository.save(existingDoctor);
        return DoctorMapper.toDoctorResponseDTO(updatedDoctor);
    }


public List<DoctorResponseDTO> getAllDoctors() {
    return doctorRepository.findAllWithSpecializations().stream()
            .map(doctor -> {
                // Create defensive copies of all collections
                Doctor copy = new Doctor(doctor);  // Implement copy constructor
                copy.setSpecializations(new HashSet<>(doctor.getSpecializations()));
                copy.setEducation(new ArrayList<>(doctor.getEducation()));
                copy.setExperience(new ArrayList<>(doctor.getExperience()));
                return DoctorMapper.toDoctorResponseDTO(copy);
            })
            .collect(Collectors.toList());
}



    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doc = doctorRepository.findByIdWithSpecializations(id).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        Doctor copy = new Doctor(doc);
        copy.setSpecializations(new HashSet<>(doc.getSpecializations()));
        copy.setEducation(new ArrayList<>(doc.getEducation()));
        copy.setExperience(new ArrayList<>(doc.getExperience()));
        return DoctorMapper.toDoctorResponseDTO(copy);
    }





    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        doctorRepository.delete(doctor);
    }

    private Set<Specialization> getSpecializations(Set<Long> specializationIds) {
        if (specializationIds == null || specializationIds.isEmpty()) {
            return Collections.emptySet();
        }

        List<Specialization> specializationsList = specializationRepository.findAllById(specializationIds);
        Set<Specialization> specializations = new HashSet<>(specializationsList);

        if (specializations.size() != specializationIds.size()) {
            List<Long> foundIds = specializations.stream()
                    .map(Specialization::getId)
                    .toList();

            List<Long> missingIds = specializationIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new EntityNotFoundException("Specializations not found: " + missingIds);
        }

        return specializations;
    }

    @Transactional
    public void updateAptitudeCertificate(Long doctorId, String filename) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
        doctor.setAptitudeCertificateUrl(filename);
        doctorRepository.save(doctor);
    }

    @Transactional
    public void updateProfilePhoto(Long doctorId, MultipartFile file) throws IOException {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        // Delete old file if exists
        if (doctor.getProfilePhotoUrl() != null) {
            Files.deleteIfExists(
                    Paths.get(uploadDir).resolve(doctor.getProfilePhotoUrl())
            );
        }

        String filename = fileStorageService.storeFile(
                file,
                "profile",
                doctorId
        );

        doctor.setProfilePhotoUrl(filename);
        doctorRepository.save(doctor);
    }

}