package com.example.firstTry.model;

import com.example.firstTry.Enums.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 9)
    private DayOfWeek dayOfWeek;

    @Column(columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(columnDefinition = "TIME")
    private LocalTime endTime;

    @ElementCollection
    @CollectionTable(name = "working_hours_breaks", joinColumns = @JoinColumn(name = "working_hours_id"))
    private List<BreakTime> breaks = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonBackReference("doctor-working-hours")
    private Doctor doctor;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BreakTime {
        @Column(columnDefinition = "TIME")
        private LocalTime start;

        @Column(columnDefinition = "TIME")
        private LocalTime end;

    }
}
