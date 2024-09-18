package dev.vansen.scheduleutils;

import dev.vansen.scheduleutils.utils.Canceller;
import dev.vansen.scheduleutils.utils.Later;
import dev.vansen.scheduleutils.utils.Repeater;
import dev.vansen.scheduleutils.utils.Runner;

/**
 * SchedulerUtils provides access to different types of schedulers for managing tasks.
 */
@SuppressWarnings("unused")
public final class SchedulerUtils {

    private final boolean async;

    private SchedulerUtils(boolean async) {
        this.async = async;
    }

    /**
     * Retrieves a SchedulerUtils instance.
     *
     * @param async If true, tasks will run asynchronously; if false, they will run synchronously.
     * @return SchedulerUtils instance.
     */
    public static SchedulerUtils get(boolean async) {
        return new SchedulerUtils(async);
    }

    /**
     * Creates a Runner for immediate task execution.
     *
     * @return A new Runner instance.
     */
    public Runner runner() {
        return new Runner(async);
    }

    /**
     * Creates a Later for delayed task execution.
     *
     * @return A new Later instance.
     */
    public Later later() {
        return new Later(async);
    }

    /**
     * Creates a Repeater for repeated task execution.
     *
     * @return A new Repeater instance.
     */
    public Repeater repeater() {
        return new Repeater(async);
    }

    /**
     * Creates a Canceller for task cancellation.
     *
     * @return A new Canceller instance.
     */
    public Canceller canceller() {
        return new Canceller(async);
    }

    /**
     * Creates a Canceller for task cancellation, only usable for sync tasks and tasks with unique ids.
     *
     * @return A new Canceller instance.
     */
    public static Canceller cancel() {
        return new Canceller(false);
    }
}
