package me.cylorun.pace.rpc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.cylorun.pace.PaceStatusOptions;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.julti.Julti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PaceMan {

    private static Pair<String, Integer> getURL(URL url) {
        StringBuilder response = null;
        int code = 400;
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            code = conn.getResponseCode();
            if (code != 200) {
                return Pair.of(null, code);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
        } catch (Exception e) {
            Julti.log(Level.ERROR, "(Pace-Status) Failed to fetch run data: " + e.toString());
        }

        return Pair.of(response == null ? null : response.toString(), code);
    }


    public static JsonObject getRun(String searchRunner) {
        String apiUrl = "https://paceman.gg/api/ars/liveruns";
        String paceData;
        try {
            paceData = getURL(new URL(apiUrl)).getLeft();
        } catch (MalformedURLException | NullPointerException e) {
            Julti.log(Level.ERROR, "(Pace-Status) Failed to fetch data from paceman");
            return null;
        }

        if (paceData != null) {
            JsonArray ja = JsonParser.parseString(paceData).getAsJsonArray();
            for (JsonElement runElement : ja) {
                JsonObject run = runElement.getAsJsonObject();
                String runnerNick = run.get("nickname").getAsString();
                if (runnerNick.toLowerCase().equals(searchRunner)) {
                    return run;
                }

            }
        }
        return null;
    }

    private static URL getPaceSatsURL() throws MalformedURLException {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        return new URL(String.format("https://paceman.gg/stats/api/getSessionNethers/?name=%s&hours=%s&hoursBetween=2", options.username, options.time_period));
    }

    public static Pair<Integer, String> getEnterStats(String runnerName) throws IOException {
        URL url = getPaceSatsURL();
        Pair<String, Integer> apiRes = getURL(url);

        if (apiRes.getRight() == 404) {
            Julti.log(Level.WARN, "(Pace-Status) Unknown username " + runnerName);
            return null;
        }

        if (apiRes.getLeft() == null) {
            return null;
        }

        JsonElement e = JsonParser.parseString(apiRes.getLeft());
        if (e != null) {
            JsonObject o = e.getAsJsonObject();
            int count = o.get("count").getAsInt();
            String avg = o.get("avg").getAsString();

            return Pair.of(count, avg);
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
