/*
 * Copyright 2016 Yetamine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.yetamine.lang.functional;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A utility class providing several {@link Consumer} implementations.
 */
public final class Consumers {

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
