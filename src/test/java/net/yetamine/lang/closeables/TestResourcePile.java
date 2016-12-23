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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link ResourcePile}.
 */
public final class TestResourcePile {

    // State constants for simulated resource

    /** State after opening a resource. */
    private static final int OPENED = 0;
    /** State after alternative opening. */
    private static final int INIT = 1;
    /** State after a closing operation. */
    private static final int CLOSED = -1;

    /**
     * Tests closing an empty instance.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testEmpty() throws Exception {
        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            // Do nothing
        }
    }

    /**
     * Tests addopted resources.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testUsing() throws Exception {
        final AtomicInteger resource1 = new AtomicInteger(OPENED);
        final AtomicInteger resource2 = new AtomicInteger(OPENED);
        final AtomicInteger resource3 = new AtomicInteger(OPENED);

        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            final ResourceHandle<AtomicInteger, IOException> handle1 = group.using(resource1);
            Assert.assertSame(handle1.available().get(), resource1);
            Assert.assertSame(handle1.acquired(), resource1);

            handle1.release();
            Assert.assertFalse(handle1.available().isPresent());

            Assert.assertSame(handle1.acquired(), resource1);
            Assert.assertEquals(resource1.get(), OPENED);
            Assert.assertSame(handle1.available().get(), resource1);
            Assert.assertSame(handle1.acquired(), resource1);

            group.using(resource2);
            group.using(resource3).close();
            Assert.assertEquals(resource3.get(), OPENED);
            resource3.set(INIT);
        }

        Assert.assertEquals(resource1.get(), OPENED);
        Assert.assertEquals(resource2.get(), OPENED);
        Assert.assertEquals(resource3.get(), INIT);
    }

    /**
     * Tests addopted resources.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testAdopted() throws Exception {
        final ResourceClosing<AtomicInteger, IOException> closing = r -> r.set(CLOSED);

        final AtomicInteger resource1 = new AtomicInteger(OPENED);
        final AtomicInteger resource2 = new AtomicInteger(OPENED);
        final AtomicInteger resource3 = new AtomicInteger(OPENED);

        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            final ResourceHandle<AtomicInteger, IOException> handle1 = group.adopted(resource1, closing);
            Assert.assertSame(handle1.available().get(), resource1);
            Assert.assertSame(handle1.acquired(), resource1);

            handle1.release();
            Assert.assertFalse(handle1.available().isPresent());

            Assert.assertSame(handle1.acquired(), resource1);
            Assert.assertEquals(resource1.get(), OPENED);
            Assert.assertSame(handle1.available().get(), resource1);
            Assert.assertSame(handle1.acquired(), resource1);

            group.adopted(resource2, closing);
            group.adopted(resource3, closing).close();
            Assert.assertEquals(resource3.get(), CLOSED);
            resource3.set(INIT);
        }

        Assert.assertEquals(resource1.get(), CLOSED);
        Assert.assertEquals(resource2.get(), CLOSED);
        Assert.assertEquals(resource3.get(), INIT);
    }

    /**
     * Tests using a managed resource.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testManaged() throws Exception {
        final AtomicInteger resource1;
        final AtomicInteger resource2;
        final AtomicInteger resource3;
        final AtomicInteger resource4;

        final ResourceClosing<AtomicInteger, IOException> closing = r -> r.set(CLOSED);
        final ResourceOpening<AtomicInteger, IOException> opening = () -> new AtomicInteger(OPENED);
        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            final ResourceHandle<AtomicInteger, IOException> handle1 = group.managed(opening, closing);
            Assert.assertFalse(handle1.available().isPresent());

            final AtomicInteger instance1 = handle1.acquired();
            Assert.assertEquals(instance1.get(), OPENED);
            Assert.assertSame(handle1.available().get(), instance1);

            final AtomicInteger instance2 = handle1.acquired();
            Assert.assertEquals(instance2.get(), OPENED);
            Assert.assertSame(handle1.available().get(), instance2);
            handle1.release(); // Destroy the resource

            Assert.assertEquals(instance2.get(), CLOSED);
            Assert.assertFalse(handle1.available().isPresent());

            resource1 = handle1.acquired();
            Assert.assertNotSame(resource1, instance2);

            final ResourceHandle<AtomicInteger, IOException> handle2 = group.managed(opening, closing);
            resource2 = handle2.acquired();

            final ResourceHandle<AtomicInteger, IOException> handle3 = group.managed(opening, closing);
            resource3 = handle3.acquired();

            resource4 = group.managed(opening, closing).acquired();

            handle2.release();
            Assert.assertEquals(resource2.get(), CLOSED);
            resource2.set(INIT);

            handle3.close();
            Assert.assertEquals(resource3.get(), CLOSED);
            resource3.set(INIT);
        }

        Assert.assertEquals(resource1.get(), CLOSED);
        Assert.assertEquals(resource2.get(), INIT);
        Assert.assertEquals(resource3.get(), INIT);
        Assert.assertEquals(resource4.get(), CLOSED);
    }

    /**
     * Tests a try-block failure situation.
     */
    @Test
    public void testTryFailure() {
        final ResourceClosing<AtomicInteger, IOException> closing = r -> r.set(CLOSED);
        final ResourceOpening<AtomicInteger, IOException> opening = () -> new AtomicInteger(OPENED);

        final AtomicInteger resource1 = new AtomicInteger(OPENED);
        final AtomicInteger resource2 = new AtomicInteger(OPENED);
        AtomicInteger resource3 = new AtomicInteger(INIT); // Prevent NullPointerException
        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            group.adopted(resource1, closing);
            group.adopted(resource2, closing);
            resource3 = group.managed(opening, closing).acquired();
            throw new IOException();
        } catch (IOException e) {
            // Ignore now
        }

