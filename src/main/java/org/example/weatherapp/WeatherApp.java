package org.example.weatherapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * WeatherApp class extends Application to create a JavaFX-based weather information application.
 */
public class WeatherApp extends Application {
    private boolean isCelsius = true; // Flag to track temperature unit
    private boolean isMetersPerSecond = true; // Flag to track wind speed unit
    private final List<String> searchHistory = new ArrayList<>(); // List to store search history
    private final ImageView weatherIcon = new ImageView(); // ImageView to display weather icon

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Information App");

        // Setup the layout grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Location input
        Label locationLabel = new Label("Location:");
        GridPane.setConstraints(locationLabel, 0, 0);
        TextField locationInput = new TextField();
        GridPane.setConstraints(locationInput, 1, 0);

        // Fetch weather button
        Button fetchButton = new Button("Fetch Weather");
        GridPane.setConstraints(fetchButton, 1, 1);

        // Toggle units button
        Button toggleUnitsButton = new Button("Toggle Units");
        GridPane.setConstraints(toggleUnitsButton, 1, 2);

        // Weather info display
        GridPane.setConstraints(weatherIcon, 1, 3);
        Label weatherLabel = new Label("Weather Info:");
        GridPane.setConstraints(weatherLabel, 0, 4);
        Label weatherInfo = new Label();
        GridPane.setConstraints(weatherInfo, 1, 4);

        // Forecast info display
        Label forecastLabel = new Label("Forecast Info:");
        GridPane.setConstraints(forecastLabel, 0, 5);
        Label forecastInfo = new Label();
        GridPane.setConstraints(forecastInfo, 1, 5);

        // Search history display
        Label historyLabel = new Label("Search History:");
        GridPane.setConstraints(historyLabel, 0, 6);
        Label historyInfo = new Label();
        GridPane.setConstraints(historyInfo, 1, 6);

        // Set action for fetch button
        fetchButton.setOnAction(e -> {
            String location = locationInput.getText();
            JSONObject weatherData = WeatherAPI.getWeatherData(location);
            JSONObject forecastData = WeatherAPI.getForecastData(location);
            if (weatherData != null && forecastData != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                searchHistory.add(location + " - " + LocalDateTime.now().format(formatter));
                weatherInfo.setText(formatWeatherInfo(weatherData));
                forecastInfo.setText(formatForecastInfo(forecastData));
                historyInfo.setText(String.join("\n", searchHistory));
                updateBackground(grid, primaryStage.getWidth(), primaryStage.getHeight()); // Pass window dimensions
                updateWeatherIcon(weatherData);
            } else {
                weatherInfo.setText("Error fetching data, Please check the city name.");
            }
        });

        // Set action for toggle units button
        toggleUnitsButton.setOnAction(e -> {
            isCelsius = !isCelsius;
            isMetersPerSecond = !isMetersPerSecond;
            String location = locationInput.getText();
            JSONObject weatherData = WeatherAPI.getWeatherData(location);
            JSONObject forecastData = WeatherAPI.getForecastData(location);
            if (weatherData != null && forecastData != null) {
                weatherInfo.setText(formatWeatherInfo(weatherData));
                forecastInfo.setText(formatForecastInfo(forecastData));
            }
        });

        // Add all components to the grid
        grid.getChildren().addAll(locationLabel, locationInput, weatherLabel, weatherInfo, forecastLabel, forecastInfo, fetchButton, toggleUnitsButton, historyLabel, historyInfo, weatherIcon);

        // Setup the scene and display it
        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Formats weather data into a user-readable string.
     * @param weatherData JSON object containing weather data
     * @return Formatted weather information
     */
    private String formatWeatherInfo(JSONObject weatherData) {
        StringBuilder weatherInfo = new StringBuilder();
        double temp = weatherData.getJSONObject("main").getDouble("temp");
        if (!isCelsius) {
            temp = temp * 9 / 5 + 32; // Convert to Fahrenheit if needed
        }
        temp = Math.round(temp * 100.0) / 100.0; // Round to two decimal places
        String tempUnit = isCelsius ? "°C" : "°F";

        double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");
        if (!isMetersPerSecond) {
            windSpeed = windSpeed * 2.23694; // Convert m/s to mph
        }
        windSpeed = Math.round(windSpeed * 100.0) / 100.0; // Round to two decimal places
        String speedUnit = isMetersPerSecond ? "m/s" : "mph";

        weatherInfo.append("Temperature: ").append(temp).append(tempUnit).append("\n");
        weatherInfo.append("Humidity: ").append(weatherData.getJSONObject("main").getInt("humidity")).append("%\n");
        weatherInfo.append("Wind Speed: ").append(windSpeed).append(" ").append(speedUnit).append("\n");
        weatherInfo.append("Conditions: ").append(weatherData.getJSONArray("weather").getJSONObject(0).getString("description")).append("\n");

        return weatherInfo.toString();
    }

    /**
     * Formats forecast data into a user-readable string.
     * @param forecastData JSON object containing forecast data
     * @return Formatted forecast information
     */
    private String formatForecastInfo(JSONObject forecastData) {
        StringBuilder forecastInfo = new StringBuilder();
        JSONArray list = forecastData.getJSONArray("list");
        for (int i = 0; i < 5; i++) { // Displaying 5 forecast entries
            JSONObject entry = list.getJSONObject(i);
            double temp = entry.getJSONObject("main").getDouble("temp");
            if (!isCelsius) {
                temp = temp * 9 / 5 + 32; // Convert to Fahrenheit if needed
            }
            temp = Math.round(temp * 100.0) / 100.0; // Round to two decimal places
            String tempUnit = isCelsius ? "°C" : "°F";

            forecastInfo.append("Forecast ").append(i + 1).append(": ").append(temp).append(tempUnit).append(", ");
            forecastInfo.append(entry.getJSONArray("weather").getJSONObject(0).getString("description")).append("\n");
        }
        return forecastInfo.toString();
    }

    /**
     * Updates the background image of the grid based on the time of day.
     * @param grid The GridPane to update
     * @param windowWidth Width of the window
     * @param windowHeight Height of the window
     */
    private void updateBackground(GridPane grid, double windowWidth, double windowHeight) {
        int hour = java.time.LocalTime.now().getHour();
        String imagePath;
        if (hour >= 6 && hour < 12) {
            imagePath = "morning.jpg";
        } else if (hour >= 12 && hour < 18) {
            imagePath = "afternoon.jpg";
        } else if (hour >= 18 && hour < 21) {
            imagePath = "evening.jpg";
        } else {
            imagePath = "night.jpg";
        }

        BackgroundImage backgroundImage = new BackgroundImage(
                new javafx.scene.image.Image(imagePath, windowWidth, windowHeight, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        grid.setBackground(new Background(backgroundImage));
    }

    /**
     * Updates the weather icon based on the weather data.
     * @param weatherData JSON object containing weather data
     */
    private void updateWeatherIcon(JSONObject weatherData) {
        String iconCode = weatherData.getJSONArray("weather").getJSONObject(0).getString("icon");
        String iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";
        Image iconImage = new Image(iconUrl);
        weatherIcon.setImage(iconImage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
