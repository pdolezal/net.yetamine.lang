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

/**
 * An alternative to {@link Optional} for the cases when the value may or may
 * not be acceptable, but the decision is provided and the container does not
 * decide on its own. The value is therefore marked as either <i>true</i> or
 * <i>false</i> and the container provides methods for both these situations.
 *
 * @param <T>
 *            the type of the contained value
 */
public final class Choice<T> implements Supplier<T> {

    /** Shared instance for <i>true</i> {@code null}. */
    private static final Choice<?> NULL_AS_TRUE = new Choice<>(null, true);
    /** Shared instance for <i>false</i> {@code null}. */
    private static final Choice<?> NULL_AS_FALSE = new Choice<>(null, false);

    /** Stored value. */
    private final T value;
    /** Flag marking <i>true</i> or <i>false</i>. */
    private final boolean valid;

    /**
     * Creates a new instance.
     *
     * @param o
     *            the value to store
     * @param v
     *            the flag marking <i>true</i> or <i>false</i>
     */
    private Choice(T o, boolean v) {
        value = o;
        valid = v;
    }

    /**
     * Returns an instance representing the value as <i>true</i>.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return an instance representing the value as <i>true</i>
     */
    public static <T> Choice<T> asTrue(T o) {
        return (o != null) ? new Choice<>(o, true) : asTrue();
    }

    /**
     * Returns an instance representing the value as <i>false</i>.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return an instance representing the value as <i>false</i>
     */
    public static <T> Choice<T> asFalse(T o) {
        return (o != null) ? new Choice<>(o, false) : asFalse();
    }

    /**
     * Returns a new instance using the value of the given {@link Optional}
     * container as <i>true</i>, or {@code null} as <i>false</i> if the given
     * container is empty.
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
    public static <T> Choice<T> of(Optional<? extends T> optional) {
        return (Choice<T>) optional.map(Choice::asTrue).orElseGet(Choice::asFalse);
    }

    /**
     * Returns a new instance representing a non-{@code null} values as
     * <i>true</i> and {@code null} as <i>false</i>.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return the new instance
     */
    public static <T> Choice<T> nonNull(T o) {
        return (o != null) ? new Choice<>(o, true) : asFalse();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Choice[%s: %s]", valid, value);
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
            return Objects.equals(value, o.value) && (valid == o.valid);
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, valid);
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
     * Returns the value of this choice if {@link #isTrue()}.
     *
     * @return the value of this choice
     *
     * @throws NoSuchElementException
     *             if this choice {@link #isFalse()}
     */
    public T require() {
        if (isTrue()) {
            return get();
        }

        throw new NoSuchElementException();
    }

    /**
     * Returns the value of this choice if {@link #isTrue()}.
     *
     * @param <X>
     *            the type of the exception to throw
     * @param e
     *            the supplier of the exception to throw if {@link #isFalse()}
     *
     * @return the value of this choice
     *
     * @throws X
     *             if this choice {@link #isFalse()}
     */
    public <X extends Throwable> T require(Supplier<? extends X> e) throws X {
        if (isTrue()) {
            return get();
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
     * Returns an {@link Optional} with the value if the value {@link #isTrue()}
     * and not {@code null}.
     *
     * @return an {@link Optional} with the value, or an empty container if the
     *         value {@link #isFalse()} or {@code null}
     */
    public Optional<T> optional() {
        return isTrue() ? Optional.ofNullable(get()) : Optional.empty();
    }

    /**
     * Returns an instance representing the same value, but with the swapped
     * decision, so that when this instance {@link #isTrue()}, then the result
     * {@link #isFalse()}.
     *
     * @return an swapped instance
     */
    public Choice<T> flip() {
        return isTrue() ? asFalse(get()) : asTrue(get());
    }

    /**
     * Tests if the value represented by this choice is considered <i>true</i>.
     *
     * @return {@code true} if the represented value is considered <i>true</i>
     */
    public boolean isTrue() {
        return valid;
    }

    /**
     * Tests if the value represented by this choice is considered <i>false</i>.
     *
     * @return {@code false} if the represented value is considered <i>false</i>
     */
    public boolean isFalse() {
        return !isTrue();
    }

    /**
     * Passes the represented value to the given consumer if {@link #isTrue()}
     * returns {@code true}.
     *
     * @param consumer
     *            the consumer to accept the value. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> ifTrue(Consumer<? super T> consumer) {
        if (isTrue()) {
            consumer.accept(get());
        }

        return this;
    }

    /**
     * Runs the given action if {@link #isTrue()} returns {@code false}.
     *
     * @param consumer
     *            the consumer to accept the value. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> ifFalse(Consumer<? super T> consumer) {
        if (isFalse()) {
            consumer.accept(get());
        }

        return this;
    }

    /**
     * Applies either action depending on {@link #isTrue()} result.
     *
     * @param whenTrue
     *            the consumer to accept the represented value if
     *            {@link #isTrue()}. It must not be {@code null}.
     * @param whenFalse
     *            the consumer to accept the represented value if
     *            {@link #isFalse()}. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> consume(Consumer<? super T> whenTrue, Consumer<? super T> whenFalse) {
        if (isTrue()) {
            whenTrue.accept(get());
        } else {
            whenFalse.accept(get());
        }

        return this;
    }

    /**
     * Maps this instance.
     *
     * @param <V>
     *            the type of the new represented value
     * @param f
     *            the mapping function. It must not be {@code null} and it
     *            should not return {@code null}.
     *
     * @return the result of the mapping function
     */
    public <V> Choice<V> map(Function<? super Choice<T>, Choice<V>> f) {
        return f.apply(this);
    }

    /**
     * Maps the represented value.
     *
     * @param <V>
     *            the type of the new value
     * @param whenTrue
     *            the function to map the represented value when
     *            {@link #isTrue()}. It must not be {@code null}.
     * @param whenFalse
     *            the function to map the represented value when
     *            {@link #isFalse()}. It must not be {@code null}.
     *
     * @return a new instance represented the mapped value
     */
    public <V> Choice<V> map(Function<? super T, ? extends V> whenTrue, Function<? super T, ? extends V> whenFalse) {
        return isTrue() ? asTrue(whenTrue.apply(get())) : asFalse(whenFalse.apply(get()));
    }

    /**
     * Maps the represented value and returns it; this method is a shortcut for
     * {@code choice.map(whenTrue, whenFalse).get()}.
     *
     * @param <V>
     *            the type of the new value
     * @param whenTrue
     *            the function to map the represented value when
     *            {@link #isTrue()}. It must not be {@code null}.
     * @param whenFalse
     *            the function to map the represented value when
     *            {@link #isFalse()}. It must not be {@code null}.
     *
     * @return the mapped value
     */
    public <V> V reconcile(Function<? super T, ? extends V> whenTrue, Function<? super T, ? extends V> whenFalse) {
        return isTrue() ? whenTrue.apply(get()) : whenFalse.apply(get());
    }

    /**
     * Returns the shared instance representing a {@code null} as <i>true</i>.
     *
     * @return the shared instance representing a {@code null} as <i>true</i>
     */
    @SuppressWarnings("unchecked")
    private static <T> Choice<T> asTrue() {
        return (Choice<T>) NULL_AS_TRUE;
    }

    /**
     * Returns the shared instance representing a {@code null} as <i>false</i>.
     *
     * @return the shared instance representing a {@code null} as <i>false</i>
     */
    @SuppressWarnings("unchecked")
    private static <T> Choice<T> asFalse() {
        return (Choice<T>) NULL_AS_FALSE;
    }
}
