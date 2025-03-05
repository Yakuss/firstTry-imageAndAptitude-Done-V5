package com.example.firstTry.repository;

import com.example.firstTry.model.Admin;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.Notification;
import com.example.firstTry.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAdminRecipient(Admin admin);
    List<Notification> findByDoctorRecipient(Doctor doctor);
    List<Notification> findByPatientRecipient(Patient patient);
}