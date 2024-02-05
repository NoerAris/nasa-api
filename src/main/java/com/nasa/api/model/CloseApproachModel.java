package com.nasa.api.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CloseApproachModel {
    private String closeApproachDateFull;
    private BigDecimal relativeVelocityInKmHour;
    private BigDecimal missDistanceInKm;
    private String orbitingBody;
}