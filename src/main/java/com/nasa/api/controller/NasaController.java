package com.nasa.api.controller;

import com.nasa.api.model.NasaModelResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/nasa")
public class NasaController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(NasaController.class);

    @GetMapping("/top-10-nearest-asteroids")
    public List<NasaModelResponse> findTop10NearestAsteroidByDate(@RequestParam(name = "start-date") String startDate, @RequestParam(name = "end-date") String endDate) {
        try {
            return nasaService.findTop10NearestAsteroidsByStartDateAndEndDate(startDate, endDate);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);

            return new ArrayList<>();
        }
    }

    @GetMapping("/detail")
    public NasaModelResponse detailAsteroid(@RequestParam(name = "id") String id) {
        try {
            return nasaService.detailAsteroid(id);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);

            return new NasaModelResponse();
        }
    }
}
