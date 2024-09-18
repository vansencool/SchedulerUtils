package dev.vansen.scheduleutils.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vansen.scheduleutils.SchedulerHolder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Later allows execution of a task after a specified delay.
 */
@SuppressWarnings("unused")
public final class Later {
    private static final Map<String, Task> asyncTasks = new HashMap<>();
    private static final Map<String, Task> syncTasks = new HashMap<>();
    private final boolean async;
    private Runnable task;
    private long delay;
    private String uniqueId;

    /**
     * Constructs a new Later instance.
     *
     * @param async Whether the task should be executed asynchronously.
     */
    public Later(boolean async) {
        this.async = async;
    }

    /**
     * Sets the task to be executed.
     *
     * @param task The task to run.
     * @return The current Later instance.
     */
    @CanIgnoreReturnValue
    public Later task(@NotNull Runnable task) {
        this.task = task;
        return this;
    }

    /**
     * Sets the delay before the task is executed.
     *
     * @param delay The delay as a Duration.
     * @return The current Later instance.
     */
    @CanIgnoreReturnValue
    public Later delay(@NotNull Duration delay) {
        this.delay = delay.toMillis() / 50; // Convert to ticks
        return this;
    }

    /**
     * Assigns a unique ID to the task, which can be used to cancel it later.
     *
     * @param uniqueId The unique ID for the task.
     * @return The current Later instance.
     */
    @CanIgnoreReturnValue
    public Later uniqueId(@NotNull String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    /**
     * Executes the task after the specified delay.
     *
     * @return A Task instance representing the running task.
     * @throws IllegalStateException If the task is null or delay is invalid.
     */
    @CanIgnoreReturnValue
    public Task run() {
        return runIf(() -> true);
    }

    /**
     * Executes the task only if the provided condition is true.
     *
     * @param condition The condition to check before running the task.
     * @return A Task instance representing the running task.
     * @throws IllegalStateException If the task is null or delay is invalid.
     */
    @CanIgnoreReturnValue
    public Task runIf(@NotNull BooleanSupplier condition) {
        if (task == null) throw new IllegalStateException("Task cannot be null!");
        if (delay < 0) throw new IllegalStateException("Delay must be non-negative!");

        Runnable conditionalTask = () -> {
            if (condition.getAsBoolean()) {
                task.run();
            }
        };

        BukkitTask bukkitTask = async ?
                Bukkit.getScheduler().runTaskLaterAsynchronously(SchedulerHolder.get(), conditionalTask, delay) :
                Bukkit.getScheduler().runTaskLater(SchedulerHolder.get(), conditionalTask, delay);

        Task resultTask = new Task(bukkitTask, delay);

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
     * Checks if a task with the specified unique ID exists.
     *
     * @param uniqueId The unique ID of the task to check.
     * @return True if the task exists, false otherwise.
     */
    public static boolean exists(@NotNull String uniqueId) {
        return asyncTasks.containsKey(uniqueId) || syncTasks.containsKey(uniqueId);
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
}