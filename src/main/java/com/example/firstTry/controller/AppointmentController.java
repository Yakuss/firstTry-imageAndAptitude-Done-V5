package com.example.firstTry.controller;

import com.example.firstTry.dto.AvailabilityResponse;
import com.example.firstTry.model.Appointment;
import com.example.firstTry.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;



    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(appointment));
    }


    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "30") int duration
    ) {
        return ResponseEntity.ok(
                appointmentService.getAvailability(doctorId, date, duration)
        );
    }

//    @GetMapping("/doctor/{doctorId}")
//    public List<Appointment> getDoctorAppointments(@PathVariable Long doctorId) {
//        return appointmentService.getAppointmentsByDoctor(doctorId);
//    }
//
//    @GetMapping("/patient/{patientId}")
//    public List<Appointment> getPatientAppointments(@PathVariable Long patientId) {
//        return appointmentService.getAppointmentsByPatient(patientId);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
//        appointmentService.cancelAppointment(id);
//        return ResponseEntity.noContent().build();
//    }


}