package me.cylorun.pace;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class PaceMan {

    public static String requestData() {
        String apiUrl = "https://paceman.gg/api/ars/liveruns";
        StringBuilder response = null;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(response).toString();
    }

    public static JsonObject getCurrentRun(String runnerName) {
        JsonArray ja = JsonParser.parseString(PaceMan.requestData()).getAsJsonArray();
        for (JsonElement run : ja) {
            JsonObject obj = run.getAsJsonObject();
            String currentRunner = obj.get("user").getAsJsonObject().get("liveAccount").getAsString();
            if (currentRunner.isEmpty()) {
                currentRunner = obj.get("nickname").getAsString();

            }
            if (currentRunner.toLowerCase().equals(runnerName)) {
                System.out.println("Run from " + runnerName);
                return obj;
            }
            System.out.println("no run to track from " + runnerName);

        }
        return null;
    }

    public static String formatTime(int ms) {
        int seconds = ms / 1000;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%d:%02d", minutes, remainingSeconds);
    }

}