        Assert.assertEquals(resource1.get(), CLOSED);
        Assert.assertEquals(resource2.get(), CLOSED);
        Assert.assertEquals(resource3.get(), CLOSED);
    }

    /**
     * Tests situations after closing.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testAfterClose() throws Exception {
        final AtomicInteger resource = new AtomicInteger(INIT);

        final ResourceGroup<IOException> group = new ResourcePile<>();
        final ResourceHandle<AtomicInteger, IOException> handle = group.adopted(resource, r -> r.set(CLOSED));
        group.close();
        Assert.assertEquals(resource.get(), CLOSED);
        Assert.assertFalse(handle.available().isPresent());
        Assert.expectThrows(IllegalStateException.class, () -> handle.acquired()); // Improper after closing

        resource.set(OPENED);
        handle.close();
        Assert.assertEquals(resource.get(), OPENED);

        handle.release();
        Assert.assertEquals(resource.get(), OPENED);

        group.release();
        Assert.assertEquals(resource.get(), OPENED);
    }

    /**
     * Tests a closing failure situation.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testCloseFailure() throws Exception {
        final ResourceOpening<AtomicInteger, IOException> opening = () -> new AtomicInteger(INIT);
        final ResourceClosing<AtomicInteger, IOException> closing = r -> {
            r.set(INIT);
            throw new IOException();
        };

        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            group.adopted(null, closing);
        }

        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            group.managed(opening, closing);
        }

        Assert.expectThrows(IOException.class, () -> {
            try (ResourceGroup<IOException> group = new ResourcePile<>()) {
                group.managed(opening, closing).acquired();
            }
        });

        Assert.expectThrows(IOException.class, () -> {
            try (ResourceGroup<IOException> group = new ResourcePile<>()) {
                group.adopted(new AtomicInteger(), closing).acquired();
            }
        });

        final AtomicInteger resource1 = new AtomicInteger(OPENED);
        final AtomicInteger resource2 = new AtomicInteger(OPENED);
        Assert.expectThrows(IOException.class, () -> {
            try (ResourceGroup<IOException> group = new ResourcePile<>()) {
                group.adopted(resource1, r -> r.set(CLOSED)).acquired();
                group.adopted(resource2, closing).acquired();
            }
        });

        Assert.assertEquals(resource1.get(), CLOSED);
        Assert.assertEquals(resource2.get(), INIT);
    }

    /**
     * Tests a release failure situation.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testReleaseFailure() throws Exception {
        final ResourceOpening<AtomicInteger, IOException> opening = () -> new AtomicInteger(OPENED);
        final ResourceClosing<AtomicInteger, IOException> closing = r -> {
            r.set(INIT);
            throw new IOException();
        };

        // No release with adopted resources
        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            group.adopted(null, closing).release();
        }

        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            final AtomicInteger resource = new AtomicInteger();
            final ResourceHandle<?, IOException> handle = group.adopted(resource, closing);
            handle.release(); // No throwing yet
            Assert.expectThrows(IOException.class, handle::close);
            Assert.assertEquals(resource.get(), INIT);
        }

        // No failures with no actual resource
        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            group.managed(opening, closing).release();
        }

        try (ResourceGroup<IOException> group = new ResourcePile<>()) {
            final ResourceHandle<AtomicInteger, IOException> handle = group.managed(opening, closing);
            final AtomicInteger resource = handle.acquired();
            Assert.expectThrows(IOException.class, handle::release);
            Assert.assertEquals(resource.get(), INIT);
        }
    }
}
