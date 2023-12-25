package me.cylorun.pace;

import com.google.common.io.Resources;
import me.cylorun.pace.rpc.DiscordStatus;
import me.cylorun.pace.ui.PaceStatusGUI;
import net.arikia.dev.drpc.DiscordRPC;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.julti.Julti;
import xyz.duncanruns.julti.JultiAppLaunch;
import xyz.duncanruns.julti.plugin.PluginEvents;
import xyz.duncanruns.julti.plugin.PluginInitializer;
import xyz.duncanruns.julti.plugin.PluginManager;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

public class PaceStatus implements PluginInitializer {
    private final String CLIENT_ID = "1188623641513050224";

    public static void main(String[] args) throws IOException {
        JultiAppLaunch.launchWithDevPlugin(args, PluginManager.JultiPluginData.fromString(Resources.toString(Resources.getResource(PaceStatus.class, "/julti.plugin.json"), Charset.defaultCharset())), new PaceStatus());
    }

    @Override
    public void initialize() {
        Julti.log(Level.INFO, "Pace-Status plugin initialized");
        PaceStatusOptions options;
        try {
            options = PaceStatusOptions.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DiscordStatus ds = new DiscordStatus(CLIENT_ID);
        ds.startup();

        AtomicLong timeTracker = new AtomicLong(System.currentTimeMillis());
        PluginEvents.RunnableEventType.END_TICK.register(() -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - timeTracker.get() > 10000) {
                if (options.enabled) ds.updatePresence();
                else DiscordRPC.discordClearPresence();
                timeTracker.set(currentTime);
            }
        });

        PluginEvents.RunnableEventType.STOP.register(() -> {
            Julti.log(Level.INFO, "Pace-Status plugin shutting down...");
        });
    }

    @Override
    public void onMenuButtonPress() {
        PaceStatusGUI.open(null);
    }
}
