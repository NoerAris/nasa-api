package com.nasa.api.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NasaModelResponse {
    private String id;
    private String name;
    private String urlDetail;
    private BigDecimal estimatedDiameterKmMin;
    private BigDecimal estimatedDiameterKmMax;
    private String closeApproachDateFull;
    private BigDecimal missDistanceKm;
    private boolean potentiallyHazardousAsteroid;
}
