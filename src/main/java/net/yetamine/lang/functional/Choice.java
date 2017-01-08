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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.yetamine.lang.exceptions.ThrowingOperation;

/**
 * An alternative to {@link Optional} for the cases when the value may be right
 * or wrong, but the value itself might be used anyway, e.g., for logging. The
 * the value is marked as either <i>right</i> or <i>wrong</i> and this mark is
 * carried with the value, so that the consumer of the value can decide if and
 * how the value could be used.
 *
 * @param <T>
 *            the type of the contained value
 */
public final class Choice<T> implements Supplier<T> {

    /** Shared instance for <i>right</i> {@code null}. */
    private static final Choice<?> RIGHT_NULL = new Choice<>(null, true);
    /** Shared instance for <i>wrong</i> {@code null}. */
    private static final Choice<?> WRONG_NULL = new Choice<>(null, false);

    /** Stored value. */
    private final T value;
    /** Flag marking <i>right</i> or <i>wrong</i>. */
    private final boolean right;

    /**
     * Creates a new instance.
     *
     * @param o
     *            the value to store
     * @param t
     *            the flag marking <i>right</i> or <i>wrong</i>
     */
    private Choice(T o, boolean t) {
        value = o;
        right = t;
    }

    /**
     * Narrows a widened type performing a safe type cast (thanks to the safe
     * covariant changes for immutable types).
     *
     * @param <T>
     *            the type of the represented value
     * @param instance
     *            the instance to narrow
     *
     * @return the narrowed instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Choice<T> narrow(Choice<? extends T> instance) {
        return (Choice<T>) instance;
    }

    /**
     * Returns a representation of the given value.
     *
     * @param <T>
     *            the type of the value
     * @param value
     *            the value to store
     * @param right
     *            {@code true} when the value shall be considered <i>right</i>
     *            and {@code false} otherwise
     *
     * @return a representation of the given value
     */
    public static <T> Choice<T> of(T value, boolean right) {
        return right ? right(value) : wrong(value);
    }

    /**
     * Returns the shared instance representing a {@code null} as <i>right</i>.
     *
     * @param <T>
     *            the type of the value
     *
     * @return the shared instance representing a {@code null} as <i>right</i>
     */
    @SuppressWarnings("unchecked")
    public static <T> Choice<T> rightNull() {
        return (Choice<T>) RIGHT_NULL;
    }

    /**
     * Returns the shared instance representing a {@code null} as <i>wrong</i>.
     *
     * @param <T>
     *            the type of the value
     *
     * @return the shared instance representing a {@code null} as <i>wrong</i>
     */
    @SuppressWarnings("unchecked")
    public static <T> Choice<T> wrongNull() {
        return (Choice<T>) WRONG_NULL;
    }

    /**
     * Returns an instance representing the value as <i>right</i>.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return an instance representing the value as <i>right</i>
     */
    public static <T> Choice<T> right(T o) {
        return (o != null) ? new Choice<>(o, true) : rightNull();
    }

    /**
     * Returns an instance representing the value as <i>wrong</i>.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return an instance representing the value as <i>wrong</i>
     */
    public static <T> Choice<T> wrong(T o) {
        return (o != null) ? new Choice<>(o, false) : wrongNull();
    }

    /**
     * Returns a new instance representing the given value in the way that the
     * {@code null} value is considered <i>wrong</i>, while a non-{@code null}
     * value is considered <i>right</i>.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return the new instance
     */
    public static <T> Choice<T> nonNull(T o) {
        return (o != null) ? new Choice<>(o, true) : wrongNull();
    }

    /**
     * Returns a new instance using the given {@link Optional} in the way that a
     * present value is considered <i>right</i>, while an absent value maps to
     * <i>wrong</i> {@code null}.
     *
     * @param <T>
     *            the type of the value
     * @param optional
     *            the container of the value to represent. It must not be
     *            {@code null}.
     *
     * @return the new instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Choice<T> from(Optional<? extends T> optional) {
        return (Choice<T>) optional.map(Choice::right).orElseGet(Choice::wrongNull);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("%s[%s]", right ? "Choice.right" : "Choice.wrong", value);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Choice<?>) {
            final Choice<?> o = (Choice<?>) obj;
            return Objects.equals(value, o.value) && (right == o.right);
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, right);
    }

    /**
     * Returns the value of this choice.
     *
     * @return the value of this choice
     *
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        return value;
    }

    /**
     * Returns the value of this choice if {@link #isRight()}.
     *
     * @return the value of this choice
     *
     * @throws NoSuchElementException
     *             if this choice {@link #isWrong()}
     */
    public T require() {
        if (isRight()) {
            return value;
        }

        throw new NoSuchElementException();
    }

    /**
     * Returns the value of this choice if {@link #isRight()}.
     *
     * @param <X>
     *            the type of the exception to throw
     * @param e
     *            the supplier of the exception to throw if {@link #isWrong()}.
     *            It must not be {@code null}.
     *
     * @return the value of this choice
     *
     * @throws X
     *             if this choice {@link #isWrong()}
     */
    public <X extends Throwable> T require(Supplier<? extends X> e) throws X {
        if (isRight()) {
            return value;
        }

        throw e.get();
    }

    /**
     * Creates a stream of this instance as the only element of the stream.
     *
     * @return a stream of this instance
     */
    public Stream<Choice<T>> stream() {
        return Stream.of(this);
    }

    /**
     * Returns an {@link Optional} representing a non-{@code null} <i>right</i>
     * value.
     *
     * @return an {@link Optional} with the non-{@code null} <i>right</i> value,
     *         otherwise {@link Optional#empty()}
     */
    public Optional<T> optional() {
        return isRight() ? Optional.ofNullable(value) : Optional.empty();
    }

