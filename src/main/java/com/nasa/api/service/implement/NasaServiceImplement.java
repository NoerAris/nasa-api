package com.nasa.api.service.implement;

import com.nasa.api.model.CloseApproachModel;
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

    // NASA API URL format
    private static final String NASA_FEED_URL_FORMAT = "https://api.nasa.gov/neo/rest/v1/feed?start_date=%s&end_date=%s&api_key=%s";

    private static final String NASA_LOOKUP_URL_FORMAT = "https://api.nasa.gov/neo/rest/v1/neo/%s?api_key=%s";

    // Maximum count of asteroids to retrieve
    private static final int MAX_ASTEROID_COUNT = 10;

    // Default NASA API key, can be overridden in application properties
    @Value("${nasa.api.key:DEMO_KEY}")
    private String nasaApiKey;

    // REST template for making HTTP requests
    private final RestTemplate restTemplate;

    // Constructor to initialize RestTemplate bean
    public NasaServiceImplement() {
        this.restTemplate = new RestTemplate();
    }

    // Method to find the top 10 nearest asteroids within a date range
    @Override
    public List<NasaModelResponse> findTop10NearestAsteroidsByStartDateAndEndDate(String startDate, String endDate) {
        // Construct the NASA API URL with parameters
        String nasaFeedUrl = String.format(NASA_FEED_URL_FORMAT, startDate, endDate, nasaApiKey);

        // Make HTTP GET request to NASA API and retrieve the JSON response
        String jsonResponse = restTemplate.getForObject(nasaFeedUrl, String.class);

        // Check if the JSON response is not null
        if (jsonResponse != null) {
            // Parse the JSON response into a JSONObject
            JSONObject response = new JSONObject(jsonResponse);

            // Extract the "near_earth_objects" object from the response
            JSONObject neartEarthObjects = response.getJSONObject("near_earth_objects");

            // Lists to store asteroid data
            List<JSONObject> asteroidListAll = new ArrayList<>();
            JSONArray top10AsteroidsResult = new JSONArray();
            JSONArray topAsteroids = new JSONArray();

            // Check if "near_earth_objects" object is not null
            if (neartEarthObjects != null) {
                // Iterate over each date in "near_earth_objects"
                for (Iterator<String> it = neartEarthObjects.keys(); it.hasNext(); ) {
                    String date = it.next();

                    // Get the array of asteroids for the current date
                    JSONArray asteroids = neartEarthObjects.getJSONArray(date);

                    // Check if there are asteroids for the current date
                    if (asteroids.length() > 0) {
                        List<JSONObject> asteroidList = new ArrayList<>();

                        // Iterate over each asteroid in the array
                        for (int i = 0; i < asteroids.length(); i++) {
                            // Add the asteroid to the list
                            asteroidList.add(asteroids.getJSONObject(i));
                        }

                        // Sort the list of asteroids by miss distance
                        asteroidList.sort(Comparator.comparingDouble(a -> {
                            try {
                                // Extract the miss distance from the first close_approach_data object
                                JSONArray closeApproachData = a.getJSONArray("close_approach_data");
                                if (closeApproachData.length() > 0) {
                                    return closeApproachData.getJSONObject(0)
                                            .getJSONObject("miss_distance").getDouble("kilometers");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // Return Double.MAX_VALUE if there is an exception
                            return Double.MAX_VALUE;
                        }));

                        // Add the top asteroids to the topAsteroids array
                        for (int i = 0; i < Math.min(MAX_ASTEROID_COUNT, asteroidList.size()); i++) {
                            topAsteroids.put(asteroidList.get(i));
                        }
                    }
                }
            }

            // Check if there are top asteroids
            if (topAsteroids.length() > 0) {
                // Add all top asteroids to asteroidListAll
                for (int i = 0; i < topAsteroids.length(); i++) {
                    asteroidListAll.add(topAsteroids.getJSONObject(i));
                }

                // Sort the list of all asteroids by miss distance
                asteroidListAll.sort(Comparator.comparingDouble(a -> {
                    try {
                        // Extract the miss distance from the first close_approach_data object
                        JSONArray closeApproachData = a.getJSONArray("close_approach_data");
                        if (closeApproachData.length() > 0) {
                            return closeApproachData.getJSONObject(0)
                                    .getJSONObject("miss_distance").getDouble("kilometers");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Return Double.MAX_VALUE if there is an exception
                    return Double.MAX_VALUE;
                }));

                // Add the top 10 asteroids to top10AsteroidsResult
                for (int i = 0; i < Math.min(MAX_ASTEROID_COUNT, asteroidListAll.size()); i++) {
                    top10AsteroidsResult.put(asteroidListAll.get(i));
                }

                // List to store NasaModelResponse objects
                List<NasaModelResponse> nasaModelResponses = new ArrayList<>();

                // Iterate over top 10 asteroids and create NasaModelResponse objects
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
                    resp.setCloseApproachModels(null);
                    resp.setOrbitalPeriod(null);

                    // Add the NasaModelResponse object to the list
                    nasaModelResponses.add(resp);
                }

                // Return the list of NasaModelResponse objects
                return nasaModelResponses;
            }
        }

        // Return an empty list if no data is available
        return new ArrayList<>();
    }

    /**
     * Retrieves detailed information about an asteroid using its NASA ID.
     *
     * @param id The NASA ID of the asteroid.
     * @return A {@link NasaModelResponse} containing detailed information about the asteroid.
     */
    @Override
    public NasaModelResponse detailAsteroid(String id) {
        String nasaLookupUrl = String.format(NASA_LOOKUP_URL_FORMAT, id, nasaApiKey);
        String jsonResponse = restTemplate.getForObject(nasaLookupUrl, String.class);

        if (jsonResponse != null) {
            NasaModelResponse resp = new NasaModelResponse();
            JSONObject json = new JSONObject(jsonResponse);

            resp.setId(json.getString("id"));
            resp.setName(json.getString("name"));
            resp.setUrlDetail(json.getString("nasa_jpl_url"));
            resp.setEstimatedDiameterKmMin(json.getJSONObject("estimated_diameter").getJSONObject("kilometers").getBigDecimal("estimated_diameter_min"));
            resp.setEstimatedDiameterKmMax(json.getJSONObject("estimated_diameter").getJSONObject("kilometers").getBigDecimal("estimated_diameter_max"));

            List<CloseApproachModel> closeApproachModels = new ArrayList<>();
            if (json.getJSONArray("close_approach_data").length() > 0) {
                JSONArray data = new JSONArray(json.getJSONArray("close_approach_data"));
                for (int i = 0; i < data.length(); i++) {
                    CloseApproachModel closeApproachModel = new CloseApproachModel();

                    closeApproachModel.setCloseApproachDateFull(data.getJSONObject(i).getString("close_approach_date_full"));
                    closeApproachModel.setRelativeVelocityInKmHour(data.getJSONObject(i).getJSONObject("relative_velocity").getBigDecimal("kilometers_per_hour"));
                    closeApproachModel.setMissDistanceInKm(data.getJSONObject(i).getJSONObject("miss_distance").getBigDecimal("kilometers"));
                    closeApproachModel.setOrbitingBody(data.getJSONObject(i).getString("orbiting_body"));

                    closeApproachModels.add(closeApproachModel);
                }

                resp.setCloseApproachModels(closeApproachModels);
            }

            resp.setPotentiallyHazardousAsteroid(json.getBoolean("is_potentially_hazardous_asteroid"));
            resp.setOrbitId(json.getJSONObject("orbital_data").getString("orbit_id"));
            resp.setFirstObservationDate(json.getJSONObject("orbital_data").getString("first_observation_date"));
            resp.setLastObservationDate(json.getJSONObject("orbital_data").getString("last_observation_date"));
            resp.setOrbitalPeriod(json.getJSONObject("orbital_data").getBigDecimal("orbital_period"));
            resp.setOrbitClassType(json.getJSONObject("orbital_data").getJSONObject("orbit_class").getString("orbit_class_type"));

            return resp;

        }
        return null;
    }
}