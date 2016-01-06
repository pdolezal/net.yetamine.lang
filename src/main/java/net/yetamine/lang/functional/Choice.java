package net.yetamine.lang.functional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An alternative to {@link Optional} for the cases when the value may or may
 * not be acceptable, but the decision is provided and the container does not
 * decide on its own.
 *
 * @param <T>
 *            the type of the contained value
 */
public final class Choice<T> implements Supplier<T> {

    /** Shared instance for accepted {@code null}. */
    private static final Choice<?> ACCEPTED_NULL = new Choice<>(null, true);
    /** Shared instance for rejected {@code null}. */
    private static final Choice<?> REJECTED_NULL = new Choice<>(null, false);

    /** Stored value. */
    private final T value;
    /** Acceptance flag. */
    private final boolean valid;

    /**
     * Creates a new instance.
     *
     * @param o
     *            the value to store
     * @param v
     *            the validity flag
     */
    private Choice(T o, boolean v) {
        value = o;
        valid = v;
    }

    /**
     * Returns an instance representing the value as accepted.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return an instance representing the value as accepted
     */
    public static <T> Choice<T> accept(T o) {
        return (o != null) ? new Choice<>(o, true) : accept();
    }

    /**
     * Returns an instance representing the value as rejected.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return an instance representing the value as rejected
     */
    public static <T> Choice<T> reject(T o) {
        return (o != null) ? new Choice<>(o, false) : reject();
    }

    /**
     * Returns a new instance accepting the value of the given {@link Optional}
     * instance, or rejecting {@code null} if the given container is empty.
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
        return (Choice<T>) optional.map(Choice::accept).orElseGet(Choice::reject);
    }

    /**
     * Returns a new instance accepting a non-{@code null} values, rejecting
     * {@code null}.
     *
     * @param <T>
     *            the type of the value
     * @param o
     *            the value to represent
     *
     * @return the new instance
     */
    public static <T> Choice<T> nonNull(T o) {
        return (o != null) ? new Choice<>(o, true) : reject();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Choice[%s: %s]", valid ? "accepted" : "rejected", value);
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
     * Returns the value of this choice if {@link #isAccepted()}.
     *
     * @return the value of this choice
     *
     * @throws NoSuchElementException
     *             if this choice {@link #isRejected()}
     */
    public T require() {
        if (isAccepted()) {
            return get();
        }

        throw new NoSuchElementException();
    }

    /**
     * Returns the value of this choice if {@link #isAccepted()}.
     *
     * @param <X>
     *            the type of the exception to throw
     * @param e
     *            the supplier of the exception to throw if
     *            {@link #isRejected()}
     *
     * @return the value of this choice
     *
     * @throws X
     *             if this choice {@link #isRejected()}
     */
    public <X extends Throwable> T require(Supplier<? extends X> e) throws X {
        if (isAccepted()) {
            return get();
        }

        throw e.get();
    }

    /**
     * Returns an {@link Optional} with the value if the value
     * {@link #isAccepted()} and not {@code null}.
     *
     * @return an {@link Optional} with the value, or an empty container if the
     *         value {@link #isRejected()} or {@code null}
     */
    public Optional<T> optional() {
        return isAccepted() ? Optional.ofNullable(get()) : Optional.empty();
    }

    /**
     * Returns an instance representing the same value, but with the swapped
     * decision, so that when this instance {@link #isAccepted()}, then the
     * result {@link #isRejected()}.
     *
     * @return an swapped instance
     */
    public Choice<T> flip() {
        return isAccepted() ? reject(get()) : accept(get());
    }

    /**
     * Tests if the value represented by this choice is accepted.
     *
     * @return {@code true} if the represented value is considered accepted
     */
    public boolean isAccepted() {
        return valid;
    }

    /**
     * Tests if the value represented by this choice is rejected.
     *
     * @return {@code false} if the represented value is considered accepted
     */
    public boolean isRejected() {
        return !isAccepted();
    }

    /**
     * Passes the represented value to the given consumer if
     * {@link #isAccepted()} returns {@code true}.
     *
     * @param consumer
     *            the consumer to accept the value. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> ifAccepted(Consumer<? super T> consumer) {
        if (isAccepted()) {
            consumer.accept(get());
        }

        return this;
    }

    /**
     * Runs the given action if {@link #isAccepted()} returns {@code false}.
     *
     * @param consumer
     *            the consumer to accept the value. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> ifRejected(Consumer<? super T> consumer) {
        if (isRejected()) {
            consumer.accept(get());
        }

        return this;
    }

    /**
     * Applies either action depending on {@link #isAccepted()} result.
     *
     * @param whenAccepted
     *            the consumer to accept the represented value if
     *            {@link #isAccepted()}. It must not be {@code null}.
     * @param whenRejected
     *            the consumer to accept the represented value if
     *            {@link #isRejected()}. It must not be {@code null}.
     *
     * @return this instance
     */
    public Choice<T> consume(Consumer<? super T> whenAccepted, Consumer<? super T> whenRejected) {
        if (isAccepted()) {
            whenAccepted.accept(get());
        } else {
            whenRejected.accept(get());
        }

        return this;
    }

    /**
     * Maps the represented value.
     *
     * @param <V>
     *            the type of the new value
     * @param whenAccepted
     *            the function to map the represented value if
     *            {@link #isAccepted()} . It must not be {@code null}.
     * @param whenRejected
     *            the function to map the represented value if
     *            {@link #isRejected()}. It must not be {@code null}.
     *
     * @return a new instance represented the mapped value
     */
    public <V> Choice<V> map(Function<? super T, ? extends V> whenAccepted, Function<? super T, ? extends V> whenRejected) {
        return isAccepted() ? accept(whenAccepted.apply(get())) : reject(whenRejected.apply(get()));
    }

    /**
     * Maps the represented value and returns it; this method is a shortcut for
     * {@code choice.map(whenAccepted, whenRejected).get()}.
     *
     * @param <V>
     *            the type of the new value
     * @param whenAccepted
     *            the function to map the represented value if
     *            {@link #isAccepted()} . It must not be {@code null}.
     * @param whenRejected
     *            the function to map the represented value if
     *            {@link #isRejected()}. It must not be {@code null}.
     *
     * @return the mapped value
     */
    public <V> V reconcile(Function<? super T, ? extends V> whenAccepted, Function<? super T, ? extends V> whenRejected) {
        return isAccepted() ? whenAccepted.apply(get()) : whenRejected.apply(get());
    }

    /**
     * Returns the shared instance representing an accepted {@code null}.
     *
     * @return the shared instance representing an accepted {@code null}
     */
    @SuppressWarnings("unchecked")
    private static <T> Choice<T> accept() {
        return (Choice<T>) ACCEPTED_NULL;
    }

    /**
     * Returns the shared instance representing rejected {@code null}.
     *
     * @return the shared instance representing rejected {@code null}
     */
    @SuppressWarnings("unchecked")
    private static <T> Choice<T> reject() {
        return (Choice<T>) REJECTED_NULL;
    }
}
