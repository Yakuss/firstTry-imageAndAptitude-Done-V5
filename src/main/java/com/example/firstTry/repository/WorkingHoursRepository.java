package com.example.firstTry.repository;

import com.example.firstTry.Enums.DayOfWeek;
import com.example.firstTry.model.Doctor;
import com.example.firstTry.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    Optional<WorkingHours> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
    List<WorkingHours> findByDoctorId(Long doctorId);

    //void deleteByDoctorAndDayOfWeekIn(Doctor doctor, List<DayOfWeek> days);

    @Modifying
    @Query("DELETE FROM WorkingHours wh WHERE wh.doctor = :doctor AND wh.dayOfWeek IN :days")
    void deleteByDoctorAndDayOfWeekIn(
            @Param("doctor") Doctor doctor,
            @Param("days") List<DayOfWeek> days
    );
//news
    List<WorkingHours> findAllByDoctorId(Long doctorId);

    void deleteByDoctorAndDayOfWeekIn(Doctor doctor, Collection<DayOfWeek> days);

    Optional<WorkingHours> findByIdAndDoctorId(Long id, Long doctorId);

    List<WorkingHours> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek day);

    boolean existsByIdAndDoctorId(Long id, Long doctorId);

    //newww

    @Modifying
    @Query("DELETE FROM WorkingHours wh WHERE wh.doctor = :doctor AND wh.dayOfWeek IN :days")
    void deleteByDoctorAndDays(@Param("doctor") Doctor doctor, @Param("days") List<DayOfWeek> days);



    @Query("SELECT wh FROM WorkingHours wh " +
            "WHERE wh.doctor.id = :doctorId " +
            "AND wh.dayOfWeek = :day " +
            "AND wh.id <> :excludeId " +
            "AND ((wh.startTime < :endTime AND wh.endTime > :startTime))")
    List<WorkingHours> findOverlappingEntries(
            @Param("doctorId") Long doctorId,
            @Param("day") DayOfWeek day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );


}