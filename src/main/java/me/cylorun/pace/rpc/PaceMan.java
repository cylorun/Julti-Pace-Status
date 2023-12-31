package me.cylorun.pace.rpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.julti.Julti;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PaceMan {

    private static String requestData() {
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
        if (response == null){
            return null;
        } else {
            return response.toString();
        }
    }

    public static JsonObject getRun(String searchRunner) {
        String paceData = PaceMan.requestData();
        if (paceData != null) {
            JsonArray ja = JsonParser.parseString(paceData).getAsJsonArray();
            for (JsonElement runElement : ja) {
                JsonObject run = runElement.getAsJsonObject();
                String runnerNick = run.get("nickname").getAsString();
                if (runnerNick.toLowerCase().equals(searchRunner)) {
                    Julti.log(Level.DEBUG, "Run detected from " + searchRunner);
                    return run;
                }

            }
        }
        return null;
    }

    public static String formatTime(int ms) {
        int seconds = ms / 1000;
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    public static String getIcon(String currentSplit) {
        Map<String, String> paceIcons = new HashMap<>();
        paceIcons.put("rsg.enter_nether", "nether");
        paceIcons.put("rsg.enter_bastion", "bastion");
        paceIcons.put("rsg.enter_fortress", "fortress");
        paceIcons.put("rsg.first_portal", "blind");
        paceIcons.put("rsg.second_portal", "blind");
        paceIcons.put("rsg.enter_stronghold", "stronghold");
        paceIcons.put("rsg.enter_end", "end");
        paceIcons.put("rsg.credits", "finish");
        return paceIcons.get(currentSplit);
    }

    public static String getRunDesc(String currentSplit) {
        Map<String, String> paceDescriptions = new HashMap<>();
        paceDescriptions.put("rsg.enter_nether", "The Nether");
        paceDescriptions.put("rsg.enter_bastion", "Bastion");
        paceDescriptions.put("rsg.enter_fortress", "Fortress");
        paceDescriptions.put("rsg.first_portal", "First Portal");
        paceDescriptions.put("rsg.second_portal", "Second Portal");
        paceDescriptions.put("rsg.enter_stronghold", "Stronghold");
        paceDescriptions.put("rsg.enter_end", "The End");
        paceDescriptions.put("rsg.credits", "Finish!");
        return paceDescriptions.get(currentSplit);
    }
}
