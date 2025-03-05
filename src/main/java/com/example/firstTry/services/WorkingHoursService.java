package com.example.firstTry.services;

import com.example.firstTry.Enums.DayOfWeek;
import com.example.firstTry.dto.WorkingHoursDto;
import com.example.firstTry.exception.ConflictException;
import com.example.firstTry.exception.InvalidScheduleException;
import com.example.firstTry.exception.ResourceNotFoundException;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.WorkingHours;
import com.example.firstTry.repository.DoctorRepository;
import com.example.firstTry.repository.WorkingHoursRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkingHoursService {

    private final DoctorRepository doctorRepository;
    private final WorkingHoursRepository workingHoursRepository;

    public List<WorkingHours> replaceWorkingHours(Long doctorId, List<WorkingHoursDto> dtos) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        List<WorkingHours> newHours = dtos.stream()
                .map(this::convertToEntity)
                .peek(wh -> wh.setDoctor(doctor))
                .toList();

        validateWorkingHours(newHours);
        checkForDayDuplicates(newHours);

        List<DayOfWeek> daysToReplace = newHours.stream()
                .map(WorkingHours::getDayOfWeek)
                .toList();

        workingHoursRepository.deleteByDoctorAndDays(doctor, daysToReplace);
        return workingHoursRepository.saveAll(newHours);
    }

    public WorkingHours updateWorkingHours(Long doctorId, Long workingHoursId, WorkingHoursDto dto) {
        WorkingHours existing = workingHoursRepository.findByIdAndDoctorId(workingHoursId, doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Working hours not found"));

        // Convert breaks from DTO
        List<WorkingHours.BreakTime> newBreaks = dto.getBreaks().stream()
                .map(br -> new WorkingHours.BreakTime(br.getStart(), br.getEnd()))
                .collect(Collectors.toList());

        // Validate using WorkingHours object
        WorkingHours temp = new WorkingHours();
        temp.setDayOfWeek(dto.getDayOfWeek());
        temp.setStartTime(dto.getStartTime());
        temp.setEndTime(dto.getEndTime());
        temp.setBreaks(newBreaks);

        validateTimeRange(temp);
        validateBreaks(temp);
        checkForOverlaps(existing.getDoctor().getId(), temp, workingHoursId);

        // Update fields
        existing.setDayOfWeek(temp.getDayOfWeek());
        existing.setStartTime(temp.getStartTime());
        existing.setEndTime(temp.getEndTime());
        existing.setBreaks(temp.getBreaks());

        return workingHoursRepository.save(existing);
    }


    public void deleteWorkingHours(Long doctorId, Long workingHoursId) {
        if (!workingHoursRepository.existsByIdAndDoctorId(workingHoursId, doctorId)) {
            throw new ResourceNotFoundException("Working hours not found");
        }
        workingHoursRepository.deleteById(workingHoursId);
    }

    public List<WorkingHours> getAllWorkingHours(Long doctorId) {
        return workingHoursRepository.findByDoctorId(doctorId);
    }

    private WorkingHours convertToEntity(WorkingHoursDto dto) {
        WorkingHours wh = new WorkingHours();
        wh.setDayOfWeek(dto.getDayOfWeek());
        wh.setStartTime(dto.getStartTime());
        wh.setEndTime(dto.getEndTime());

        List<WorkingHours.BreakTime> breaks = dto.getBreaks().stream()
                .map(br -> new WorkingHours.BreakTime(br.getStart(), br.getEnd()))
                .collect(Collectors.toList());

        wh.setBreaks(breaks);
        return wh;
    }

    private void validateWorkingHours(List<WorkingHours> hours) {
        hours.forEach(this::validateTimeRange);
        hours.forEach(this::validateBreaks);
    }


    private void validateTimeRange(WorkingHours wh) {
        if (wh.getStartTime().isAfter(wh.getEndTime())) {
            throw new InvalidScheduleException("End time must be after start time");
        }
    }


    private void validateBreaks(WorkingHours wh) {
        for (WorkingHours.BreakTime breakTime : wh.getBreaks()) {
            if (breakTime.getStart().isBefore(wh.getStartTime()) ||
                    breakTime.getEnd().isAfter(wh.getEndTime())) {
                throw new InvalidScheduleException("Break must be within working hours");
            }
        }
    }

    private void checkForDayDuplicates(List<WorkingHours> hours) {
        Set<DayOfWeek> days = new HashSet<>();
        for (WorkingHours wh : hours) {
            if (!days.add(wh.getDayOfWeek())) {
                throw new ConflictException("Multiple entries for day: " + wh.getDayOfWeek());
            }
        }
    }

    private void checkForOverlaps(Long doctorId, WorkingHours wh, Long excludeId) {
        List<WorkingHours> overlaps = workingHoursRepository.findOverlappingEntries(
                doctorId,
                wh.getDayOfWeek(),
                wh.getStartTime(),
                wh.getEndTime(),
                excludeId != null ? excludeId : -1L
        );

        if (!overlaps.isEmpty()) {
            throw new ConflictException("Time slot overlaps with existing schedule");
        }
    }
}
