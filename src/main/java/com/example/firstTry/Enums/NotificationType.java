package com.example.firstTry.Enums;

public enum NotificationType {
    DOCTOR_PENDING_APPROVAL, // Sent to Admin when a Doctor registers
    APPOINTMENT_REMINDER,    // Sent to Doctor/Patient for appointment reminders
    ACCOUNT_APPROVED,        // Sent to Doctor when Admin approves their account
    GENERAL_ANNOUNCEMENT,    // System-wide messages for all users
    PAYMENT_REMINDER,        // Payment due reminders
    NEW_MESSAGE,             // Private messages from patients or doctors
    SYSTEM_ALERT,            // Critical system notifications
    APPOINTMENT_REQUEST,
    APPOINTMENT_CONFIRMATION,
    APPOINTMENT_CANCELLATION
}
