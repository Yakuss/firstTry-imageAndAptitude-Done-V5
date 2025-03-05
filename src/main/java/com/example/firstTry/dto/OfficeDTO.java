package com.example.firstTry.dto;

import com.example.firstTry.Enums.TunisianGovernorate;
import lombok.Data;

@Data
public class OfficeDTO {
    private String address;
    private TunisianGovernorate governorate;
    private Double longitude;
    private Double latitude;
}