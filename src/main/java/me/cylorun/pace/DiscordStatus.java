package me.cylorun.pace;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;


public class DiscordStatus {
    String cliendId;
    public DiscordStatus(String clientId) {
        this.cliendId = clientId;
    }
    public void startup() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> System.out.println("Welcome " + user.username + "#" + user.discriminator + "!")).build();
        DiscordRPC.discordInitialize(this.cliendId, handlers, true);
    }
    public void updatePresence(){
        DiscordRPC.discordUpdatePresence(getNewPresence());
    }
    public DiscordRichPresence getNewPresence(){
        String runner = new String("energy9802").toLowerCase();
        JsonObject run = PaceMan.getCurrentRun(runner);
        if (run != null) {
            JsonArray eventList = run.getAsJsonArray("eventList");
            JsonObject latestEvent = eventList.get(eventList.size() - 1).getAsJsonObject();
            String currentSplit = latestEvent.get("eventId").getAsString();
            String currentTime = PaceMan.formatTime(Integer.parseInt(latestEvent.get("igt").getAsString()));
            return new DiscordRichPresence.Builder("Current Time " + currentTime).setDetails(currentSplit).build();
        }
        return new DiscordRichPresence.Builder("No run to track L").build();
    }

}

