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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A generic operation interface which is a convenient extension of the common
 * {@link UnaryOperator} interface.
 *
 * @param <T>
 *            the type of the input and output of the operation
 */
@FunctionalInterface
public interface UnaryOperation<T> extends UnaryOperator<T> {

    /**
     * Returns an operation that returns the result of the given function
     * applied on the result of this operation.
     *
     * @param after
     *            the function to apply. It must not be {@code null}.
     *
     * @return the composition of the functions
     *
     * @see java.util.function.Function#andThen(Function)
     */
    default UnaryOperation<T> andNext(Function<? super T, ? extends T> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }

    /**
     * Returns an operation that executes this operation only if the given
     * predicate is satisfied for the actual argument; if the predicate is
     * unsatisfied, the operand is returned without as it is.
     *
     * @param predicate
     *            the predicate to test the argument. It must not be
     *            {@code null}.
     *
     * @return a consumer guarded by the predicate
     */
    default UnaryOperation<T> onlyIf(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return t -> predicate.test(t) ? apply(t) : t;
    }

    /**
     * Returns an operation that always returns its input argument.
     *
     * @param <T>
     *            the type of the input and output of the operation
     *
     * @return a unary operation that always returns its input argument
     */
    static <T> UnaryOperation<T> identity() {
        return t -> t;
    }

    /**
     * Returns an operation that applies, in sequence, all given functions as if
     * they were composed, passing the result of the previous one as the
     * argument to the next.
     *
     * <p>
     * This method does not make any copy of the input, therefore the caller may
     * provide a dynamic underlying sequence, but on the other hand, the caller
     * is responsible for thread safety of the sequence, so that another thread
     * may iterate through the sequence, having a consistent snapshot.
     *
     * @param <T>
     *            the type of the input and output of the operation
     * @param sequence
     *            the sequence of the consumers to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a consumer that applies, in sequence, all given consumers
     */
    static <T> UnaryOperation<T> sequential(Iterable<? extends Function<? super T, ? extends T>> sequence) {
        final UnaryOperator<T> result = UnaryOperators.sequential(sequence);
        return result::apply;
    }
}
