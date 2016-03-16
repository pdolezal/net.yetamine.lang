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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.functional.Consumers;

/**
 * Tests {@link SafeCloseableAdapter}.
 */
public final class TestSafeCloseableAdapter {

    /**
     * Tests using a {@link Consumer}.
     */
    @Test
    public void testConsumer() {
        final AtomicBoolean resource = new AtomicBoolean();
        final Consumer<AtomicBoolean> closing = b -> Assert.assertFalse(b.getAndSet(true));

        final SafeCloseableAdapter<AtomicBoolean> adapter = SafeCloseableAdapter.using(resource, closing);
        Assert.assertSame(adapter.available().get(), resource);
        Assert.assertSame(adapter.resource(), resource);
        Assert.assertFalse(adapter.isClosed());

        adapter.close();
        Assert.assertTrue(adapter.isClosed());
        Assert.assertFalse(adapter.available().isPresent());
    }

    /**
     * Tests a {@code null} resource.
     */
    @Test(expectedExceptions = { NullPointerException.class })
    public void testNullResource() {
        try (SafeCloseableAdapter<Object> adapter = SafeCloseableAdapter.using(null, o -> {
            Assert.fail();
        })) {
            // Do nothing
        }
    }

    /**
     * Tests accessing a resource after closing.
     */
    @Test(expectedExceptions = { IllegalStateException.class })
    public void testClosedAccess() {
        final SafeCloseableAdapter<Object> adapter = SafeCloseableAdapter.using(new Object(), Consumers.ignoring());
        adapter.close();
        Assert.assertFalse(adapter.available().isPresent());
        adapter.resource();
    }

    /**
     * Tests a failure of closing {@link Consumer}.
     */
    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testConsumerClose() {
        final Consumer<Object> closing = o -> {
            throw new IllegalArgumentException();
        };

        try (SafeCloseableAdapter<Object> adapter = SafeCloseableAdapter.using(new Object(), closing)) {
            // Do nothing
        }
    }
}
