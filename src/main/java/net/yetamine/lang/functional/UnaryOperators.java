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
import java.util.function.UnaryOperator;

/**
 * A utility class providing several {@link UnaryOperator} implementations.
 */
public final class UnaryOperators {

    /**
     * Returns an operator consumer that applies, in sequence, all given
     * functions and returns the result (i.e., an empty sequence is the identity
     * operator).
     *
     * <p>
     * This method does not make any copy of the input, therefore the caller may
     * provide a dynamic underlying sequence, but on the other hand, the caller
     * is responsible for thread safety of the sequence, so that another thread
     * may iterate through the sequence, having a consistent snapshot.
     *
     * <p>
     * This method may be useful in the cases of a dynamic chain or when simply
     * the sequence is long and chaining composition causes too deep call
     * nesting.
     *
     * @param <T>
     *            the type of the accepted parameter
     * @param sequence
     *            the sequence of the functions to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return an operator that applies, in sequence, all given functions
     */
    public static <T> UnaryOperator<T> sequential(Iterable<? extends Function<T, T>> sequence) {
        Objects.requireNonNull(sequence);

        return t -> {
            T result = t;

            for (Function<T, T> function : sequence) {
                result = function.apply(result);
            }

            return result;
        };
    }

    private UnaryOperators() {
        throw new AssertionError();
    }
}
