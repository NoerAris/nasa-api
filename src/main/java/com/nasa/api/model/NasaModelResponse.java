package com.nasa.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NasaModelResponse {
    private String id;
    private String name;
    private String urlDetail;
    private BigDecimal estimatedDiameterKmMin;
    private BigDecimal estimatedDiameterKmMax;
    private String closeApproachDateFull;
    private BigDecimal missDistanceKm;
    private boolean potentiallyHazardousAsteroid;
    private List<CloseApproachModel> closeApproachModels;
    private String orbitId;
    private String firstObservationDate;
    private String lastObservationDate;
    private BigDecimal orbitalPeriod;
    private String orbitClassType;
}