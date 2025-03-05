package com.example.firstTry.mappers;

import com.example.firstTry.dto.EducationDTO;
import com.example.firstTry.model.Education;

public class EducationMapper {

    public static EducationDTO toDto(Education education) {
        if (education == null) return null;
        EducationDTO dto = new EducationDTO();
        dto.setName(education.getName());
        dto.setEstablishment(education.getEstablishment());
        dto.setYear(education.getYear());
        return dto;
    }

    public static Education toEntity(EducationDTO dto) {
        if (dto == null) return null;
        Education entity = new Education();
        entity.setName(dto.getName());
        entity.setEstablishment(dto.getEstablishment());
        entity.setYear(dto.getYear());
        return entity;
    }
}