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
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link AutoResource}.
 */
public final class TestAutoResource {

    /**
     * Tests successful closing.
     *
     * @throws IOException
     *             if something fails
     */
    @Test
    public void testClose_Success() throws IOException {
        final AtomicBoolean resource = new AtomicBoolean();
        final AutoResource.Handler<AtomicBoolean, IOException> closing = r -> Assert.assertFalse(r.getAndSet(true));

        try (AutoResource<AtomicBoolean, IOException> adapter = AutoResource.adapt(resource, closing)) {
            Assert.assertSame(adapter.get(), resource);
            Assert.assertFalse(adapter.get().get());
        }

        Assert.assertTrue(resource.get());
    }

    /**
     * Tests failed closing.
     *
     * @throws IOException
     *             if something fails
     */
    @Test(expectedExceptions = { IOException.class })
    public void testClose_Failure() throws IOException {
        final AutoResource.Handler<Object, IOException> closing = r -> {
            throw new IOException();
        };

        try (AutoResource<Object, IOException> adapter = AutoResource.adapt(new Object(), closing)) {
            // Do nothing
        }
    }

    /**
     * Tests a {@code null} resource.
     *
     * @throws IOException
     *             if something fails
     */
    @Test
    public void testNullResource() throws IOException {
        try (AutoResource<Object, IOException> adapter = AutoResource.adapt(null, r -> Assert.fail())) {
            // Do nothing
        }
    }
}
