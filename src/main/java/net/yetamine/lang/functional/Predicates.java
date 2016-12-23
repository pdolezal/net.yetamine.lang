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
import java.util.function.Predicate;

/**
 * A utility class providing several {@link Predicate} implementations.
 */
public final class Predicates {

    /**
     * Makes an instance from the given predicate.
     *
     * <p>
     * This method is a convenient factory method for fluent making a predicate:
     *
     * <pre>
     * Predicates.from(MyUtilities::someTest).negate()
     * </pre>
     *
     * @param <T>
     *            the type of the parameter
     * @param predicate
     *            the predicate to return. It must not be {@code null}.
     *
     * @return the predicate
     */
    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> from(Predicate<? super T> predicate) {
        return (Predicate<T>) predicate;
    }

    /**
     * Returns a predicate that yields always {@code false}.
     *
     * @param <T>
     *            the type of the parameter
     *
     * @return the predicate
     */
    public static <T> Predicate<T> alwaysFalse() {
        return t -> false;
    }

    /**
     * Returns a predicate that yields always {@code true}.
     *
     * @param <T>
     *            the type of the parameter
     *
     * @return the predicate
     */
    public static <T> Predicate<T> alwaysTrue() {
        return t -> true;
    }

    /**
     * Returns a predicate that computes a conjunction of all given predicates;
     * the computation uses short-circuit evaluation.
     *
     * <p>
     * This method does not make any copy of the input, therefore the caller may
     * provide a dynamic underlying sequence, but on the other hand, the caller
     * is responsible for thread safety of the sequence, so that another thread
     * may iterate through the sequence, having a consistent snapshot.
     *
     * <p>
     * This method may be useful in the cases of a dynamic chain or when simply
     * the sequence is long and chaining the predicates causes too deep call
     * nesting.
     *
     * @param <T>
     *            the type of the parameter
     * @param sequence
     *            the sequence of the predicates to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a predicate that computes a conjunction of all given predicates
     */
    public static <T> Predicate<T> allOf(Iterable<? extends Predicate<? super T>> sequence) {
        Objects.requireNonNull(sequence);

        return t -> {
            for (Predicate<? super T> predicate : sequence) {
                if (!predicate.test(t)) {
                    return false;
                }
            }

            return true;
        };
    }

    /**
     * Returns a predicate that computes a dijunction of all given predicates;
     * the computation uses short-circuit evaluation.
     *
     * <p>
     * This method does not make any copy of the input, therefore the caller may
     * provide a dynamic underlying sequence, but on the other hand, the caller
     * is responsible for thread safety of the sequence, so that another thread
     * may iterate through the sequence, having a consistent snapshot.
     *
     * <p>
     * This method may be useful in the cases of a dynamic chain or when simply
     * the sequence is long and chaining the predicates causes too deep call
     * nesting.
     *
     * @param <T>
     *            the type of the parameter
     * @param sequence
     *            the sequence of the predicates to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a predicate that computes a disjunction of all given predicates
     */
    public static <T> Predicate<T> noneOf(Iterable<? extends Predicate<? super T>> sequence) {
        Objects.requireNonNull(sequence);

        return t -> {
            for (Predicate<? super T> predicate : sequence) {
                if (predicate.test(t)) {
                    return false;
                }
            }

            return true;
        };
    }

    /**
     * Returns a predicate that computes a dijunction of all given predicates;
     * the computation uses short-circuit evaluation.
     *
     * <p>
     * This method does not make any copy of the input, therefore the caller may
     * provide a dynamic underlying sequence, but on the other hand, the caller
     * is responsible for thread safety of the sequence, so that another thread
     * may iterate through the sequence, having a consistent snapshot.
     *
     * <p>
     * This method may be useful in the cases of a dynamic chain or when simply
     * the sequence is long and chaining the predicates causes too deep call
     * nesting.
     *
     * @param <T>
     *            the type of the parameter
     * @param sequence
     *            the sequence of the predicates to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a predicate that computes a disjunction of all given predicates
     */
    public static <T> Predicate<T> anyOf(Iterable<? extends Predicate<? super T>> sequence) {
        Objects.requireNonNull(sequence);

        return t -> {
            for (Predicate<? super T> predicate : sequence) {
                if (predicate.test(t)) {
                    return true;
                }
            }

            return false;
        };
    }

    private Predicates() {
        throw new AssertionError();
    }
}
