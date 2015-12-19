package net.yetamine.lang.creational;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Cloneables}.
 */
public final class TestCloneables {

    /**
     * A helper private exception that can't definitely be used elsewhere.
     */
    private static final class FailingException extends RuntimeException {

        /** Serialization version: 1 */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance.
         *
         * @param cause
         *            the cause
         */
        public FailingException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Tests cloning in the successful case.
     *
     * @throws CloneNotSupportedException
     *             if something goes wrong
     */
    @Test
    public void testObjectCloningSuccessful() throws CloneNotSupportedException {
        final ArrayList<Object> source = new ArrayList<>();
        source.addAll(Arrays.asList(new Object(), null, new Object()));

        { // Verification test
            final Object clone = source.clone();
            Assert.assertEquals(clone, source);
            Assert.assertNotSame(clone, source);
        }

        { // Test default clone
            final ArrayList<Object> clone = Cloneables.clone(source);
            Assert.assertNotSame(clone, source);
            Assert.assertEquals(clone, source);
        }

        { // Test mapping clone
            final ArrayList<Object> clone = Cloneables.clone(source, RuntimeException::new);
            Assert.assertNotSame(clone, source);
            Assert.assertEquals(clone, source);
        }

        { // Test prototype clone
            final ArrayList<Object> clone = Cloneables.prototype(source).build();
            Assert.assertNotSame(clone, source);
            Assert.assertEquals(clone, source);
        }
    }

    /**
     * Tests array cloning in the successful case.
     *
     * @throws CloneNotSupportedException
     *             if something goes wrong
     */
    @Test
    public void testArrayCloningSuccessful() throws CloneNotSupportedException {
        final Integer[] source = { 1, 2, 3 };

        { // Verification test
            final Integer[] clone = source.clone();
            Assert.assertNotSame(clone, source);
            Assert.assertEquals(clone, source);
        }

        { // Test default clone
            final Integer[] clone = Cloneables.clone(source);
            Assert.assertNotSame(clone, source);
            Assert.assertEquals(clone, source);
        }

        { // Test mapping clone
            final Integer[] clone = Cloneables.clone(source, RuntimeException::new);
            Assert.assertNotSame(clone, source);
            Assert.assertEquals(clone, source);
        }

        { // Test prototype clone
            final Integer[] clone = Cloneables.prototype(source).build();
            Assert.assertNotSame(clone, source);
            Assert.assertEquals(clone, source);
        }
    }

    /**
     * Tests cloning failure.
     *
     * @throws CloneNotSupportedException
     *             if cloning fails
     */
    @Test(expectedExceptions = { CloneNotSupportedException.class })
    public void testCloningFailure1() throws CloneNotSupportedException {
        Cloneables.clone(new Object());
    }

    /**
     * Tests cloning failure.
     */
    @Test(expectedExceptions = { FailingException.class })
    public void testCloningFailure2() {
        Cloneables.clone(new Object(), FailingException::new);
    }

    /**
     * Tests cloning failure.
     */
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testCloningFailure3() {
        Cloneables.prototype(new Object()).build();
    }
}
