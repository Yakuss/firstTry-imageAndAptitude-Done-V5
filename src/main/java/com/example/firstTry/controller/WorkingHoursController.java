package com.example.firstTry.controller;

import com.example.firstTry.dto.WorkingHoursDto;
import com.example.firstTry.exception.ConflictException;
import com.example.firstTry.exception.InvalidScheduleException;
import com.example.firstTry.model.WorkingHours;
import com.example.firstTry.services.WorkingHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors/{doctorId}/working-hours")
@RequiredArgsConstructor
public class WorkingHoursController {

    private final WorkingHoursService workingHoursService;

    @PostMapping
    public ResponseEntity<?> setWorkingHours(
            @PathVariable Long doctorId,
            @Valid @RequestBody List<WorkingHoursDto> requests) {
        try {
            List<WorkingHours> result = workingHoursService.replaceWorkingHours(doctorId, requests);
            return ResponseEntity.ok(result);
        } catch (InvalidScheduleException | ConflictException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateWorkingHours(
            @PathVariable Long doctorId,
            @PathVariable Long id,
            @Valid @RequestBody WorkingHoursDto request) {
        try {
            WorkingHours updated = workingHoursService.updateWorkingHours(doctorId, id, request);
            return ResponseEntity.ok(updated);
        } catch (InvalidScheduleException | ConflictException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkingHours(
            @PathVariable Long doctorId,
            @PathVariable Long id) {
        workingHoursService.deleteWorkingHours(doctorId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WorkingHours>> getWorkingHours(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(workingHoursService.getAllWorkingHours(doctorId));
    }
}
