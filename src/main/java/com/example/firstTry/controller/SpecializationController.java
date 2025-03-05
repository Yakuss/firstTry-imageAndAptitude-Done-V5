package com.example.firstTry.controller;

import com.example.firstTry.model.Specialization;
import com.example.firstTry.services.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialization")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    // Create a new specialization
    @PostMapping
    public ResponseEntity<Specialization> createSpecialization(@RequestBody Specialization specialization) {
        Specialization createdSpecialization = specializationService.createSpecialization(specialization);
        return new ResponseEntity<>(createdSpecialization, HttpStatus.CREATED);
    }

    // Get all specializations
    @GetMapping
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        List<Specialization> specializations = specializationService.getAllSpecializations();
        return ResponseEntity.ok(specializations);
    }

    // Get specialization by ID
    @GetMapping("/{id}")
    public ResponseEntity<Specialization> getSpecializationById(@PathVariable Long id) {
        Specialization specialization = specializationService.getSpecializationById(id);
        return ResponseEntity.ok(specialization);
    }

    // Get specialization by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Specialization> getSpecializationByName(@PathVariable String name) {
        Specialization specialization = specializationService.getSpecializationByName(name);
        return ResponseEntity.ok(specialization);
    }

    // Update a specialization
    @PutMapping("/{id}")
    public ResponseEntity<Specialization> updateSpecialization(
            @PathVariable Long id,
            @RequestBody Specialization updatedSpecialization
    ) {
        Specialization specialization = specializationService.updateSpecialization(id, updatedSpecialization);
        return ResponseEntity.ok(specialization);
    }

    // Delete a specialization
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialization(@PathVariable Long id) {
        specializationService.deleteSpecialization(id);
        return ResponseEntity.noContent().build();
    }


}
