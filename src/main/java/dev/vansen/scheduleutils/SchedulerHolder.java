package dev.vansen.scheduleutils;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages the plugin instance for the {@link org.bukkit.scheduler.BukkitScheduler}.
 * <p>
 * This class is used to set and get the plugin instance that is used to schedule tasks.
 * <p>
 * The plugin instance can be set using the {@link #set(JavaPlugin)} method.
 * The plugin instance can be retrieved using the {@link #get()} method.
 */
@SuppressWarnings("unused")
public class SchedulerHolder {
    private static @Nullable JavaPlugin instance;

    /**
     * Sets the plugin instance that is used to schedule tasks.
     * <p>
     * This must be called before any tasks are scheduled.
     * <p>
     */
    public static void set(@NotNull JavaPlugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("Plugin instance already set!");
        }
        SchedulerHolder.instance = plugin;
    }


    /**
     * Gets the plugin instance that is used to schedule tasks.
     * <p>
     * This method can be called at any time, and it will return the plugin instance that was previously set.
     * <p>
     * The plugin instance is used to schedule tasks, and it is used to cancel tasks.
     *
     * @return the plugin instance that is used to schedule tasks.
     */
    public static @NotNull JavaPlugin get() {
        if (instance == null) {
            throw new IllegalStateException("Plugin instance not set!");
        }
        return instance;
    }
}
