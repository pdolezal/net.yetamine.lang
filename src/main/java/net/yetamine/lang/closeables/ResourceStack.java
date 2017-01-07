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

import net.yetamine.lang.exceptions.Throwing;

/**
 * An implementation of {@link ResourceGroup} which stacks the resources in a
 * similar way like <i>try-with-resources</i> does: closing or releasing the
 * group closes or releases all resources, beginning with the last added one.
 * Closing or releasing a particular resource causes closing or releasing all
 * resources added after the resource as well, again the last added one being
 * the first one to close or release.
 *
 * @param <X>
 *            the type of the exception that the creation or release of the
 *            resource may throw
 */
public final class ResourceStack<X extends Exception> implements ResourceGroup<X> {

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
    public ResourceStack() {
        // Default constructor
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
     * @see net.yetamine.lang.closeables.ResourceGroup#release()
     */
    public void release() throws X {
        root.release();
    }

    /**
     * @see net.yetamine.lang.closeables.ResourceGroup#close()
     */
    public void close() throws X {
        synchronized (root) {
            closed = true;
        }

        root.close();
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
         * Creates a new instance for the root of a {@link ResourceStack}.
         */
        Handle() {
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
            synchronized (root) {
                if (this != root) {
                    // This is a regular node, in that case we should not
                    // continue releasing it if it is already closed, but
                    // finding that out requires locking this instance

                    synchronized (this) {
                        if (opening == null) { // Already closed
                            assert (instance == null);
                            return;
                        }
                    }
                }

                Throwable exception = null;
                boolean interrupt = false;

                // Traverse from the top of the stack towards this node and free the resources
                for (Handle<?, X> handle = root.next; /* until handle == this */; handle = handle.next) {
                    try { // This may throw
                        handle.releaseSelf();
                    } catch (Throwable t) {
                        if (exception != null) { // Rethrowing just the first one, others are suppressed
                            interrupt |= t instanceof InterruptedException;
                            exception.addSuppressed(t);
                        } else {
                            exception = t;
                        }
                    }

                    if (handle == this) {
                        break;
                    }
                }

                rethrow(exception, interrupt);
            }
        }

        /**
         * @see net.yetamine.lang.closeables.ResourceHandle#close()
         */
        public void close() throws X {
            synchronized (root) {
                if (this != root) {
                    // This is a regular node, in that case we should not
                    // continue releasing it if it is already closed, but
                    // finding that out requires locking this instance

                    synchronized (this) {
                        if (opening == null) { // Already closed
                            assert (instance == null);
                            return;
                        }
                    }
                }

                Throwable exception = null;
                boolean interrupt = false;

                // Traverse from the top of the stack towards this node and close all the nodes
                for (Handle<?, X> handle = root.next; /* until handle == this */; /* next of handle */) {
                    final Handle<?, X> removed = handle;

                    try { // This may throw
                        handle = handle.next;
                        removed.removeSelf();
                        removed.closeSelf();
                    } catch (Throwable t) {
                        if (exception != null) { // Rethrowing just the first one, others are suppressed
                            interrupt |= t instanceof InterruptedException;
                            exception.addSuppressed(t);
                        } else {
                            exception = t;
                        }
                    }

                    if (removed == this) {
                        break;
                    }
                }

                rethrow(exception, interrupt);
            }
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
         * Removes self from the list.
         *
         * <p>
         * The caller must hold the {@link #root}.
         */
        private void removeSelf() {
            assert Thread.holdsLock(root);

            prev.next = next;
            next.prev = prev;
            prev = next = this;
        }

        /**
         * Releases the resource for this handle only.
         *
         * <p>
         * The caller must hold the {@link #root}.
         *
         * @throws X
         *             if the operation fails
         */
        private void releaseSelf() throws X {
            final R resource;
            synchronized (this) {
                resource = instance;
                instance = null;
            }

            releasing.close(resource);
        }

        /**
         * Closes the resource for this handle only.
         *
         * <p>
         * The caller must hold the {@link #root}.
         *
         * @throws X
         *             if the operation fails
         */
        private void closeSelf() throws X {
            final R resource;
            synchronized (this) {
                resource = instance;
                instance = null;
                opening = null; // Like release, but prevent any future opening
            }

            closing.close(resource);
        }

        /**
         * Rethrows an exception.
         *
         * @param exception
         *            the exception to throw. It may be {@code null}.
         * @param interrupt
         *
         * @throws Throwable
         *             if an exception is given
         */
        private static <X extends Throwable> void rethrow(Throwable exception, boolean interrupt) throws X {
            if (exception == null) {
                return;
            }

            Throwing.some(exception).throwIfUnchecked();
            if (interrupt && !(exception instanceof InterruptedException)) {
                Thread.currentThread().interrupt();
            }

            @SuppressWarnings("unchecked") // Valid because all unchecked exception have been handled
            final X throwable = (X) exception;
            throw throwable;
        }
    }
}