    /**
     * Returns a <i>right</i> case of the represented value.
     *
     * @return a <i>right</i> case of the represented value
     */
    public Choice<T> right() {
        return isRight() ? this : right(value);
    }

    /**
     * Returns a <i>wrong</i> case of the represented value.
     *
     * @return a <i>wrong</i> case of the represented value
     */
    public Choice<T> wrong() {
        return isWrong() ? this : wrong(value);
    }

    /**
     * Returns an instance representing the same value, but <i>right</i> turned
     * into <i>wrong</i> and vice versa.
     *
     * @return an swapped instance
     */
    public Choice<T> swap() {
        return isRight() ? wrong(value) : right(value);
    }

    /**
     * Tests if the value represented by this choice is considered <i>right</i>.
     *
     * @return {@code true} if the represented value is considered <i>right</i>
     */
    public boolean isRight() {
        return right;
    }

    /**
     * Tests if the value represented by this choice is considered <i>wrong</i>.
     *
     * @return {@code false} if the represented value is considered <i>wrong</i>
     */
    public boolean isWrong() {
        return !isRight();
    }

    /**
     * Passes the represented value to the given consumer if {@link #isRight()}
     * returns {@code true}.
     *
     * @param consumer
     *            the consumer to accept the value. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> ifRight(Consumer<? super T> consumer) {
        if (isRight()) {
            consumer.accept(value);
        }

        return this;
    }

    /**
     * Runs the given action if {@link #isRight()} returns {@code false}.
     *
     * @param consumer
     *            the consumer to accept the value. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> ifWrong(Consumer<? super T> consumer) {
        if (isWrong()) {
            consumer.accept(value);
        }

        return this;
    }

    /**
     * Maps the represented value if right, keeping the wrong value untouched.
     *
     * @param mapping
     *            the function to map the represented value when
     *            {@link #isRight()}. It must not be {@code null}.
     *
     * @return a new instance representing the mapped value
     */
    public Choice<T> mapRight(Function<? super T, ? extends T> mapping) {
        return isRight() ? right(mapping.apply(value)) : this;
    }

    /**
     * Maps the represented value if wrong, keeping the right value untouched.
     *
     * @param mapping
     *            the function to map the represented value when
     *            {@link #isWrong()}. It must not be {@code null}.
     *
     * @return a new instance representing the mapped value
     */
    public Choice<T> mapWrong(Function<? super T, ? extends T> mapping) {
        return isRight() ? this : wrong(mapping.apply(value));
    }

    /**
     * Maps the represented value.
     *
     * @param <V>
     *            the type of the new value
     * @param whenRight
     *            the function to map the represented value when
     *            {@link #isRight()}. It must not be {@code null}.
     * @param whenWrong
     *            the function to map the represented value when
     *            {@link #isWrong()}. It must not be {@code null}.
     *
     * @return a new instance representing the mapped value
     */
    public <V> Choice<V> map(Function<? super T, ? extends V> whenRight, Function<? super T, ? extends V> whenWrong) {
        return isRight() ? right(whenRight.apply(value)) : wrong(whenWrong.apply(value));
    }

    /**
     * Maps this instance.
     *
     * @param <V>
     *            the type of the new represented value
     * @param mapping
     *            the mapping function. It must not be {@code null} and it
     *            should not return {@code null}.
     *
     * @return the result of the mapping function
     */
    public <V> Choice<V> map(Function<? super Choice<T>, Choice<V>> mapping) {
        return mapping.apply(this);
    }

    /**
     * Applies either action depending on {@link #isRight()} result.
     *
     * @param whenRight
     *            the consumer to accept the represented value if
     *            {@link #isRight()}. It must not be {@code null}.
     * @param whenWrong
     *            the consumer to accept the represented value if
     *            {@link #isWrong()}. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> use(Consumer<? super T> whenRight, Consumer<? super T> whenWrong) {
        if (isRight()) {
            whenRight.accept(value);
        } else {
            whenWrong.accept(value);
        }

        return this;
    }

    /**
     * Maps the represented value and returns it using the appropriate mapping
     * function for the actual <i>right</i> or <i>wrong</i> case.
     *
     * @param <V>
     *            the type of the new value
     * @param whenRight
     *            the function to map the represented value when
     *            {@link #isRight()}. It must not be {@code null}.
     * @param whenWrong
     *            the function to map the represented value when
     *            {@link #isWrong()}. It must not be {@code null}.
     *
     * @return the mapped value
     */
    public <V> V reconcile(Function<? super T, ? extends V> whenRight, Function<? super T, ? extends V> whenWrong) {
        return isRight() ? whenRight.apply(value) : whenWrong.apply(value);
    }

    /**
     * Maps the represented value and returns it using the appropriate mapping
     * function for the actual <i>right</i> or <i>wrong</i> case.
     *
     * @param <V>
     *            the type of the new value
     * @param <X>
     *            the type of the exception that might be thrown
     * @param whenRight
     *            the function to map the represented value when
     *            {@link #isRight()}. It must not be {@code null}.
     * @param whenWrong
     *            the function to map the represented value when
     *            {@link #isWrong()}. It must not be {@code null}.
     *
     * @return the mapped value
     *
     * @throws X
     *             if an operation fails
     */
    public <V, X extends Exception> V resolve(ThrowingOperation<? super T, ? extends V, ? extends X> whenRight, ThrowingOperation<? super T, ? extends V, ? extends X> whenWrong) throws X {
        return isRight() ? whenRight.execute(value) : whenWrong.execute(value);
    }
}
