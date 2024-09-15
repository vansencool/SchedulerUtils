package dev.vansen.scheduleutils.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Canceller allows cancellation of scheduled tasks.
 */
@SuppressWarnings("unused")
public final class Canceller {
    private final Boolean async;

    /**
     * Constructs a new Canceller instance.
     *
     * @param async Whether the task should be executed asynchronously.
     */
    public Canceller(boolean async) {
        this.async = async;
    }

    /**
     * Cancels all scheduled tasks of the specified type.
     */
    public void cancel() {
        if (async) {
            Repeater.cancelAll(true);
            Later.cancelAll(true);
        } else {
            Repeater.cancelAll(false);
            Later.cancelAll(false);
        }
    }

    /**
     * Cancels a task by its unique ID.
     *
     * @param uniqueId The unique ID of the task to cancel.
     */
    public void cancel(@NotNull String uniqueId) {
        Repeater.cancelById(uniqueId);
        Later.cancelById(uniqueId);
    }
}