package com.example.firstTry.Enums;

public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    // Add conversion from java.time.DayOfWeek
    public static DayOfWeek fromJavaTimeDay(java.time.DayOfWeek javaDay) {
        return DayOfWeek.valueOf(javaDay.name());
    }

    public static DayOfWeek from(java.time.DayOfWeek javaDay) {
        return valueOf(javaDay.name());
    }
}