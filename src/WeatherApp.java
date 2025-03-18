import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    //Fetch weather data for a given location
    public static JSONObject getWeatherData(String locationName) {
        //Fetch location coordinates using the geolocation API in the library
        JSONArray locationData = getLocationData(locationName);

        //Get longitude and latitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //Building API request from location coordinates
        String urlString =
                "https://api.open-meteo.com/v1/forecast?" +
                        "latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try {
            HttpURLConnection connection = fetchApiResponce(urlString);

            //Checks for response status
            //200 code means connection is a success
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

            //Retrieves data from the API hourly
            JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");


            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // Get temperature data from the api at the current index
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // Get weathercode data from the api at the current index
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // Get humidity data from the api at the current index
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // Get windspeed data from the api at the current index
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //Building the weather JSON data object that is accessing the GUI
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray getLocationData(String locationName) {
        //Replaces whitespaces with + to match the requirements from the API
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&language=en&format=json";
        try {
            HttpURLConnection connection = fetchApiResponce(urlString);
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to Api");
                return null;
            } else {
                //Stores the API result in the Stringbuilder
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                //Checks the scanner for values, and adds it to the Stringbuilder
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                connection.disconnect();

                // Parses the Json string into a JSon object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                //Gets the list of location data the API gathered from the location name
                JSONArray locationData = (JSONArray) resultsJsonObject.get("results");
                return locationData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponce(String urlString) {
        try {
            //Attempts connection with API
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Set the request method to "Get" because i am retrieving data
            connection.setRequestMethod("GET");

            //Connect to the API
            connection.connect();
            return connection;


        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connection could not be made");
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timelist) {
        String currentTime = getCurrentTime();

        for (int index = 0; index < timelist.size(); index++) {
            String time = (String) timelist.get(index);
            if (time.equalsIgnoreCase(currentTime)) {
                return index;
            }
        }
        return 0;
    }

    private static String getCurrentTime() {
        // Get current Date and time now
        LocalDateTime currentDateTime = LocalDateTime.now();

        //Formatting the date to be the same as the API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;

    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if (weatherCode <= 3L && weatherCode > 0L) {
            weatherCondition = "Cloudy";
        } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }

}
