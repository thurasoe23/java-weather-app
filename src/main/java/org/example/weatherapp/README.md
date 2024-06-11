# Weather Information App

## Overview
This is a JavaFX-based Weather Information App that provides real-time weather updates for a specified location. The app displays current weather details, including temperature, humidity, wind speed, and conditions. Users can toggle between Celsius and Fahrenheit for temperature display and view their search history.

## Features
- Fetch and display real-time weather information for a specified location
- Toggle between Celsius and Fahrenheit
- View search history with timestamps
- Dynamic background changes based on the time of day

## Setup and Usage
1. Clone the repository or download the source code.
2. Ensure you have JDK 11 or higher installed.
3. Configure your API key in the `WeatherAPI` class:
   ```java
   private static final String API_KEY = "your_api_key_here";
