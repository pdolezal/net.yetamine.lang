package net.yetamine.lang.closeables;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link AutoCloseableAdapter}.
 */
public final class TestAutoCloseableAdapter {

    /**
     * Tests using a {@link Consumer}.
     *
     * @throws IOException
     *             if something fails
     */
    @Test
    public void testConsumer() throws IOException {
        final AtomicBoolean resource = new AtomicBoolean();
        final Consumer<AtomicBoolean> closing = b -> Assert.assertFalse(b.getAndSet(true));

        final AutoCloseableAdapter<AtomicBoolean, IOException> adapter = AutoCloseableAdapter.using(resource, closing);
        Assert.assertSame(adapter.available().get(), resource);
        Assert.assertSame(adapter.resource(), resource);
        Assert.assertFalse(adapter.isClosed());

        adapter.close();
        Assert.assertTrue(adapter.isClosed());
        Assert.assertFalse(adapter.available().isPresent());
    }

    /**
     * Tests using a {@link Function}.
     *
     * @throws IOException
     *             if something fails
     */
    @Test
    public void testFunction() throws IOException {
        final AtomicBoolean resource = new AtomicBoolean();
        final Function<AtomicBoolean, IOException> closing = b -> {
            Assert.assertFalse(b.getAndSet(true));
            return null;
        };

        final AutoCloseableAdapter<AtomicBoolean, IOException> adapter = AutoCloseableAdapter.using(resource, closing);
        Assert.assertSame(adapter.available().get(), resource);
        Assert.assertSame(adapter.resource(), resource);
        Assert.assertFalse(adapter.isClosed());

        adapter.close();
        Assert.assertTrue(adapter.isClosed());
        Assert.assertFalse(adapter.available().isPresent());
    }

    /**
     * Tests a {@code null} resource.
     *
     * @throws IOException
     *             if something fails
     */
    @Test(expectedExceptions = { NullPointerException.class })
    public void testNullResource() throws IOException {
        final Consumer<Object> closing = o -> {
            Assert.fail();
        };

        try (AutoCloseableAdapter<Object, IOException> adapter = AutoCloseableAdapter.using(null, closing)) {
            // Do nothing
        }
    }

    /**
     * Tests accessing a resource after closing.
     *
     * @throws IOException
     *             if something fails
     */
    @Test(expectedExceptions = { IllegalStateException.class })
    public void testClosedAccess() throws IOException {
        final Function<Object, IOException> closing = o -> null;
        final AutoCloseableAdapter<Object, IOException> adapter = AutoCloseableAdapter.using(new Object(), closing);
        adapter.close();
        Assert.assertFalse(adapter.available().isPresent());
        adapter.resource();
    }

    /**
     * Tests a failure of closing {@link Consumer}.
     *
     * @throws IOException
     *             if something fails
     */
    @Test(expectedExceptions = { NoSuchElementException.class })
    public void testConsumerClose() throws IOException {
        final Consumer<Object> closing = o -> {
            throw new NoSuchElementException();
        };

        try (AutoCloseableAdapter<Object, IOException> adapter = AutoCloseableAdapter.using(new Object(), closing)) {
            // Do nothing
        }
    }

    /**
     * Tests a failure due to {@link Function} returning an exception.
     *
     * @throws IOException
     *             if something fails
     */
    @Test(expectedExceptions = { IOException.class })
    public void testFunctionClose() throws IOException {
        final Function<Object, IOException> closing = o -> new IOException();
        try (AutoCloseableAdapter<Object, IOException> adapter = AutoCloseableAdapter.using(new Object(), closing)) {
            // Do nothing
        }
    }
}
