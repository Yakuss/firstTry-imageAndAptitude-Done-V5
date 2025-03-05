package com.example.firstTry.mappers;

import com.example.firstTry.dto.SpecializationDTO;
import com.example.firstTry.model.Specialization;

public class SpecializationMapper {

    public static SpecializationDTO toDto(Specialization specialization) {
        if (specialization == null) return null;
        SpecializationDTO dto = new SpecializationDTO();
        dto.setId(specialization.getId());
        dto.setName(specialization.getName());
        dto.setDescription(specialization.getDescription());
        return dto;
    }
}