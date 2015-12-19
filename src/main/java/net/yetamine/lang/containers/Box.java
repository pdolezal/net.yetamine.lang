package net.yetamine.lang.containers;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A mutable container for holding a single value.
 *
 * @param <T>
 *            the type of the stored value
 */
public final class Box<T> implements Serializable, Consumer<T>, Supplier<T> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Boxed value. */
    private T value;

    /**
     * Creates a new instance filled with {@code null}.
     */
    private Box() {
        // Default constructor
    }

    /**
     * Creates a new instance filled with the initial value.
     *
     * @param initial
     *            the initial value
     */
    private Box(T initial) {
        value = initial;
    }

    /**
     * Creates a new instance filled with the initial value.
     *
     * @param <T>
     *            the type of the stored value
     * @param value
     *            the initial value
     *
     * @return the new instance
     */
    public static <T> Box<T> of(T value) {
        return new Box<>(value);
    }

    /**
     * Creates a new instance filled with {@code null}.
     *
     * @param <T>
     *            the type of the stored value
     *
     * @return the new instance
     */
    public static <T> Box<T> empty() {
        return new Box<>();
    }

    /**
     * Makes a box that accepts a value only once.
     *
     * <p>
     * This method fills the given box with an empty {@link Optional}; the
     * returned consumer then ignores {@code null} arguments and once it stores
     * the first non-{@code null} argument as a non-empty {@code Optional}, all
     * subsequent attempts to store anything are ignored as well.
     *
     * @param <T>
     *            the type of the stored value
     * @param box
     *            the box to use. It must not be {@code null}.
     *
     * @return a consumer storing the first non-{@code null} value only
     */
    public static <T> Consumer<T> acceptingOnce(Box<Optional<T>> box) {
        box.accept(Optional.empty());

        return o -> {
            if ((o == null) || box.get().isPresent()) {
                return;
            }

            box.accept(Optional.ofNullable(o));
        };
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuilder("box[").append(value).append(']').toString();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return ((obj instanceof Box<?>) && Objects.equals(((Box<?>) obj).value, value));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a stream using this instance as the element source.
     *
     * @return a stream using this instance as the element source
     */
    public Stream<T> stream() {
        return Stream.of(this).map(Box::get);
    }

    /**
     * Stores the value in this instance.
     *
     * @see java.util.function.Consumer#accept(java.lang.Object)
     */
    public void accept(T t) {
        value = t;
    }

    /**
     * Returns the value stored in this instance.
     *
     * @see java.util.function.Supplier#get()
     */
    public T get() {
        return value;
    }

    /**
     * Stores the value in this instance.
     *
     * @param t
     *            the value to accept
     *
     * @return this instance
     */
    public Box<T> set(T t) {
        value = t;
        return this;
    }

    /**
     * Returns the value as an {@link Optional} instance.
     *
     * <p>
     * This method provides a bridge to the standard library and allows using
     * patterns like {@code box.nonNull().orElse(fallback)}
     *
     * @return the value as an {@link Optional} instance
     */
    public Optional<T> nonNull() {
        return Optional.ofNullable(value);
    }

    /**
     * Updates the stored value the result of the given function applied on the
     * current value.
     *
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return this instance
     */
    public Box<T> replace(Function<? super T, ? extends T> mapping) {
        value = mapping.apply(value);
        return this;
    }

    /**
     * Returns the result of the given function applied on the value stored in
     * this instance.
     *
     * @param <V>
     *            the type of the mapping result
     * @param mapping
     *            the mapping function to apply. It must not be {@code null}.
     *
     * @return the result of the mapping function
     */
    public <V> V map(Function<? super T, ? extends V> mapping) {
        return mapping.apply(value);
    }

    /**
     * Passes the contained value to the specified consumer.
     *
     * @param consumer
     *            the consumer to call. It must not be {@code null}.
     *
     * @return this instance
     */
    public Box<T> use(Consumer<? super T> consumer) {
        consumer.accept(value);
        return this;
    }
}
