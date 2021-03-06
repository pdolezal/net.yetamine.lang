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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A generic operation interface which is a convenient extension of the common
 * {@link Consumer} interface.
 *
 * @param <T>
 *            the type of the input to the operation
 */
@FunctionalInterface
public interface Acceptor<T> extends Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * <p>
     * This method provides a direct bridge to {@link Function}, but this
     * interface intentionally does not inherit from it, because it has a
     * different role and function composition is not well-suited for it.
     *
     * @param t
     *            the input argument
     *
     * @return the input argument
     */
    default T apply(T t) {
        accept(t);
        return t;
    }

    /**
     * @see java.util.function.Consumer#andThen(Consumer)
     */
    default Acceptor<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);

        return t -> {
            accept(t);
            after.accept(t);
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
    default Acceptor<T> onlyIf(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);

        return t -> {
            if (predicate.test(t)) {
                accept(t);
            }
        };
    }

    /**
     * Returns a function that performs, in sequence, this operation followed
     * applying the specified mapping function on the argument.
     *
     * <p>
     * To turn this instance in an usual {@link Function} instance without any
     * change of the result, use {@link Function#identity()} as the argument.
     *
     * @param <V>
     *            the type of the mapping result
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return the composed function
     */
    default <V> Function<T, V> finish(Function<? super T, ? extends V> mapping) {
        Objects.requireNonNull(mapping);
        return t -> mapping.apply(apply(t));
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
     * Acceptor.from(MyUtilities::someOperation).onlyIf(MyUtilities::someTest)
     * </pre>
     *
     * @param <T>
     *            the type of the argument
     * @param consumer
     *            the consumer to adapt. It must not be {@code null}.
     *
     * @return the adapted consumer
     */
    static <T> Acceptor<T> from(Consumer<? super T> consumer) {
        return consumer::accept;
    }

    /**
     * Returns a nothing-doing instance.
     *
     * @param <T>
     *            the type of the argument
     *
     * @return the nothing-doing instance
     */
    static <T> Acceptor<T> nil() {
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
     *            the type of the input to the operation
     * @param sequence
     *            the sequence of the consumers to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a consumer that applies, in sequence, all given consumers
     */
    static <T> Acceptor<T> sequential(Iterable<? extends Consumer<? super T>> sequence) {
        Objects.requireNonNull(sequence);

        return t -> {
            for (Consumer<? super T> consumer : sequence) {
                consumer.accept(t);
            }
        };
    }
}
