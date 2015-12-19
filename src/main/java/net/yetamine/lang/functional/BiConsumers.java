package net.yetamine.lang.functional;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * A utility class providing several {@link BiConsumer} implementations.
 */
public final class BiConsumers {

    /**
     * Provides a nothing-doing consumer.
     *
     * @param <T>
     *            the type of the first accepted parameter
     * @param <U>
     *            the type of the second accepted parameter
     *
     * @return a nothing-doing consumer
     */
    public static <T, U> BiConsumer<T, U> ignoring() {
        return (t, u) -> {
            // Do nothing
        };
    }

    /**
     * Returns a consumer that calls the given consumer only if the specified
     * predicate is satisfied.
     *
     * @param <T>
     *            the type of the first accepted parameter
     * @param <U>
     *            the type of the second accepted parameter
     * @param predicate
     *            the predicate to check. It must not be {@code null}.
     * @param consumer
     *            the consumer to call then. It must not be {@code null}.
     *
     * @return a consumer that calls the given consumer only if the specified
     *         predicate is satisfied
     *
     */
    public static <T, U> BiConsumer<T, U> conditional(BiPredicate<? super T, ? super U> predicate, BiConsumer<? super T, ? super U> consumer) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(consumer);

        return (u, v) -> {
            if (predicate.test(u, v)) {
                consumer.accept(u, v);
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
     * the sequence is long and chaining {@link BiConsumer#andThen(BiConsumer)}
     * causes too deep call nesting.
     *
     * @param <T>
     *            the type of the first accepted parameter
     * @param <U>
     *            the type of the second accepted parameter
     * @param sequence
     *            the sequence of the consumers to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a consumer that applies, in sequence, all given consumers
     */
    public static <T, U> BiConsumer<T, U> sequential(Iterable<? extends BiConsumer<? super T, ? super U>> sequence) {
        Objects.requireNonNull(sequence);

        return (t, u) -> {
            for (BiConsumer<? super T, ? super U> consumer : sequence) {
                consumer.accept(t, u);
            }
        };
    }

    private BiConsumers() {
        throw new AssertionError();
    }
}
