package com.example.firstTry.model;

import com.example.firstTry.Enums.TunisianGovernorate;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Office {
    private String address;

    @Enumerated(EnumType.STRING)
    private TunisianGovernorate governorate;

    private Double longitude;
    private Double latitude;

}
