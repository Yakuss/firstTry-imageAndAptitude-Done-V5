package com.example.firstTry.controller;

import com.example.firstTry.model.Admin;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.Notification;
import com.example.firstTry.model.Patient;
import com.example.firstTry.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<Notification>> getAdminNotifications(Authentication authentication) {
        Admin admin = (Admin) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getAdminNotifications(admin));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor")
    public ResponseEntity<List<Notification>> getDoctorNotifications(Authentication authentication) {
        Doctor doctor = (Doctor) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getDoctorNotifications(doctor));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient")
    public ResponseEntity<List<Notification>> getPatientNotifications(Authentication authentication) {
        Patient patient = (Patient) authentication.getPrincipal();
        return ResponseEntity.ok(notificationService.getPatientNotifications(patient));
    }
}
