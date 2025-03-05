package com.example.firstTry.services;


import com.example.firstTry.Enums.DayOfWeek;
import com.example.firstTry.dto.AvailabilityResponse;
import com.example.firstTry.exception.ConflictException;
import com.example.firstTry.exception.InvalidAppointmentException;
import com.example.firstTry.exception.ResourceNotFoundException;
import com.example.firstTry.model.Appointment;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.Patient;
import com.example.firstTry.model.WorkingHours;
import com.example.firstTry.repository.AppointmentRepository;
import com.example.firstTry.repository.DoctorRepository;
import com.example.firstTry.repository.PatientRepository;
import com.example.firstTry.repository.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final WorkingHoursRepository workingHoursRepository;

    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Patient patient = patientRepository.findById(appointment.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        validateWorkingHours(doctor, appointment);
        checkForConflicts(doctor, patient, appointment);

        return appointmentRepository.save(appointment);
    }

    private void validateWorkingHours(Doctor doctor, Appointment appointment) {
        DayOfWeek day = DayOfWeek.from(
                appointment.getStartTime().getDayOfWeek()
        );

        LocalTime start = appointment.getStartTime().toLocalTime();
        LocalTime end = appointment.getEndTime().toLocalTime();

        WorkingHours workingHours = workingHoursRepository.findByDoctorAndDayOfWeek(doctor, day)
                .orElseThrow(() -> new InvalidAppointmentException("Doctor not available on this day"));

        if (start.isBefore(workingHours.getStartTime())) {
            throw new InvalidAppointmentException("Before working hours");
        }
        if (end.isAfter(workingHours.getEndTime())) {
            throw new InvalidAppointmentException("After working hours");
        }
    }

    private void checkForConflicts(Doctor doctor, Patient patient, Appointment appointment) {
        List<Appointment> doctorConflicts = appointmentRepository.findConflictingDoctorAppointments(
                doctor,
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        List<Appointment> patientConflicts = appointmentRepository.findConflictingPatientAppointments(
                patient,
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        if (!doctorConflicts.isEmpty() || !patientConflicts.isEmpty()) {
            throw new ConflictException("Time slot already booked");
        }
    }

    public AvailabilityResponse getAvailability(Long doctorId, LocalDate date, int duration) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        DayOfWeek dayOfWeek = DayOfWeek.from(date.getDayOfWeek());
        WorkingHours hours = workingHoursRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek)
                .orElseThrow(() -> new InvalidAppointmentException("Doctor not available"));

        LocalDateTime workStart = date.atTime(hours.getStartTime());
        LocalDateTime workEnd = date.atTime(hours.getEndTime());

        List<Appointment> appointments = appointmentRepository.findByDoctorAndStartTimeBetween(
                doctor, workStart, workEnd
        );

        return calculateAvailability(hours, appointments, duration, date);
    }

    private AvailabilityResponse calculateAvailability(WorkingHours hours,
                                                       List<Appointment> appointments,
                                                       int durationMinutes,
                                                       LocalDate date) {
        LocalDateTime workStart = date.atTime(hours.getStartTime());
        LocalDateTime workEnd = date.atTime(hours.getEndTime());

        List<LocalTime> slots = calculateAvailableSlots(
                workStart, workEnd, appointments, durationMinutes
        );

        // Filter out break times
        slots = filterOutBreaks(slots, hours.getBreaks());

        return new AvailabilityResponse(
                date,
                slots,
                hours.getBreaks().stream()
                        .map(WorkingHours.BreakTime::getStart)
                        .collect(Collectors.toList())
        );
    }

    private List<LocalTime> calculateAvailableSlots(
            LocalDateTime workStart,
            LocalDateTime workEnd,
            List<Appointment> appointments,
            int durationMinutes
    ) {
        List<TimeSlot> busySlots = appointments.stream()
                .map(a -> new TimeSlot(a.getStartTime(), a.getEndTime()))
                .sorted(Comparator.comparing(TimeSlot::start))
                .collect(Collectors.toList());

        List<TimeSlot> mergedBusy = mergeSlots(busySlots);
        List<TimeSlot> freeSlots = calculateFreeSlots(
                new TimeSlot(workStart, workEnd),
                mergedBusy
        );

        return generateTimeSlots(freeSlots, durationMinutes);
    }

    private List<LocalTime> filterOutBreaks(List<LocalTime> slots, List<WorkingHours.BreakTime> breaks) {
        return slots.stream()
                .filter(slotTime -> breaks.stream().noneMatch(breakTime ->
                        !slotTime.isBefore(breakTime.getStart()) &&
                                slotTime.isBefore(breakTime.getEnd())))
                .collect(Collectors.toList());
    }

    private record TimeSlot(LocalDateTime start, LocalDateTime end) {
    }

    private List<TimeSlot> mergeSlots(List<TimeSlot> slots) {
        if (slots.isEmpty()) return new ArrayList<>();

        List<TimeSlot> merged = new ArrayList<>();
        TimeSlot current = slots.get(0);

        for (TimeSlot next : slots) {
            if (current.end.isAfter(next.start)) {
                current = new TimeSlot(
                        current.start,
                        current.end.isAfter(next.end) ? current.end : next.end
                );
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    private List<TimeSlot> calculateFreeSlots(TimeSlot workingSlot, List<TimeSlot> busySlots) {
        List<TimeSlot> freeSlots = new ArrayList<>();
        LocalDateTime currentStart = workingSlot.start;

        for (TimeSlot busy : busySlots) {
            if (busy.start.isAfter(currentStart)) {
                freeSlots.add(new TimeSlot(currentStart, busy.start));
            }
            currentStart = busy.end.isAfter(currentStart) ? busy.end : currentStart;
        }

        if (currentStart.isBefore(workingSlot.end)) {
            freeSlots.add(new TimeSlot(currentStart, workingSlot.end));
        }

        return freeSlots;
    }

    private List<LocalTime> generateTimeSlots(List<TimeSlot> freeSlots, int durationMinutes) {
        List<LocalTime> slots = new ArrayList<>();
        Duration duration = Duration.ofMinutes(durationMinutes);

        for (TimeSlot free : freeSlots) {
            LocalDateTime current = free.start();
            while (current.plus(duration).isBefore(free.end()) ||
                    current.plus(duration).equals(free.end())) {
                slots.add(current.toLocalTime());
                current = current.plus(duration);
            }
        }
        return slots;
    }

}