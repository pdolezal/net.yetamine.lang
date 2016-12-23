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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A standalone resource representation.
 *
 * <p>
 * This implementation supports even {@code null} resource "instances" and
 * allows adopting instances which shall never be released or even closed.
 * Therefore it may be used for situations when some code expects some of the
 * parent interfaces, but a particular resource can't be managed dynamically.
 *
 * @param <R>
 *            the type of the resource
 * @param <X>
 *            the type of the exception that the creation or release of the
 *            resource may throw
 */
public final class ResourceAdapter<R, X extends Exception> implements ResourceHandle<R, X> {

    /** Resource destructor for {@link #close()}. */
    private final ResourceClosing<? super R, ? extends X> closing;
    /** Resource destructor for {@link #release()}. */
    private final ResourceClosing<? super R, ? extends X> releasing;
    /** Resource constructor: {@code null} after {@link #close()}. */
    private ResourceOpening<? extends R, ? extends X> opening;
    /** Available resource instance. */
    private volatile R instance;

    /**
     * Creates a new instance.
     *
     * @param constructor
     *            the strategy for opening a resource instance. It must not be
     *            {@code null}.
     * @param destructor
     *            the strategy for closing a resource instance. It must not be
     *            {@code null}.
     */
    private ResourceAdapter(ResourceOpening<? extends R, ? extends X> constructor, ResourceClosing<? super R, ? extends X> destructor) {
        opening = Objects.requireNonNull(constructor);

        Objects.requireNonNull(destructor);
        releasing = closing = r -> {
            if (r != null) {
                destructor.close(r);
            }
        };
    }

    /**
     * Creates a new instance.
     *
     * @param resource
     *            the resource instance to manage
     * @param destructor
     *            the strategy for closing a resource instance. It must not be
     *            {@code null}.
     * @param nil
     *            the overloading discriminator telling that nil release action
     *            should be used.
     */
    private ResourceAdapter(R resource, ResourceClosing<? super R, ? extends X> destructor, Void nil) {
        Objects.requireNonNull(destructor);

        // Closing needs special handling: since the resource must be closed then, but
        // might not be available in order to have release() consistent with the other
        // usage possibility. So, closing must close the actual given resource at the
        // end, but just once and if not null (again, for the consistency).
        final AtomicBoolean closed = new AtomicBoolean();

        closing = r -> {
            assert ((r == null) || (r == resource));
            if (closed.compareAndSet(false, true) && (resource != null)) {
                destructor.close(resource);
            }
        };

        instance = resource;                // Make the resource available from the beginning
        opening = () -> resource;           // Always provide the same resource instance
        releasing = ResourceClosing.none(); // Never release though
    }

    /**
     * Creates a new instance.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to manage the
     *            resource may throw
     * @param constructor
     *            the strategy for opening a resource instance. It must not be
     *            {@code null}.
     * @param destructor
     *            the strategy for closing a resource instance. It must not be
     *            {@code null}.
     *
     * @return the new instance
     */
    public static <R, X extends Exception> ResourceHandle<R, X> managed(ResourceOpening<? extends R, ? extends X> constructor, ResourceClosing<? super R, ? extends X> destructor) {
        return new ResourceAdapter<>(constructor, destructor);
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * A managed resource created by this method would be closed automatically
     * using its own {@link PureCloseable#close()} method for both release and
     * close.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to manage the
     *            resource may throw
     * @param constructor
     *            the strategy for opening a resource instance. It must not be
     *            {@code null}.
     *
     * @return the new instance
     */
    public static <X extends Exception, R extends PureCloseable<? extends X>> ResourceHandle<R, X> managed(ResourceOpening<? extends R, ? extends X> constructor) {
        return managed(constructor, r -> r.close());
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * The resource given for managing can be closed with the handle using the
     * given closing strategy, but {@link #release()} does not apply to it, so
     * any attempts to just release the resource do nothing.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to manage the
     *            resource may throw
     * @param resource
     *            the resource instance to manage
     * @param destructor
     *            the strategy for closing a resource instance. It must not be
     *            {@code null}.
     *
     * @return the new instance
     */
    public static <R, X extends Exception> ResourceHandle<R, X> adopted(R resource, ResourceClosing<? super R, ? extends X> destructor) {
        return new ResourceAdapter<>(resource, destructor, null);
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * The resource given for managing can be closed with the handle using its
     * own {@link PureCloseable#close()} methods, while {@link #release()} does
     * not apply to it, so any attempts to just release the resource do nothing.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to manage the
     *            resource may throw
     * @param resource
     *            the resource instance to manage
     *
     * @return the new instance
     */
    public static <X extends Exception, R extends PureCloseable<? extends X>> ResourceHandle<R, X> adopted(R resource) {
        return adopted(resource, r -> r.close()); // JDK does not handle R::close well...
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * The resource given for managing can't be closed by any means by the
     * returned handle. This is useful when the handle should provide some
     * resource without actually owning it, e.g., when some code expects a
     * handle, but the resource can't be managed in such a dynamic way and must
     * remain steady for the code.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the attempt to manage the
     *            resource may throw
     * @param resource
     *            the resource instance to manage
     *
     * @return the new instance
     */
    public static <R, X extends Exception> ResourceHandle<R, X> using(R resource) {
        return adopted(resource, ResourceClosing.none());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("ResourceHandle[id=%08x, instance=%s]", System.identityHashCode(this), instance);
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceInstance#available()
     */
    public Optional<R> available() {
        return Optional.ofNullable(instance);
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceInstance#acquired()
     */
    public R acquired() throws X {
        R result = instance;
        if (result != null) {
            return result;
        }

        synchronized (this) {
            result = instance;
            if (result != null) {
                return result;
            }

            if (opening == null) {
                throw new IllegalStateException();
            }

            result = opening.open();
            instance = result;
        }

        return result;
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceInstance#release()
     */
    public void release() throws X {
        final R resource;

        synchronized (this) {
            resource = instance;
            instance = null;
        }

        releasing.close(resource);
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceHandle#close()
     */
    public void close() throws X {
        final R resource;

        synchronized (this) {
            opening = null; // Prevent any subsequent opening
            resource = instance;
            instance = null;
        }

        closing.close(resource);
    }
}
