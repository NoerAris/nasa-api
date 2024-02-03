package com.nasa.api.model;

import lombok.Data;

@Data
public class NasaModelResponse {
    private String id;
    private String name;
    private String urlDetail;
    private Double estimatedDiameterKmMin;
    private Double estimatedDiameterKmMax;
    private String closeApproachDateFull;
    private Double missDistanceKm;
    private boolean potentiallyHazardousAsteroid;
}
