package com.example.firstTry.repository;

import com.example.firstTry.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

    Optional<Specialization> findByName(String name);
    boolean existsByName(String name);
}
