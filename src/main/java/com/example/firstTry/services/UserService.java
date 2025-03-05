package com.example.firstTry.services;

import com.example.firstTry.Enums.NotificationType;
import com.example.firstTry.model.*;
import com.example.firstTry.repository.AdminRepository;
import com.example.firstTry.repository.DoctorRepository;
import com.example.firstTry.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check all user types
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) return admin.get();

        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) return doctor.get();

        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) return patient.get();

        throw new UsernameNotFoundException("User not found: " + email);
    }



    public Admin createAdmin(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public Doctor registerDoctor(Doctor doctor) {
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        doctor.setEnabled(false); // Doctor needs admin approval

        Doctor savedDoctor = doctorRepository.save(doctor);

        // Notify all admins about the new doctor registration
        adminRepository.findAll().forEach(admin -> {
            System.out.println("[DEBUG] Notifying admin ID: " + admin.getId());
            notificationService.notifyAdmin(admin,
                    "New doctor registration pending approval: " + doctor.getEmail(),
                    NotificationType.DOCTOR_PENDING_APPROVAL);
        });


        return savedDoctor;
    }




    public Patient registerPatient(Patient patient) {
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepository.save(patient);
    }
}
