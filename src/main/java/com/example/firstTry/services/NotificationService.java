package com.example.firstTry.services;

import com.example.firstTry.Enums.NotificationType;
import com.example.firstTry.dto.NotificationDto;
import com.example.firstTry.model.*;
import com.example.firstTry.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper ;

    // Notify an Admin
    public void notifyAdmin(Admin admin, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setAdminRecipient(admin);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);
        System.out.println("=== SENDING NOTIFICATION TO ADMIN  ===");
        System.out.println("Admin ID: " + admin.getId());
        System.out.println("Topic: /topic/admin/" + admin.getId());
        String topic = "/topic/admin/" + admin.getId();
        System.out.println("Sending to topic: " + topic);
        NotificationDto dto = convertToDto(notification);
        try {
            System.out.println("DTO Content: " + objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        messagingTemplate.convertAndSend("/topic/admin/" + admin.getId(), dto);
    }

    private NotificationDto convertToDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.getIsRead(),
                notification.getTimestamp()
        );
    }

    // Should show in console when notifications are sent


    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
    }



    // Notify a Doctor
    public void notifyDoctor(Doctor doctor, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setDoctorRecipient(doctor);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/doctor/" + doctor.getId(), convertToDto(notification));
    }





    // Notify a Patient
    public void notifyPatient(Patient patient, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setPatientRecipient(patient);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/patient/" + patient.getId(), convertToDto(notification));
    }

    // Retrieve notifications
    public List<Notification> getAdminNotifications(Admin admin) {
        return notificationRepository.findByAdminRecipient(admin);
    }

    public List<Notification> getDoctorNotifications(Doctor doctor) {
        return notificationRepository.findByDoctorRecipient(doctor);
    }

    public List<Notification> getPatientNotifications(Patient patient) {
        return notificationRepository.findByPatientRecipient(patient);
    }
}
