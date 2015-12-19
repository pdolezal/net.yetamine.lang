package net.yetamine.lang.functional;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * A utility class providing several {@link BiPredicate} implementations.
 */
public final class BiPredicates {

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
     *            the type of the first accepted parameter
     * @param <U>
     *            the type of the second accepted parameter
     * @param sequence
     *            the sequence of the predicates to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a predicate that computes a conjunction of all given predicates
     */
    public static <T, U> BiPredicate<T, U> and(Iterable<? extends BiPredicate<? super T, ? super U>> sequence) {
        Objects.requireNonNull(sequence);

        return (t, u) -> {
            for (BiPredicate<? super T, ? super U> predicate : sequence) {
                if (!predicate.test(t, u)) {
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
     *            the type of the first accepted parameter
     * @param <U>
     *            the type of the second accepted parameter
     * @param sequence
     *            the sequence of the predicates to apply. It must not be
     *            {@code null} and it must not provide {@code null} elements.
     *
     * @return a predicate that computes a disjunction of all given predicates
     */
    public static <T, U> BiPredicate<T, U> or(Iterable<? extends BiPredicate<? super T, ? super U>> sequence) {
        Objects.requireNonNull(sequence);

        return (t, u) -> {
            for (BiPredicate<? super T, ? super U> predicate : sequence) {
                if (predicate.test(t, u)) {
                    return true;
                }
            }

            return false;
        };
    }

    private BiPredicates() {
        throw new AssertionError();
    }
}
