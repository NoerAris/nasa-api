package com.nasa.api.service;

import com.nasa.api.model.NasaModelResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NasaService {
    List<NasaModelResponse> findTop10NearestAsteroidsByStartDateAndEndDate(String startDate, String endDate);
}
