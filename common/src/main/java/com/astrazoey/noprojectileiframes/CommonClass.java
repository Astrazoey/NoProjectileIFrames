package com.astrazoey.noprojectileiframes;

/**
 * Common initialization logic shared across all loaders.
 * <p>
 * On Fabric, this is called from the {@code ModInitializer} entrypoint.
 * On NeoForge, this is called statically from the {@code RegisterEvent} handler.
 */
public class CommonClass {

    private static NoProjectileIFramesConfig config;

    /**
     * Runs the loader-agnostic initialization.
     * Each loader calls this from its own entrypoint.
     */
    public static void init() {
        Constants.LOG.info("Initializing {}", Constants.MOD_NAME);
        config = NoProjectileIFramesConfig.load();
    }

    public static NoProjectileIFramesConfig getConfig() {
        return config;
    }
}
