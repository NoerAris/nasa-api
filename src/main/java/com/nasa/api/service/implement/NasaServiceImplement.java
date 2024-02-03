package com.nasa.api.service.implement;

import com.nasa.api.model.NasaModelResponse;
import com.nasa.api.service.NasaService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Service
public class NasaServiceImplement implements NasaService {
    @Value("${nasa.api.key:DEMO_KEY}")
    private String nasaApiKey;

    @Override
    public List<NasaModelResponse> findTop10NearestAsteroidsByStartDateAndEndDate(String startDate, String endDate) {
        String nasaFeedUrl = "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + startDate + "&end_date=" + endDate + "&api_key=" + nasaApiKey;
        RestTemplate restTemplate = new RestTemplate();

        String jsonResponse = restTemplate.getForObject(nasaFeedUrl, String.class);
        if (jsonResponse != null) {
            JSONObject response = new JSONObject(jsonResponse);
            JSONObject neartEarthObjects = response.getJSONObject("near_earth_objects");

            List<JSONObject> asteroidListAll = new ArrayList<>();
            JSONArray top10AsteroidsResult = new JSONArray();
            JSONArray topAsteroids = new JSONArray();

            if (neartEarthObjects != null) {
                for (Iterator it = neartEarthObjects.keys(); it.hasNext(); ) {
                    String date = (String) it.next();

                    JSONArray asteroids = neartEarthObjects.getJSONArray(date);
                    if (asteroids.length() > 0) {
                        List<JSONObject> asteroidList = new ArrayList<>();
                        for (int i = 0; i < asteroids.length(); i++) {
                            asteroidList.add(asteroids.getJSONObject(i));
                        }

                        asteroidList.sort(Comparator.comparingDouble(a -> {
                            try {
                                JSONArray closeApproachData = a.getJSONArray("close_approach_data");
                                if (closeApproachData.length() > 0) {
                                    return closeApproachData.getJSONObject(0)
                                            .getJSONObject("miss_distance").getDouble("kilometers");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return Double.MAX_VALUE;
                        }));

                        for (int i = 0; i < Math.min(10, asteroidList.size()); i++) {
                            if (i > asteroidList.size()) {
                                break;
                            }
                            topAsteroids.put(asteroidList.get(i));
                        }

                    }
                }
            }

            if (topAsteroids.length() > 0) {
                for (int i = 0; i < topAsteroids.length(); i++) {
                    asteroidListAll.add(topAsteroids.getJSONObject(i));
                }

                asteroidListAll.sort(Comparator.comparingDouble(a -> {
                    try {
                        JSONArray closeApproachData = a.getJSONArray("close_approach_data");
                        if (closeApproachData.length() > 0) {
                            return closeApproachData.getJSONObject(0)
                                    .getJSONObject("miss_distance").getDouble("kilometers");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Double.MAX_VALUE;
                }));

                for (int i = 0; i < Math.min(10, asteroidListAll.size()); i++) {
                    if (i > asteroidListAll.size()) {
                        break;
                    }
                    top10AsteroidsResult.put(asteroidListAll.get(i));
                }

                List<NasaModelResponse> nasaModelResponses = new ArrayList<>();
                for (int i = 0; i < top10AsteroidsResult.length(); i++) {
                    NasaModelResponse resp = new NasaModelResponse();
                    JSONObject json = new JSONObject(top10AsteroidsResult.get(i).toString());
                    resp.setId(json.getString("id"));
                    resp.setName(json.getString("name"));
                    resp.setUrlDetail(json.getString("nasa_jpl_url"));
                    resp.setEstimatedDiameterKmMin(json.getJSONObject("estimated_diameter").getJSONObject("kilometers").getBigDecimal("estimated_diameter_min"));
                    resp.setEstimatedDiameterKmMax(json.getJSONObject("estimated_diameter").getJSONObject("kilometers").getBigDecimal("estimated_diameter_max"));
                    resp.setCloseApproachDateFull(json.getJSONArray("close_approach_data").getJSONObject(0).getString("close_approach_date_full"));
                    resp.setMissDistanceKm(json.getJSONArray("close_approach_data").getJSONObject(0).getJSONObject("miss_distance").getBigDecimal("kilometers"));
                    resp.setPotentiallyHazardousAsteroid(json.getBoolean("is_potentially_hazardous_asteroid"));

                    nasaModelResponses.add(resp);
                }

                return nasaModelResponses;
            }
        }

        return new ArrayList<>();
    }
}
