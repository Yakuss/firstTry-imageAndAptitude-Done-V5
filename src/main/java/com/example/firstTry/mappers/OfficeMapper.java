package com.example.firstTry.mappers;

import com.example.firstTry.dto.OfficeDTO;
import com.example.firstTry.model.Office;

public class OfficeMapper {

    public static OfficeDTO toDto(Office office) {
        if (office == null) return null;
        OfficeDTO dto = new OfficeDTO();
        dto.setAddress(office.getAddress());
        dto.setGovernorate(office.getGovernorate());
        dto.setLongitude(office.getLongitude());
        dto.setLatitude(office.getLatitude());
        return dto;
    }

    public static Office toEntity(OfficeDTO dto) {
        if (dto == null) return null;
        Office office = new Office();
        office.setAddress(dto.getAddress());
        office.setGovernorate(dto.getGovernorate());
        office.setLongitude(dto.getLongitude());
        office.setLatitude(dto.getLatitude());
        return office;
    }
}