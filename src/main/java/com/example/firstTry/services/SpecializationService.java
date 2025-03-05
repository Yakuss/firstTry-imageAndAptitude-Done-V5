package com.example.firstTry.services;

import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.Specialization;
import com.example.firstTry.repository.DoctorRepository;
import com.example.firstTry.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;

    // Create a new specialization with unique name validation
    public Specialization createSpecialization(Specialization specialization) {
        if (specializationRepository.existsByName(specialization.getName())) {
            throw new IllegalArgumentException("Specialization with name '" + specialization.getName() + "' already exists.");
        }
        return specializationRepository.save(specialization);
    }

    // Retrieve all specializations
    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    // Get specialization by ID or throw exception
    public Specialization getSpecializationById(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Specialization not found with id: " + id));
    }

    // Get specialization by name or throw exception
    public Specialization getSpecializationByName(String name) {
        return specializationRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Specialization not found with name: " + name));
    }

    // Update an existing specialization
    public Specialization updateSpecialization(Long id, Specialization updatedSpecialization) {
        Specialization existing = getSpecializationById(id);

        // Check if the name is being updated and validate uniqueness
        String newName = updatedSpecialization.getName();
        if (!existing.getName().equals(newName)) {
            if (specializationRepository.existsByName(newName)) {
                throw new IllegalArgumentException("Specialization with name '" + newName + "' already exists.");
            }
            existing.setName(newName);
        }

        // Update description
        existing.setDescription(updatedSpecialization.getDescription());
        return specializationRepository.save(existing);
    }

    // Delete a specialization and manage associations with doctors
    public void deleteSpecialization(Long id) {
        Specialization specialization = getSpecializationById(id);

        // Remove this specialization from all associated doctors
        Set<Doctor> doctors = specialization.getDoctors();
        doctors.forEach(doctor -> doctor.getSpecializations().remove(specialization));
        doctorRepository.saveAll(doctors); // Save updated doctors

        specializationRepository.delete(specialization);
    }

}
