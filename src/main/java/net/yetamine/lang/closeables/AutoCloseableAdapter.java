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

package net.yetamine.lang.closeables;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An {@link AutoCloseable} adapter for resources that don't implement that
 * interface.
 *
 * <p>
 * The implementation is thread-safe, guarantees that the given closing handler
 * is invoked only on the first invocation of {@link #close()}, while
 * {@link #resource()} provides only the resource which is not closed yet.
 *
 * @param <T>
 *            the type of the adapted resource
 * @param <X>
 *            the type of the exception that the {@link #close()} method may
 *            throw for this resource
 */
public final class AutoCloseableAdapter<T, X extends Exception> implements AutoCloseableResource<T, X> {

    /** Adapted object. */
    private final AtomicReference<T> resource;
    /** Closing handler providing exceptions. */
    private final Function<? super T, ? extends X> close;

    /**
     * Creates a new instance.
     *
     * @param object
     *            the object to adapt. It must not be {@code null}.
     * @param closingHandler
     *            the handler to clean up and return the exception to throw (
     *            {@code null} if none should be thrown). It must not be
     *            {@code null}.
     */
    private AutoCloseableAdapter(T object, Function<? super T, ? extends X> closingHandler) {
        resource = new AtomicReference<>(Objects.requireNonNull(object));
        close = closingHandler;
        assert (close != null);
    }

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of the adapted resource
     * @param <X>
     *            the type of the exception
     * @param object
     *            the object to adapt. It must not be {@code null}.
     * @param closingHandler
     *            the handler to clean up and return the exception to throw (
     *            {@code null} if none should be thrown). It must not be
     *            {@code null}.
     *
     * @return an adapter for the object
     */
    public static <T, X extends Exception> AutoCloseableAdapter<T, X> using(T object, Function<? super T, ? extends X> closingHandler) {
        Objects.requireNonNull(closingHandler);
        return new AutoCloseableAdapter<>(object, closingHandler);
    }

    /**
     * Creates a new instance.
     *
     * @param <T>
     *            the type of the adapted resource
     * @param <X>
     *            the type of the exception
     * @param object
     *            the object to adapt. It must not be {@code null}.
     * @param closingHandler
     *            the handler to clean up; because it can't return any
     *            exception, it may signal a problem only by throwing an
     *            unchecked exception. It must not be {@code null}.
     *
     * @return an adapter for the object
     */
    public static <T, X extends Exception> AutoCloseableAdapter<T, X> using(T object, Consumer<? super T> closingHandler) {
        Objects.requireNonNull(closingHandler);

        // Using explicit definition, Eclipse is unhappy otherwise
        final Function<T, X> closingFunction = o -> {
            closingHandler.accept(o);
            return null;
        };

        return new AutoCloseableAdapter<>(object, closingFunction);
    }

    /**
     * Provides the adapted object.
     *
     * @throws IllegalStateException
     *             if closed already
     *
     * @see net.yetamine.lang.closeables.AutoCloseableResource#resource()
     */
    public T resource() {
        final T result = resource.get();
        if (result != null) {
            return result;
        }

        throw new IllegalStateException();
    }

    /**
     * @see net.yetamine.lang.closeables.AutoCloseableResource#close()
     */
    public void close() throws X {
        final T value = resource.getAndSet(null);
        if (value == null) {
            return;
        }

        final X toThrow = close.apply(value);
        if (toThrow != null) {
            throw toThrow;
        }
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
