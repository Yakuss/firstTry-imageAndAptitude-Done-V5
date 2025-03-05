package com.example.firstTry.dto;

import com.example.firstTry.Enums.DayOfWeek;
import com.example.firstTry.model.WorkingHours;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class WorkingHoursDto {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<BreakTimeRequest> breaks;

    @Data
    public static class BreakTimeRequest {
        private LocalTime start;
        private LocalTime end;

        public WorkingHours.BreakTime toEntity() {
            WorkingHours.BreakTime breakTime = new WorkingHours.BreakTime();
            breakTime.setStart(this.start);
            breakTime.setEnd(this.end);
            return breakTime;
        }
    }
}
