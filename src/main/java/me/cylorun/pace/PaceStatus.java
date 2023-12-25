package me.cylorun.pace;

import com.google.common.io.Resources;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.julti.Julti;
import xyz.duncanruns.julti.JultiAppLaunch;
import xyz.duncanruns.julti.gui.JultiGUI;
import xyz.duncanruns.julti.plugin.PluginEvents;
import xyz.duncanruns.julti.plugin.PluginInitializer;
import xyz.duncanruns.julti.plugin.PluginManager;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

public class PaceStatus implements PluginInitializer {
    public static void main(String[] args) throws IOException {
        JultiAppLaunch.launchWithDevPlugin(args, PluginManager.JultiPluginData.fromString(
                Resources.toString(Resources.getResource(PaceStatus.class, "/julti.plugin.json"), Charset.defaultCharset())
        ), new PaceStatus());
    }

    @Override
    public void initialize() {
        Julti.log(Level.INFO, "Pace-Status plugin initialized");
        DiscordStatus discord = new DiscordStatus("1188623641513050224");
        discord.startup();
        AtomicLong timeTracker = new AtomicLong(System.currentTimeMillis());

        PluginEvents.RunnableEventType.END_TICK.register(() -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - timeTracker.get() > 5000) {
                discord.updatePresence();
                timeTracker.set(currentTime);
            }
        });

        PluginEvents.RunnableEventType.STOP.register(() -> {
            Julti.log(Level.INFO, "Pace-Status plugin shutting down...");
        });
    }

    @Override
    public void onMenuButtonPress() {
        JOptionPane.showMessageDialog(JultiGUI.getPluginsGUI(), "Holy moly! You pressed the example plugin button!!!", "Jojulti Multi Instance Macro Example Plugin Button.", JOptionPane.INFORMATION_MESSAGE);
    }
}
