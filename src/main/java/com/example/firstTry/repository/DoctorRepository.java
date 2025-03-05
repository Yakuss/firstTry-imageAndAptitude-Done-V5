package com.example.firstTry.repository;

import com.example.firstTry.model.Doctor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findByEnabledFalse();


    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT d FROM Doctor d LEFT JOIN FETCH d.specializations")
    List<Doctor> findAllWithSpecializations();


    @Query("SELECT DISTINCT d FROM Doctor d " +
            "LEFT JOIN FETCH d.specializations " +
            "WHERE d.id = :id")
    Optional<Doctor> findByIdWithSpecializations(@Param("id") Long id);

    @EntityGraph(attributePaths = "specializations")
    Optional<Doctor> findById(Long id);




}