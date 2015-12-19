package net.yetamine.lang;

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
