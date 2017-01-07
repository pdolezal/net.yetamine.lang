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

package net.yetamine.lang.exceptions;

/**
 * This exception serves as a companion of {@link InterruptedException} that
 * indicates that an {@link InterruptedException} has been caught and the
 * thread's interruption state has been set, but the interruption request
 * requires additional attention.
 *
 * <p>
 * The common handling of {@link InterruptedException} should either throw it,
 * or after some clean-up operation restore the interruption state of the
 * current thread, so that any blocking method could be interrupted again.
 * Especially, a method that can catch, but not throw the exception has to
 * restore the interruption state in order not to lose the interruption signal
 * for its intended recipient.
 *
 * <p>
 * This exception makes handling of such situations a bit easier: when such a
 * method can't handle the {@link InterruptedException} completely (because it
 * is not an actual recipient of the interruption signal that can handle the
 * signal completely), it can just throw {@link InterruptionException} as an
 * unchecked surrogate for the original exception: the interruption state is
 * restored automatically while the exceptional code flow remains in charge.
 *
 * <p>
 * To handle the interruption signal more safely, this class is intentionally
 * final and it may be instantiated or thrown via dedicated methods only that
 * restore the interruption state of the current thread as well.
 *
 * <p>
 * In the summary, let's say that seeing an {@link InterruptionException} is
 * similar to seeing an {@link InterruptedException}, just the interruption
 * state of the current thread should rather be set in the former case, but
 * cleared in the latter case.
 */
public final class InterruptionException extends UncheckedException {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance with the specified cause and a detail message
     * constructed from the cause (if not {@code null}).
     *
     * @param cause
     *            the cause. It must not be {@code null}.
     */
    private InterruptionException(InterruptedException cause) {
        super(cause);
    }

    /**
     * Sets the interruption state of the current thread and throws a new
     * {@link InterruptionException} that carries the given exception as the
     * cause.
     *
     * @param e
     *            the exception to wrap. It must not be {@code null}.
     */
    public static void raise(InterruptedException e) {
        throw signal(e);
    }

    /**
     * Sets the interruption state of the current thread and throws a new
     * {@link InterruptionException}.
     *
     * @return a new {@link InterruptedException}
     */
    public static InterruptionException signal() {
        return signal(new InterruptedException());
    }

    /**
     * Sets the interruption state of the current thread and returns a new
     * {@link InterruptionException} that carries the given exception as the
     * cause.
     *
     * @param e
     *            the exception to wrap. It must not be {@code null}.
     *
     * @return a new {@link InterruptedException}
     */
    public static InterruptionException signal(InterruptedException e) {
        Interruption.renew();
        throw new InterruptionException(e);
    }
}
