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

import net.yetamine.lang.Throwing;

/**
 * An implementation of {@link ResourceGroup} which deals with the resources as
 * completely independent, i.e., closing or releasing a particular resource has
 * no effect on the other resources in the group. However, when the group shall
 * be closed, the closing advances in stack-like manner: the first resource to
 * close is the last added into the group, while the last resource to close is
 * the first added.
 *
 * @param <X>
 *            the type of the exception that the creation or release of the
 *            resource may throw
 */
public final class ResourcePile<X extends Exception> implements ResourceGroup<X> {

    /**
     * Root element of the chain.
     *
     * <p>
     * The chain is arranged as a circular double-linked list with a fixed head
     * (the root element of the list). The head serves as a lock for adding and
     * removing elements as well.
     */
    private final Handle<?, X> root = new Handle<>();
    /** Flag indicating that no more additions are possible. */
    private boolean closed;

    /**
     * Creates a new instance.
     */
    public ResourcePile() {
        // Default constructor
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceGroup#adopted(java.lang.Object,
     *      net.yetamine.lang.closeables.ResourceClosing)
     */
    public <R> ResourceHandle<R, X> adopted(R resource, ResourceClosing<? super R, ? extends X> destructor) {
        synchronized (root) {
            if (closed) { // Prevent more additions
                throw new IllegalStateException();
            }

            final Handle<R, X> result = new Handle<>(root, resource, destructor, null);
            root.append(result);
            return result;
        }
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceGroup#managed(net.yetamine.lang.closeables.ResourceOpening,
     *      net.yetamine.lang.closeables.ResourceClosing)
     */
    public <R> ResourceHandle<R, X> managed(ResourceOpening<? extends R, ? extends X> constructor, ResourceClosing<? super R, ? extends X> destructor) {
        synchronized (root) {
            if (closed) { // Prevent more additions
                throw new IllegalStateException();
            }

            final Handle<R, X> result = new Handle<>(root, constructor, destructor);
            root.append(result);
            return result;
        }
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceGroup#release()
     */
    public void release() throws X {
        Throwable exception = null;

        synchronized (root) {
            for (Handle<?, X> handle = root.next(); handle != root; handle = handle.next()) {
                try { // Release, but continue always
                    handle.release();
                } catch (Throwable t) {
                    if (exception == null) {
                        exception = t;
                        continue;
                    }

                    exception.addSuppressed(t);
                }
            }
        }

        rethrow(exception);
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceGroup#close()
     */
    public void close() throws X {
        Throwable exception = null;

        synchronized (root) {
            closed = true; // Disable further additions, then drain all items

            for (Handle<?, X> handle; (handle = root.removeNext()) != root;) {
                try { // Close to detach from the root
                    handle.close();
                } catch (Throwable t) {
                    if (exception == null) {
                        exception = t;
                        continue;
                    }

                    exception.addSuppressed(t);
                }
            }
        }

        rethrow(exception);
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceGroup#closed()
     */
    public boolean closed() {
        synchronized (root) {
            return closed;
        }
    }

    /**
     * Rethrows an exception.
     *
     * @param exception
     *            the exception to throw. It may be {@code null}.
     *
     * @throws Throwable
     *             if an exception is given
     */
    private static <X extends Throwable> void rethrow(Throwable exception) throws X {
        if (exception == null) {
            return;
        }

        Throwing.some(exception).throwIf(RuntimeException.class).throwIf(Error.class);
        @SuppressWarnings("unchecked") // Valid because all unchecked exception have been handled
        final X throwable = (X) exception;
        throw throwable;
    }

    /**
     * Represents a resource placeholder and provides means for managing the
     * resource.
     *
     * @param <R>
     *            the type of the resource
     * @param <X>
     *            the type of the exception that the creation or release of the
     *            resource may throw
     */
    private static final class Handle<R, X extends Exception> implements ResourceHandle<R, X> {

        /** Lock for all changes. */
        private final Handle<?, X> root;
        /** Previous item in the list. */
        private Handle<?, X> prev = this;
        /** Following item in the list. */
        private Handle<?, X> next = this;

        /** Resource destructor for {@link #close()}. */
        private final ResourceClosing<? super R, ? extends X> closing;
        /** Resource destructor for {@link #release()}. */
        private final ResourceClosing<? super R, ? extends X> releasing;
        /** Resource constructor: {@code null} after {@link #close()}. */
        private ResourceOpening<? extends R, ? extends X> opening;
        /** Available resource instance. */
        private volatile R instance;

        /**
         * Creates a new instance for the root of a {@link ResourcePile}.
         */
        Handle() {
            // All the strategies might actually throw an exception as they
            // should never be invoked for the root of the chain
            closing = releasing = ResourceClosing.none();
            opening = null;
            root = this;
        }

        /**
         * Creates a new instance.
         *
         * @param head
         *            the root of the handle chain and the lock for
         *            synchronizing. It must not be {@code null}.
         * @param constructor
         *            the strategy for opening a resource instance. It must not
         *            be {@code null}.
         * @param destructor
         *            the strategy for closing a resource instance. It must not
         *            be {@code null}.
         */
        Handle(Handle<?, X> head, ResourceOpening<? extends R, ? extends X> constructor, ResourceClosing<? super R, ? extends X> destructor) {
            opening = Objects.requireNonNull(constructor);

            Objects.requireNonNull(destructor);
            releasing = closing = r -> {
                if (r != null) {
                    destructor.close(r);
                }
            };

            root = head; // Assuming private parameter
            assert (root != null);
        }

        /**
         * Creates a new instance.
         *
         * @param head
         *            the root of the handle chain and the lock for
         *            synchronizing. It must not be {@code null}.
         * @param resource
         *            the resource instance to manage
         * @param destructor
         *            the strategy for closing a resource instance. It must not
         *            be {@code null}.
         * @param nil
         *            the overloading discriminator telling that nil release
         *            action should be used.
         */
        Handle(Handle<?, X> head, R resource, ResourceClosing<? super R, ? extends X> destructor, Void nil) {
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

            releasing = ResourceClosing.none(); // Never release though
            opening = () -> resource;           // Always provide the same resource instance
            instance = resource;                // Make the resource available from the beginning

            root = head; // Assuming private parameter
            assert (root != null);
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
            synchronized (root) {
                removeSelf();
            }

            final R resource;
            synchronized (this) {
                resource = instance;
                instance = null;
                opening = null; // Like release, but prevent any future opening
            }

            closing.close(resource);
        }

        /**
         * Appends the given handle after this instance.
         *
         * <p>
         * The caller must hold the {@link #root}.
         *
         * @param handle
         *            the handle to append. It must not be {@code null}.
         */
        void append(Handle<?, X> handle) {
            assert Thread.holdsLock(root);

            handle.next = next;
            next.prev = handle;
            handle.prev = this;
            next = handle;
        }

        /**
         * Removes the next element of the list and returns it.
         *
         * <p>
         * The caller must hold the {@link #root}.
         *
         * @return the next element of the list
         */
        Handle<?, X> removeNext() {
            assert Thread.holdsLock(root);

            final Handle<?, X> result = next;
            result.removeSelf();
            return result;
        }

        /**
         * Removes self from the list.
         *
         * <p>
         * The caller must hold the {@link #root}.
         */
        void removeSelf() {
            assert Thread.holdsLock(root);

            prev.next = next;
            next.prev = prev;
            prev = next = this;
        }

        /**
         * Returns the next element in the chain.
         *
         * @return the next element in the chain
         */
        Handle<?, X> next() {
            return next;
        }
    }
}
