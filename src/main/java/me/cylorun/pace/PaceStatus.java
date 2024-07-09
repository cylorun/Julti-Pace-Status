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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PaceStatus implements PluginInitializer {
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private static final String CLIENT_ID = "1188623641513050224";
    private static long lastResetTime = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {
        JultiAppLaunch.launchWithDevPlugin(args, PluginManager.JultiPluginData.fromString(Resources.toString(Resources.getResource(PaceStatus.class, "/julti.plugin.json"), Charset.defaultCharset())), new PaceStatus());
    }

    @Override
    public void initialize() {
        Julti.log(Level.INFO, "Pace-Status plugin initialized");
        PaceStatusOptions options;
        DiscordStatus ds = new DiscordStatus(CLIENT_ID);

        try {
            options = PaceStatusOptions.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        AtomicInteger errorCounter = new AtomicInteger();
        AtomicBoolean hasInitialized = new AtomicBoolean(false);
        EXECUTOR.scheduleWithFixedDelay(() -> {
            if (!hasInitialized.get()) {
                ds.init();
                hasInitialized.set(true);
            }
            try {
                if (options.enabled) {
                    ds.updatePresence();
                } else {
                    DiscordRPC.discordClearPresence();
                }
                errorCounter.set(0);
            } catch (Throwable t) {
                if (errorCounter.incrementAndGet() > 10) {
                    Julti.log(Level.ERROR, "Pace Status Error: " + ExceptionUtil.toDetailedString(t));
                }
            }
        }, 1, 10, TimeUnit.SECONDS);

        PluginEvents.InstanceEventType.RESET.register(mcInstance -> lastResetTime = System.currentTimeMillis());
    }

    public static boolean isAfk() {
        return (System.currentTimeMillis() - PaceStatus.lastResetTime) > (1000 * 300); // 5 minutes
    }

    @Override
    public void onMenuButtonPress() {
        PaceStatusGUI.open(null);
    }
}
