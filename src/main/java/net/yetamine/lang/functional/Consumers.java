package net.yetamine.lang.functional;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A utility class providing several {@link Consumer} implementations.
 */
public final class Consumers {

    /**
     * Makes the given consumer accept the specified value.
     *
     * @param <T>
     *            the type of the value
     * @param consumer
     *            the consumer to accept the value to. If {@code null}, this
     *            method does nothing.
     * @param value
     *            the value to accept
     *
     * @return {@code true} if the consumer accepted the value
     */
    public static <T> boolean accept(Consumer<? super T> consumer, T value) {
        if (consumer == null) {
            return false;
        }

        consumer.accept(value);
        return true;
    }

    /**
     * Provides a nothing-doing consumer.
     *
     * @param <T>
     *            the type of the accepted parameter
     *
     * @return a nothing-doing consumer
     */
    public static <T> Consumer<T> ignoring() {
        return o -> {
            // Do nothing
        };
    }

    /**
     * Returns a consumer that calls the given consumer only if the specified
     * predicate is satisfied.
     *
     * @param <T>
     *            the type of the accepted parameter
     * @param predicate
     *            the predicate to check. It must not be {@code null}.
     * @param consumer
     *            the consumer to call then. It must not be {@code null}.
     *
     * @return a consumer that calls the given consumer only if the specified
     *         predicate is satisfied
     *
     */
    public static <T> Consumer<T> conditional(Predicate<? super T> predicate, Consumer<? super T> consumer) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(consumer);

        return o -> {
            if (predicate.test(o)) {
                consumer.accept(o);
            }
        };
    }

    /**
     * Returns a consumer that applies, in sequence, all given consumers.
     *
     * <p>
     * This method does not make any copy of the input, therefore the caller may
     * provide a dynamic underlying sequence, but on the other hand, the caller
     * is responsible for thread safety of the sequence, so that another thread
     * may iterate through the sequence, having a consistent snapshot.
     *
     * <p>
     * This method may be useful in the cases of a dynamic chain or when simply
     * the sequence is long and chaining {@link Consumer#andThen(Consumer)}
     * causes too deep call nesting.
     *
     * @param <T>
     *            the type of the accepted parameter
     * @param sequence
     *            the sequence of the consumers to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a consumer that applies, in sequence, all given consumers
     */
    public static <T> Consumer<T> sequential(Iterable<? extends Consumer<? super T>> sequence) {
        Objects.requireNonNull(sequence);

        return t -> {
            for (Consumer<? super T> consumer : sequence) {
                consumer.accept(t);
            }
        };
    }

    private Consumers() {
        throw new AssertionError();
    }
}
