package net.yetamine.lang.concurrent;

/**
 * Provides the cancellation capability of an operation.
 */
public interface Cancellable {

    /**
     * Requests cancellation of the task.
     *
     * @param mayInterrupt
     *            if {@code true} and the operation has been started, the
     *            operation may be interrupted in order to terminate (if
     *            possible and supported)
     *
     * @return {@code true} if the task was cancelled before starting
     */
    boolean cancel(boolean mayInterrupt);
}
