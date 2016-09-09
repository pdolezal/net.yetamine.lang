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
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * A generic operation interface which is a convenient extension of the common
 * {@link Consumer} interface.
 *
 * @param <T>
 *            the type of the first argument of the operation
 * @param <U>
 *            the type of the second argument of the operation
 */
@FunctionalInterface
public interface BiAcceptor<T, U> extends BiConsumer<T, U> {

    /**
     * @see java.util.function.BiConsumer#andThen(java.util.function.BiConsumer)
     */
    default BiAcceptor<T, U> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);

        return (t, u) -> {
            accept(t, u);
            after.accept(t, u);
        };
    }

    /**
     * Returns a consumer that executes this consumer only if the given
     * predicate is satisfied for the actual argument.
     *
     * @param predicate
     *            the predicate to test the argument. It must not be
     *            {@code null}.
     *
     * @return a consumer guarded by the predicate
     */
    default BiAcceptor<T, U> onlyIf(BiPredicate<? super T, ? super U> predicate) {
        Objects.requireNonNull(predicate);

        return (t, u) -> {
            if (predicate.test(t, u)) {
                accept(t, u);
            }
        };
    }

    /**
     * Makes an instance from the given consumer.
     *
     * <p>
     * This method is a convenient factory method for adapting a consumer into
     * this smarter interface with fluent chaining with no casting-like steps,
     * or intermediate variables:
     *
     * <pre>
     * BiAcceptor.from(MyUtilities::someOperation).onlyIf(MyUtilities::someTest)
     * </pre>
     *
     * @param <T>
     *            the type of the first argument of the operation
     * @param <U>
     *            the type of the second argument of the operation
     * @param consumer
     *            the consumer to adapt. It must not be {@code null}.
     *
     * @return the adapted consumer
     */
    static <T, U> BiAcceptor<T, U> from(BiConsumer<? super T, ? super U> consumer) {
        return consumer::accept;
    }

    /**
     * Returns a nothing-doing instance.
     *
     * @param <T>
     *            the type of the first argument of the operation
     * @param <U>
     *            the type of the second argument of the operation
     *
     * @return the nothing-doing instance
     */
    static <T, U> BiAcceptor<T, U> nil() {
        return NoOperation::execute;
    }

    /**
     * Returns an instance that applies, in sequence, all given consumers.
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
     *            the type of the first argument of the operation
     * @param <U>
     *            the type of the second argument of the operation
     * @param sequence
     *            the sequence of the consumers to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a consumer that applies, in sequence, all given consumers
     */
    static <T, U> BiAcceptor<T, U> sequential(Iterable<? extends BiConsumer<? super T, ? super U>> sequence) {
        Objects.requireNonNull(sequence);

        return (t, u) -> {
            for (BiConsumer<? super T, ? super U> consumer : sequence) {
                consumer.accept(t, u);
            }
        };
    }
}
