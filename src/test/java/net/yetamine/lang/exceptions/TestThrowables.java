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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Throwables}.
 */
public final class TestThrowables {

    /**
     * A helper private exception that can't definitely be used elsewhere.
     */
    private static final class FailingException extends Exception {

        /** Serialization version: 1 */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance.
         */
        public FailingException() {
            // Default constructor
        }
    }

    /**
     * Tests {@link Throwables#raise(Throwable)}.
     *
     * @throws FailingException
     *             if tests succeeds
     */
    @Test(expectedExceptions = { FailingException.class })
    public void testRaise() throws FailingException {
        Throwables.raise(new FailingException());
    }

    /**
     * Tests {@link Throwables#init(Throwable, Throwable)}.
     */
    @Test
    public void testInit() {
        final FailingException e = new FailingException();
        final Throwable cause = new IllegalArgumentException();
        final FailingException result = Throwables.init(e, cause);
        Assert.assertSame(result.getCause(), cause);
        Assert.assertSame(result, e);
    }

    /**
     * Tests {@link Throwables#init(Throwable, Throwable)} with {@code null}
     * exception to initialize.
     */
    @Test(expectedExceptions = { AssertionError.class })
    public void testWrong() {
        Throwables.init(null, new RuntimeException());
    }

    /**
     * Tests {@link Throwables#init(Throwable, Throwable)} with {@code null}
     * cause.
     */
    @Test
    public void testNull() {
        Assert.assertNull(Throwables.init(new RuntimeException(), null).getCause());
    }
}
