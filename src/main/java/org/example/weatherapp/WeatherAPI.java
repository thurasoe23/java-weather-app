package org.example.weatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 * WeatherAPI class handles communication with the OpenWeatherMap API.
 */
public class WeatherAPI {
    private static final String API_KEY = "1e11167c62ecd7f9562cc6d0c340742c";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast";

    /**
     * Fetches current weather data for a given location.
     * @param location Name of the city
     * @return JSON object containing weather data
     */
    public static JSONObject getWeatherData(String location) {
        return fetchData(BASE_URL, location);
    }

    /**
     * Fetches forecast data for a given location.
     * @param location Name of the city
     * @return JSON object containing forecast data
     */
    public static JSONObject getForecastData(String location) {
        return fetchData(FORECAST_URL, location);
    }

    /**
     * Fetches data from the API.
     * @param baseUrl The base URL of the API endpoint
     * @param location Name of the city
     * @return JSON object containing the response data
     */
    private static JSONObject fetchData(String baseUrl, String location) {
        try {
            String urlString = baseUrl + "?q=" + location + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            return new JSONObject(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}