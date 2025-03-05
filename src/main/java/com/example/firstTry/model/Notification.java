package com.example.firstTry.model;

import com.example.firstTry.Enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private NotificationType type;

    @Column(name = "is_read") // Renamed column to avoid SQL keyword conflict
    private boolean isRead = false;

    private LocalDateTime timestamp = LocalDateTime.now();

    // Relationships to Admin, Doctor, and Patient
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true)
    private Admin adminRecipient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = true)
    private Doctor doctorRecipient;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = true)
    private Patient patientRecipient;

    public boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

}