package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaHttpClient {
    public static String logicParse(String expr) {
        StringBuilder response = new StringBuilder();
        try {
            // Define the server URL with the parameter
            String serverUrl = "http://localhost:8000/tb?param=" + expr;

            // Create a URL object
            URL url = new URL(serverUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response from the server
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Print the server's response
            } else {
                System.err.println("HTTP Request failed with response code: " + responseCode);
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public static String simplifyParse(String expr) {
        StringBuilder response = new StringBuilder();
        try {
            // Define the server URL with the parameter
            String serverUrl = "http://localhost:8000/simplify?param=" + expr;

            // Create a URL object
            URL url = new URL(serverUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response from the server
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Print the server's response
            } else {
                System.err.println("HTTP Request failed with response code: " + responseCode);
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}

