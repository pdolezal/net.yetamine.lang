package net.yetamine.lang.closeables;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A {@link SafeCloseable} adapter for resources that don't implement that
 * interface.
 *
 * <p>
 * The implementation is thread-safe, guarantees that the given closing handler
 * is invoked only on the first invocation of {@link #close()}, while
 * {@link #resource()} provides only the resource which is not closed yet.
 *
 * @param <T>
 *            the type of the adapted resource
 */
public final class SafeCloseableAdapter<T> implements SafeCloseableResource<T> {

    /** Adapted object. */
    private final AtomicReference<T> resource;
    /** Closing handler for the adapted object. */
    private final Consumer<? super T> close;

    /**
     * Creates a new instance.
     *
     * @param object
     *            the object to adapt. It must not be {@code null}.
     * @param closingHandler
     *            the handler to clean up. It must not be {@code null}.
     */
    private SafeCloseableAdapter(T object, Consumer<? super T> closingHandler) {
        resource = new AtomicReference<>(Objects.requireNonNull(object));
        close = Objects.requireNonNull(closingHandler);
    }

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of the adapted resource
     * @param object
     *            the object to adapt. It must not be {@code null}.
     * @param closingHandler
     *            the handler to clean up; because it can't return any
     *            exception, it may signal a problem only by throwing an
     *            unchecked exception. It must not be {@code null}.
     *
     * @return an adapter for the object
     */
    public static <T> SafeCloseableAdapter<T> using(T object, Consumer<? super T> closingHandler) {
        return new SafeCloseableAdapter<>(object, closingHandler);
    }

    /**
     * Provides the adapted object.
     *
     * @throws IllegalStateException
     *             if closed already
     *
     * @see net.yetamine.lang.closeables.SafeCloseableResource#resource()
     */
    public T resource() {
        final T result = resource.get();
        if (result != null) {
            return result;
        }

        throw new IllegalStateException();
    }

    /**
     * @see net.yetamine.lang.closeables.SafeCloseableResource#close()
     */
    public void close() {
        final T value = resource.getAndSet(null);
        if (value == null) {
            return;
        }

        close.accept(value);
    }

    /**
     * Provides the adapted object if not closed yet.
     *
     * @return an {@link Optional} containing the adapted object, or an empty
     *         container if the resource has been closed
     */
    public Optional<T> available() {
        return Optional.ofNullable(resource.get());
    }

    /**
     * Indicates whether the resource is closed; note that the state may change
     * by closing the resource from another thread any time.
     *
     * @return {@code true} if the resource is closed
     */
    public boolean isClosed() {
        return (resource.get() == null);
    }
}
