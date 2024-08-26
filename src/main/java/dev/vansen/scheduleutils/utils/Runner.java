package dev.vansen.scheduleutils.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vansen.scheduleutils.SchedulerHolder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Runner allows immediate execution of a task.
 */
@SuppressWarnings("unused")
public final class Runner {
    private final boolean async;
    private Runnable task;

    public Runner(boolean async) {
        this.async = async;
    }

    /**
     * Sets the task to be executed.
     *
     * @param task The task to run.
     * @return The current Runner instance.
     */
    @CanIgnoreReturnValue
    public Runner task(@NotNull Runnable task) {
        this.task = task;
        return this;
    }

    /**
     * Executes the task immediately.
     *
     * @return A Task instance representing the running task.
     * @throws IllegalStateException If the task is null.
     */
    @CanIgnoreReturnValue
    public Task run() {
        if (task == null) throw new IllegalStateException("Task cannot be null!");
        BukkitTask bukkitTask = async ?
                Bukkit.getScheduler().runTaskAsynchronously(SchedulerHolder.get(), task) :
                Bukkit.getScheduler().runTask(SchedulerHolder.get(), task);
        return new Task(bukkitTask);
    }
}