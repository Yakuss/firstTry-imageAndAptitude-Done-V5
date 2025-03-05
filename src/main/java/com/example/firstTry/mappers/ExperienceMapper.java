package com.example.firstTry.mappers;

import com.example.firstTry.dto.ExperienceDTO;
import com.example.firstTry.model.Experience;

public class ExperienceMapper {

    public static ExperienceDTO toDto(Experience experience) {
        if (experience == null) return null;
        ExperienceDTO dto = new ExperienceDTO();
        dto.setName(experience.getName());
        dto.setEstablishment(experience.getEstablishment());
        dto.setDuration(experience.getDuration());
        return dto;
    }

    public static Experience toEntity(ExperienceDTO dto) {
        if (dto == null) return null;
        Experience entity = new Experience();
        entity.setName(dto.getName());
        entity.setEstablishment(dto.getEstablishment());
        entity.setDuration(dto.getDuration());
        return entity;
    }
}