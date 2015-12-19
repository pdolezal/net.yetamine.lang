package net.yetamine.lang.functional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A utility interface for extending a use of the {@link Optional} class.
 *
 * <p>
 * This interface is designed specifically for the pattern when an operation
 * must be executed as an alternative to consuming an {@link Optional} value:
 *
 * <pre>
 * if (Conditional.ifAbsent(stream.findAny(), value -&gt; {
 *     // Consume the optional value as the conditional action
 * })) {
 *     // Perform an alternative action
 * }
 * </pre>
 *
 * @param <T>
 *            the type of the contained value
 */
public interface Conditional<T> {

    /**
     * Provides an argument to accept by the specified consumer if available.
     *
     * @param consumer
     *            the consumer to use. It must not be {@code null}.
     *
     * @return {@code false} iff the value for the consumer was invoked and
     *         finished successfully
     */
    boolean absent(Consumer<? super T> consumer);

    /**
     * Invokes conditionally a consumer with the value provided by an
     * {@link Optional} instance.
     *
     * @param <T>
     *            the type of the accepted value
     * @param value
     *            the {@link Optional} instance to process. It must not be
     *            {@code null}.
     * @param consumer
     *            the consumer to use. It must not be {@code null}.
     *
     * @return {@code false} iff the value for the consumer was invoked and
     *         finished successfully
     */
    static <T> boolean ifAbsent(Optional<T> value, Consumer<? super T> consumer) {
        if (value.isPresent()) {
            consumer.accept(value.get());
            return false;
        }

        Objects.requireNonNull(consumer);
        return true;
    }

    /**
     * Makes an instance bound to an {@link Optional} instance to the
     * {@link #ifAbsent(Optional, Consumer)} method.
     *
     * @param <T>
     *            the type of the accepted value
     * @param value
     *            the value to bind. It must not be {@code null}.
     *
     * @return an instance bound to an {@link Optional} instance
     */
    static <T> Conditional<T> when(Optional<? extends T> value) {
        Objects.requireNonNull(value);
        return consumer -> ifAbsent(value, consumer);
    }
}
