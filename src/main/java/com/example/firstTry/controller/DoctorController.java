package com.example.firstTry.controller;

import com.example.firstTry.dto.DoctorRequestDTO;
import com.example.firstTry.dto.DoctorResponseDTO;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.services.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/my-profile")
    public ResponseEntity<DoctorResponseDTO> getMyProfile(Authentication authentication) {
        DoctorResponseDTO doctor = doctorService.getDoctorById(getCurrentDoctorId(authentication));
        return ResponseEntity.ok(doctor);
    }

    @PutMapping("/my-profile")
    public ResponseEntity<DoctorResponseDTO> updateMyProfile(
            @Valid @RequestBody DoctorRequestDTO updateRequest,
            Authentication authentication) {
        Long doctorId = getCurrentDoctorId(authentication);
        DoctorResponseDTO updatedDoctor = doctorService.updateDoctor(doctorId, updateRequest);
        return ResponseEntity.ok(updatedDoctor);
    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PatchMapping("/{id}/approve")
//    public ResponseEntity<DoctorResponseDTO> approveDoctor(@PathVariable Long id) {
//        DoctorResponseDTO approvedDoctor = doctorService.approveDoctor(id);
//        return ResponseEntity.ok(approvedDoctor);
//    }

    private Long getCurrentDoctorId(Authentication authentication) {
        return ((Doctor) authentication.getPrincipal()).getId();
    }
}