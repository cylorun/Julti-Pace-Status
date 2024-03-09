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
import xyz.duncanruns.julti.util.ExceptionUtil;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PaceStatus implements PluginInitializer {
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
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

        AtomicInteger errorCounter = new AtomicInteger();
        EXECUTOR.scheduleWithFixedDelay(() -> {
            try {
                if (options.enabled) ds.updatePresence();
                else DiscordRPC.discordClearPresence();
                errorCounter.set(0);
            } catch (Throwable t) {
                if (errorCounter.incrementAndGet() > 10) {
                    Julti.log(Level.ERROR, "Pace Status Error: " + ExceptionUtil.toDetailedString(t));
                }
            }
        }, 1, 10, TimeUnit.SECONDS);

        PluginEvents.RunnableEventType.STOP.register(() -> {
            Julti.log(Level.INFO, "Pace-Status plugin shutting down...");
        });
    }

    @Override
    public void onMenuButtonPress() {
        PaceStatusGUI.open(null);
    }
}
