package dev.vansen.scheduleutils.utils;

import org.bukkit.scheduler.BukkitTask;

/**
 * Task encapsulates a BukkitTask and provides additional control and information.
 */
@SuppressWarnings("unused")
public final class Task {
    private final BukkitTask bukkitTask;
    private final long repeatPeriodMillis;

    Task(BukkitTask bukkitTask) {
        this(bukkitTask, 0);
    }

    Task(BukkitTask bukkitTask, long repeatPeriodMillis) {
        this.bukkitTask = bukkitTask;
        this.repeatPeriodMillis = repeatPeriodMillis;
    }

    /**
     * Cancels the task.
     */
    public void cancel() {
        bukkitTask.cancel();
    }

    /**
     * Returns the repeat period in milliseconds.
     *
     * @return The repeat period in milliseconds, or 0 if the task does not repeat.
     */
    public long getRepeatPeriodMillis() {
        return repeatPeriodMillis;
    }

    /**
     * Checks if the task is repeating.
     *
     * @return True if the task is repeating; false otherwise.
     */
    public boolean isRepeating() {
        return repeatPeriodMillis > 0;
    }

    /**
     * Checks if the task is cancelled.
     *
     * @return True if the task is cancelled; false otherwise.
     */
    public boolean isCancelled() {
        return bukkitTask.isCancelled();
    }
}