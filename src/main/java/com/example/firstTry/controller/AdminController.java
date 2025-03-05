package com.example.firstTry.controller;

import com.example.firstTry.model.Doctor;
import com.example.firstTry.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;

    @PutMapping("/doctors/{id}/approve")
    public ResponseEntity<Doctor> approveDoctor(@PathVariable Long id) {
        Doctor approvedDoctor = adminService.approveDoctor(id);
        return ResponseEntity.ok(approvedDoctor);
    }

    @DeleteMapping("/doctors/{id}/decline")
    public ResponseEntity<Void> declineDoctor(@PathVariable Long id) {
        adminService.declineDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctors/pending")
    public ResponseEntity<List<Doctor>> getPendingDoctors() {
        return ResponseEntity.ok(adminService.getPendingDoctors());
    }
}
