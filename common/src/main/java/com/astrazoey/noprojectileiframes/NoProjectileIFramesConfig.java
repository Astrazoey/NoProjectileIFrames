package com.astrazoey.noprojectileiframes;

import com.astrazoey.noprojectileiframes.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Template JSON config with version tracking.
 * If the loaded config version is lower than the current version,
 * it is regenerated with defaults (unless enable_config_updates is false).
 */
public class NoProjectileIFramesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int CURRENT_VERSION = 1;

    @SerializedName("config_version")
    private int config_version = CURRENT_VERSION;

    @SerializedName("enable_config_updates")
    private boolean enable_config_updates = true;

    // Add your config fields here
    @SerializedName("projectile_i_frames")
    private int projectile_i_frames = 0;

    private static Path getConfigPath() {
        return Services.PLATFORM.getConfigDirectory().resolve(Constants.MOD_ID + ".json");
    }

    public static NoProjectileIFramesConfig load() {
        Path configPath = getConfigPath();
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                NoProjectileIFramesConfig config = GSON.fromJson(json, NoProjectileIFramesConfig.class);
                if (config == null) {
                    return createDefault();
                }
                if (config.config_version < CURRENT_VERSION && config.enable_config_updates) {
                    Constants.LOG.info("Config version {} is outdated (current {}), regenerating with defaults",
                            config.config_version, CURRENT_VERSION);
                    return createDefault();
                }
                return config;
            } catch (Exception e) {
                Constants.LOG.error("Failed to load config, using defaults", e);
                return createDefault();
            }
        } else {
            return createDefault();
        }
    }

    private static NoProjectileIFramesConfig createDefault() {
        NoProjectileIFramesConfig config = new NoProjectileIFramesConfig();
        config.save();
        return config;
    }

    public void save() {
        Path configPath = getConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(this));
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }

    public int getProjectileIframes() {
        return projectile_i_frames;
    }
}
