package me.cylorun.pace.rpc;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.cylorun.pace.PaceStatusOptions;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;


public class DiscordStatus {
    private String cliendId;
    DiscordUser user;

    public DiscordStatus(String clientId) {
        this.cliendId = clientId;
    }

    public void startup() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> System.out.println(user.username)).build();
        DiscordRPC.discordInitialize(this.cliendId, handlers, true);
    }

    public void updatePresence() {
        DiscordRPC.discordUpdatePresence(getNewPresence());
    }

    public DiscordRichPresence getNewPresence() {
        PaceStatusOptions options = PaceStatusOptions.getInstance();
        JsonObject run = PaceMan.getCurrentRun(options.username.toLowerCase());
        if (run != null) {
            JsonArray eventList = run.getAsJsonArray("eventList");
            JsonObject latestEvent = eventList.get(eventList.size() - 1).getAsJsonObject();
            String currentSplit = latestEvent.get("eventId").getAsString();
            String currentTime = PaceMan.formatTime(Integer.parseInt(latestEvent.get("igt").getAsString()));
            return new DiscordRichPresence.Builder("Current Time " + currentTime)
                    .setDetails(PaceMan.getRunDesc(currentSplit))
                    .setBigImage(PaceMan.getIcon(currentSplit), null)
                    .setSmallImage("app_icon", null)
                    .build();
        }
        return new DiscordRichPresence.Builder("Not on pace").setBigImage("idle", null).build();
    }
}

