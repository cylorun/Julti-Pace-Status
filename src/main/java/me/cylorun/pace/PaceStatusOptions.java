package me.cylorun.pace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PaceStatusOptions {
    public String username = "";
    public boolean enabled = true;
    public boolean show_enter_count = true;
    public boolean show_enter_avg = true;
    public int time_period = 24;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SAVE_PATH = Paths.get(System.getProperty("user.home")).resolve(".Julti").resolve("pacestatus_options.json").toAbsolutePath();
    private static PaceStatusOptions instance;
    public static void save() throws IOException {
        PaceStatusOptions.ensurePaceManDir();
        FileWriter writer = new FileWriter(SAVE_PATH.toFile());
        GSON.toJson(instance, writer);
        writer.close();
    }

    public static PaceStatusOptions load() throws IOException {
        if (Files.exists(SAVE_PATH)) {
            instance = GSON.fromJson(new String(Files.readAllBytes(SAVE_PATH)), PaceStatusOptions.class);
        } else {
            instance = new PaceStatusOptions();
        }
        return instance;
    }

    public static PaceStatusOptions getInstance() {
        return instance;
    }

    public static void ensurePaceManDir() {
        new File((System.getProperty("user.home") + "/.PaceMan/").replace("\\", "/").replace("//", "/")).mkdirs();
    }
}

