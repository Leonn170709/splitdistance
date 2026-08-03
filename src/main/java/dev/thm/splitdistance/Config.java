package dev.thm.splitdistance;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * ponytail: java.util.Properties and a flat file. No config library -- the screen is built
 * from vanilla OptionInstance widgets, so there is nothing for Cloth Config to do here.
 */
public final class Config {

    public static final int MAX_RENDER_CHUNKS = 32;

    private static final int DEFAULT_RENDER_CHUNKS = 12;
    private static final boolean DEFAULT_THREAD_GUARD = true;

    private static int renderChunks = DEFAULT_RENDER_CHUNKS;
    private static boolean threadGuard = DEFAULT_THREAD_GUARD;
    private static boolean loaded = false;

    private Config() {}

    public static int renderChunks() {
        ensureLoaded();
        return renderChunks;
    }

    public static boolean threadGuard() {
        ensureLoaded();
        return threadGuard;
    }

    /**
     * Applies and persists in one go. Called when the config screen closes.
     *
     * @return true if anything actually changed, so the caller knows whether to rebuild chunks
     */
    public static synchronized boolean set(int newRenderChunks, boolean newThreadGuard) {
        ensureLoaded();
        if (newRenderChunks == renderChunks && newThreadGuard == threadGuard) return false;
        renderChunks = newRenderChunks;
        threadGuard = newThreadGuard;
        save();
        return true;
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("splitdistance.properties");
    }

    private static synchronized void ensureLoaded() {
        if (loaded) return;
        loaded = true;

        Path file = file();
        if (!Files.exists(file)) {
            save();
            return;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("[splitdistance] could not read config, using defaults: " + e);
            return;
        }
        renderChunks = parseInt(props.getProperty("renderChunks"), DEFAULT_RENDER_CHUNKS);
        threadGuard = parseBool(props.getProperty("threadGuard"), DEFAULT_THREAD_GUARD);
    }

    private static void save() {
        Properties props = new Properties();
        props.setProperty("renderChunks", String.valueOf(renderChunks));
        props.setProperty("threadGuard", String.valueOf(threadGuard));
        Path file = file();
        try {
            Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                props.store(out, """
                        Split Distance
                        renderChunks - chunks actually rendered. Set your in-game render distance HIGH (e.g. 32);
                                       the server sends that much, map mods index it, this caps what gets drawn.
                                       0 disables the mod.
                        threadGuard  - return the uncapped distance to callers outside the render thread, so map
                                       mods that query render distance still see the full radius. Turn off if a
                                       mod misbehaves.""");
            }
        } catch (IOException e) {
            System.err.println("[splitdistance] could not write config: " + e);
        }
    }

    private static int parseInt(String s, int fallback) {
        try {
            return s == null ? fallback : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static boolean parseBool(String s, boolean fallback) {
        return s == null ? fallback : Boolean.parseBoolean(s.trim());
    }
}
