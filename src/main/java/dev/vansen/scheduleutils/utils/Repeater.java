package dev.vansen.scheduleutils.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vansen.scheduleutils.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Repeater allows execution of a task repeatedly with a specified delay and period.
 */
@SuppressWarnings("unused")
public final class Repeater {
    private static final Map<String, Task> asyncTasks = new HashMap<>();
    private static final Map<String, Task> syncTasks = new HashMap<>();
    private final boolean async;
    private Runnable task;
    private long delay;
    private long period;
    private long totalDuration;
    private boolean repeatsForever;
    private String uniqueId;

    public Repeater(boolean async) {
        this.async = async;
        this.repeatsForever = false;
    }

    /**
     * Sets the task to be executed.
     *
     * @param task The task to run.
     * @return The current Repeater instance.
     */
    @CanIgnoreReturnValue
    public Repeater task(@NotNull Runnable task) {
        this.task = task;
        return this;
    }

    /**
     * Sets the initial delay before the task is executed.
     *
     * @param delay The delay as a Duration.
     * @return The current Repeater instance.
     */
    @CanIgnoreReturnValue
    public Repeater delay(@NotNull Duration delay) {
        this.delay = delay.toMillis() / 50; // Convert to ticks
        return this;
    }

    /**
     * Sets the period between each execution of the task.
     *
     * @param period The period as a Duration.
     * @return The current Repeater instance.
     */
    @CanIgnoreReturnValue
    public Repeater repeats(@NotNull Duration period) {
        this.period = period.toMillis() / 50; // Convert to ticks
        return this;
    }

    /**
     * Sets the total duration for which the task should repeat.
     *
     * @param duration The duration as a Duration.
     * @return The current Repeater instance.
     */
    @CanIgnoreReturnValue
    public Repeater repeatsFor(@NotNull Duration duration) {
        this.totalDuration = duration.toMillis() / 50; // Convert to ticks
        return this;
    }

    /**
     * Sets whether the task should repeat indefinitely.
     *
     * @param repeatsForever If true, the task repeats indefinitely.
     * @return The current Repeater instance.
     */
    @CanIgnoreReturnValue
    public Repeater repeatsForever(boolean repeatsForever) {
        this.repeatsForever = repeatsForever;
        return this;
    }

    /**
     * Assigns a unique ID to the task, which can be used to cancel it later.
     *
     * @param uniqueId The unique ID for the task.
     * @return The current Repeater instance.
     */
    @CanIgnoreReturnValue
    public Repeater uniqueId(@NotNull String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    /**
     * Executes the task repeatedly with the specified settings.
     *
     * @return A Task instance representing the running task.
     * @throws IllegalStateException If the task is null or delay/period is invalid.
     */
    @CanIgnoreReturnValue
    public Task run() {
        return runIf(() -> true);
    }

    /**
     * Executes the task repeatedly only if the provided condition is true.
     *
     * @param condition The condition to check before running the task.
     * @return A Task instance representing the running task.
     * @throws IllegalStateException If the task is null or delay/period is invalid.
     */
    @CanIgnoreReturnValue
    public Task runIf(@NotNull BooleanSupplier condition) {
        if (task == null) throw new IllegalStateException("Task cannot be null!");
        if (delay < 0) throw new IllegalStateException("Delay must be non-negative!");
        if (period <= 0) throw new IllegalStateException("Period must be greater than 0!");

        Runnable conditionalTask = () -> {
            if (condition.getAsBoolean()) {
                task.run();
            }
        };

        BukkitTask bukkitTask;
        if (repeatsForever) {
            bukkitTask = async ?
                    Bukkit.getScheduler().runTaskTimerAsynchronously(JavaPlugin.getProvidingPlugin(SchedulerUtils.class), conditionalTask, delay, period) :
                    Bukkit.getScheduler().runTaskTimer(JavaPlugin.getProvidingPlugin(SchedulerUtils.class), conditionalTask, delay, period);
        } else {
            long repetitions = totalDuration / period;
            bukkitTask = async ?
                    Bukkit.getScheduler().runTaskTimerAsynchronously(JavaPlugin.getProvidingPlugin(SchedulerUtils.class), new RepeatingTask(conditionalTask, repetitions), delay, period) :
                    Bukkit.getScheduler().runTaskTimer(JavaPlugin.getProvidingPlugin(SchedulerUtils.class), new RepeatingTask(conditionalTask, repetitions), delay, period);
        }

        Task resultTask = new Task(bukkitTask, period);

        if (uniqueId != null) {
            if (async) {
                asyncTasks.put(uniqueId, resultTask);
            } else {
                syncTasks.put(uniqueId, resultTask);
            }
        }
        return resultTask;
    }

    /**
     * Cancels all tasks of the specified type.
     *
     * @param async If true, cancels async tasks; otherwise, cancels sync tasks.
     */
    public static void cancelAll(boolean async) {
        Map<String, Task> taskMap = async ? asyncTasks : syncTasks;
        taskMap.values().forEach(Task::cancel);
        taskMap.clear();
    }

    /**
     * Cancels a task by its unique ID.
     *
     * @param uniqueId The unique ID of the task to cancel.
     */
    public static void cancelById(@NotNull String uniqueId) {
        Task task = asyncTasks.remove(uniqueId);
        if (task == null) {
            task = syncTasks.remove(uniqueId);
        }
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Internal class to handle repeated task execution.
     */
    private static class RepeatingTask implements Runnable {
        private final Runnable task;
        private long remainingRepetitions;

        RepeatingTask(@NotNull Runnable task, long remainingRepetitions) {
            this.task = task;
            this.remainingRepetitions = remainingRepetitions;
        }

        @Override
        public void run() {
            if (remainingRepetitions > 0) {
                task.run();
                remainingRepetitions--;
            }
        }
    }
}