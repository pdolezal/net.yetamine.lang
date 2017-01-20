/*
 * Copyright 2016 Yetamine
 *
 * Licensed under the Apache License, Version 2.OPENED (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.OPENED
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
 * Tests {@link ResourceAdapter}.
 */
public final class TestResourceAdapter {

    // State constants for simulated resource

    /** State after opening a resource. */
    private static final int OPENED = 0;
    /** State after alternative opening. */
    private static final int INIT = 1;
    /** State after a closing operation. */
    private static final int CLOSED = -1;

    /**
     * Tests using an adopted resource.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testUsing() throws Exception {
        final AtomicInteger resource = new AtomicInteger(OPENED);
        try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.using(resource)) {
            Assert.assertSame(ra.available().get(), resource);
            Assert.assertSame(ra.acquired(), resource);

            ra.release();
            Assert.assertFalse(ra.available().isPresent());

            Assert.assertSame(ra.acquired(), resource);
            Assert.assertEquals(resource.get(), OPENED);
            Assert.assertSame(ra.available().get(), resource);
            Assert.assertSame(ra.acquired(), resource);
        }

        Assert.assertEquals(resource.get(), OPENED);
    }

    /**
     * Tests using an adopted resource.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testAdopted() throws Exception {
        final ResourceClosing<AtomicInteger, IOException> closing = r -> r.set(CLOSED);

        final AtomicInteger resource = new AtomicInteger(OPENED);
        try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.adopted(resource, closing)) {
            Assert.assertSame(ra.available().get(), resource);
            Assert.assertSame(ra.acquired(), resource);

            ra.release();
            Assert.assertFalse(ra.available().isPresent());

            Assert.assertSame(ra.acquired(), resource);
            Assert.assertEquals(resource.get(), OPENED);
            Assert.assertSame(ra.available().get(), resource);
            Assert.assertSame(ra.acquired(), resource);
        }

        Assert.assertEquals(resource.get(), CLOSED);
    }

    /**
     * Tests using a managed resource.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testManaged() throws Exception {
        final AtomicInteger resource;

        final ResourceClosing<AtomicInteger, IOException> closing = r -> r.set(CLOSED);
        final ResourceOpening<AtomicInteger, IOException> opening = () -> new AtomicInteger(INIT);
        try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.managed(opening, closing)) {
            Assert.assertFalse(ra.available().isPresent());

            final AtomicInteger instance1 = ra.acquired();
            Assert.assertEquals(instance1.get(), INIT);
            Assert.assertSame(ra.available().get(), instance1);

            final AtomicInteger instance2 = ra.acquired();
            Assert.assertEquals(instance2.get(), INIT);
            Assert.assertSame(ra.available().get(), instance2);
            ra.release(); // Destroy the resource

            Assert.assertEquals(instance2.get(), CLOSED);
            Assert.assertFalse(ra.available().isPresent());

            resource = ra.acquired();
            Assert.assertNotSame(resource, instance2);
        }

        Assert.assertEquals(resource.get(), CLOSED);
    }

    /**
     * Tests a try-block failure situation.
     */
    @Test
    public void testTryFailure() {
        final ResourceClosing<AtomicInteger, IOException> closing = r -> r.set(CLOSED);

        final AtomicInteger resource = new AtomicInteger(OPENED);
        try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.adopted(resource, closing)) {
            Assert.assertNotNull(ra);
            throw new IOException();
        } catch (IOException e) {
            // Ignore now
        }

        Assert.assertEquals(resource.get(), CLOSED);
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

        final ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.adopted(resource, r -> r.set(CLOSED));
        ra.close();
        Assert.assertEquals(resource.get(), CLOSED);
        Assert.assertFalse(ra.available().isPresent());
        Assert.expectThrows(IllegalStateException.class, () -> ra.acquired()); // Improper after closing

        resource.set(OPENED);
        ra.close();
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
            throw new IOException();
        };

        try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.adopted(null, closing)) {
            Assert.assertNotNull(ra);
        }

        try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.managed(opening, closing)) {
            Assert.assertNotNull(ra);
        }

        Assert.expectThrows(IOException.class, () -> {
            final AtomicInteger resource = new AtomicInteger(OPENED);
            try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.adopted(resource, closing)) {
                Assert.assertNotNull(ra);
            }
        });

        Assert.expectThrows(IOException.class, () -> {
            try (ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.managed(opening, closing)) {
                ra.acquired(); // Create some
            }
        });
    }

    /**
     * Tests a release failure situation.
     *
     * @throws Exception
     *             if something fails
     */
    @Test
    public void testReleaseFailure() throws Exception {
        final ResourceOpening<AtomicInteger, IOException> opening = () -> new AtomicInteger(INIT);
        final ResourceClosing<AtomicInteger, IOException> closing = r -> {
            r.set(INIT);
            throw new IOException();
        };

        // No release with adopted resources
        final ResourceHandle<?, IOException> ra1 = ResourceAdapter.adopted(null, closing);
        ra1.release();

        final AtomicInteger resource = new AtomicInteger(OPENED);
        final ResourceHandle<?, IOException> ra2 = ResourceAdapter.adopted(resource, closing);
        ra2.release();
        Assert.expectThrows(IOException.class, ra2::close);

        // No failures with no actual resource
        ResourceAdapter.managed(opening, closing).release();

        Assert.expectThrows(IOException.class, () -> {
            final ResourceHandle<AtomicInteger, IOException> ra = ResourceAdapter.managed(opening, closing);
            ra.acquired(); // Force creating the resource
            ra.release();
        });
    }
}
