package com.example.firstTry.services;

import com.example.firstTry.model.Doctor;
import com.example.firstTry.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final DoctorRepository doctorRepository;
    private final EmailService emailService;

    public List<Doctor> getPendingDoctors() {
        return doctorRepository.findByEnabledFalse();
    }

    @Transactional
    public Doctor approveDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        doctor.setEnabled(true);
        Doctor savedDoctor=doctorRepository.save(doctor);
        System.out.printf("Doctor approved: %s\n", savedDoctor);
        System.out.printf("==== SENDING DOCTOR APPROVED MAIL====\n");
        //emailService.sendDoctorApprovalEmail(savedDoctor);

        return savedDoctor ;
    }

    @Transactional
    public void declineDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        doctor.setEnabled(false);
        System.out.printf("Doctor approved: %s\n", doctor);
        System.out.printf("==== SENDING DOCTOR DECLINED MAIL====\n");
        //emailService.sendDoctorDeclinedEmail(doctor);
        doctorRepository.deleteById(doctorId); // Direct deletion (adjust if you need soft-delete)
    }



}
