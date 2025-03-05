package com.example.firstTry.services;


import com.example.firstTry.Enums.DayOfWeek;
import com.example.firstTry.dto.WorkingHoursDto;
import com.example.firstTry.exception.ResourceNotFoundException;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.WorkingHours;
import com.example.firstTry.repository.DoctorRepository;
import com.example.firstTry.repository.WorkingHoursRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkingHoursServiceV {

    private final DoctorRepository doctorRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Transactional
    public List<WorkingHours> replaceWorkingHours(Long doctorId, List<WorkingHoursDto> requests) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        List<DayOfWeek> daysToUpdate = extractDaysToUpdate(requests);
        deleteExistingHours(doctor, daysToUpdate);

        return saveNewHours(doctor, requests);
    }

    private List<DayOfWeek> extractDaysToUpdate(List<WorkingHoursDto> requests) {
        return requests.stream()
                .map(WorkingHoursDto::getDayOfWeek)
                .collect(Collectors.toList());
    }

    private void deleteExistingHours(Doctor doctor, List<DayOfWeek> days) {
        workingHoursRepository.deleteByDoctorAndDayOfWeekIn(doctor, days);
    }

    private List<WorkingHours> saveNewHours(Doctor doctor, List<WorkingHoursDto> requests) {
        return workingHoursRepository.saveAll(
                requests.stream()
                        .map(request -> convertToEntity(request, doctor))
                        .collect(Collectors.toList())
        );
    }

    private WorkingHours convertToEntity(WorkingHoursDto request, Doctor doctor) {
        return WorkingHours.builder()
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .breaks(convertBreaks(request.getBreaks()))
                .doctor(doctor)
                .build();
    }

    private List<WorkingHours.BreakTime> convertBreaks(List<WorkingHoursDto.BreakTimeRequest> breaks) {
        return breaks.stream()
                .map(br -> new WorkingHours.BreakTime(br.getStart(), br.getEnd()))
                .collect(Collectors.toList());
    }
}