package com.example.firstTry.controller;



import com.example.firstTry.Enums.Role;
import com.example.firstTry.dto.DoctorRequestDTO;
import com.example.firstTry.dto.DoctorResponseDTO;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.repository.DoctorRepository;
import com.example.firstTry.services.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors/a")
@RequiredArgsConstructor
public class DoctorControllerAdmin {

    private final DoctorService doctorService;


    // Create doctor (public registration)
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<DoctorResponseDTO> createDoctor(
//            @Valid @RequestBody DoctorRequestDTO doctorRequest) {
//        return ResponseEntity.ok(doctorService.createDoctor(doctorRequest));
//    }

    // Get all doctors (admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // Get doctor by ID (admin or the doctor themselves)
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(
            @PathVariable Long id,
            Authentication authentication) {
        DoctorResponseDTO doctor = doctorService.getDoctorById(id);
        //checkAuthorization(id, authentication);
        return ResponseEntity.ok(doctor);
    }


    // Update doctor (admin or the doctor themselves)
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequestDTO doctorRequest,
            Authentication authentication) {
        //checkAuthorization(id, authentication);
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorRequest));
    }

    // Delete doctor (admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // Approve doctor (admin only)
//    @PatchMapping("/{id}/approve")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<DoctorResponseDTO> approveDoctor(@PathVariable Long id) {
//        return ResponseEntity.ok(doctorService.approveDoctor(id));
//    }

    // Get current doctor's profile
    @GetMapping("/me")
    public ResponseEntity<DoctorResponseDTO> getCurrentDoctorProfile(
            Authentication authentication) {
        Long doctorId = ((Doctor) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    // Update current doctor's profile
    @PutMapping("/me")
    public ResponseEntity<DoctorResponseDTO> updateCurrentDoctorProfile(
            @Valid @RequestBody DoctorRequestDTO doctorRequest,
            Authentication authentication) {
        Long doctorId = ((Doctor) authentication.getPrincipal()).getId();
        return ResponseEntity.ok(doctorService.updateDoctor(doctorId, doctorRequest));
    }

    private void checkAuthorization(Long targetDoctorId, Authentication authentication) {
        Doctor currentUser = (Doctor) authentication.getPrincipal();

        if (!currentUser.getRole().equals(Role.ROLE_ADMIN) )
        {
            if (!currentUser.getId().equals(targetDoctorId)) {
                throw new AccessDeniedException("You are not authorized to access this resource");
            }
        }
    }
}