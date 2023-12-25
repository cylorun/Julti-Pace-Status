package me.cylorun.pace.rpc;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.cylorun.pace.PaceStatusOptions;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;


public class DiscordStatus {
    private String cliendId;
    private long start;

    public DiscordStatus(String clientId) {
        this.cliendId = clientId;
    }

    public void startup() {
        this.start = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> System.out.println(user.username)).build();
        DiscordRPC.discordInitialize(this.cliendId, handlers, true);
        updatePresence();
    }

    public void updatePresence() {
        DiscordRPC.discordUpdatePresence(getNewPresence());
    }

    private DiscordRichPresence getNewPresence() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        JsonObject run = PaceMan.getRun(options.username.toLowerCase());
        if (run != null) {
            JsonArray eventList = run.getAsJsonArray("eventList");
            JsonObject latestEvent = eventList.get(eventList.size() - 1).getAsJsonObject();
            String currentSplit = latestEvent.get("eventId").getAsString();
            String currentTime = PaceMan.formatTime(Integer.parseInt(latestEvent.get("igt").getAsString()));
            return new DiscordRichPresence.Builder("Current Time: " + currentTime)
                    .setStartTimestamps(this.start)
                    .setDetails(PaceMan.getRunDesc(currentSplit))
                    .setBigImage(PaceMan.getIcon(currentSplit), null)
                    .setSmallImage("app_icon", "https://paceman.gg")
                    .build();
        }
        return new DiscordRichPresence.Builder("Not on pace")
                .setStartTimestamps(this.start)
                .setBigImage("idle", null)
                .setSmallImage("app_icon", "https://paceman.gg")
                .build();
    }
}

