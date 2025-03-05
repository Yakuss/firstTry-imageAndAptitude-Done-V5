package com.example.firstTry.controller;

import com.example.firstTry.model.Patient;
import com.example.firstTry.repository.PatientRepository;
import com.example.firstTry.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    private final PatientRepository patientRepository;


    // Get patient profile
    @GetMapping("/profile")
    public ResponseEntity<Patient> getProfile(Authentication authentication) {
        Patient patient = (Patient) authentication.getPrincipal();
        return ResponseEntity.ok(patient);
    }

    // Update patient profile
    @PutMapping("/profile")
    public ResponseEntity<Patient> updateProfile(@RequestBody Patient updatedPatient,
                                                 Authentication authentication) {
        Patient currentPatient = (Patient) authentication.getPrincipal();

        // Only allow updating specific fields
        currentPatient.setInsuranceId(updatedPatient.getInsuranceId());
        currentPatient.setDateOfBirth(updatedPatient.getDateOfBirth());

        Patient savedPatient = patientRepository.save(currentPatient);
        return ResponseEntity.ok(savedPatient);
    }
}