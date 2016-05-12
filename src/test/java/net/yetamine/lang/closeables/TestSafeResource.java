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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link SafeResource}.
 */
public final class TestSafeResource {

    /**
     * Tests successful closing.
     */
    @Test
    public void testClose_Success() {
        final AtomicBoolean resource = new AtomicBoolean();
        final SafeResource.Handler<AtomicBoolean> closing = r -> Assert.assertFalse(r.getAndSet(true));

        try (SafeResource<AtomicBoolean> adapter = SafeResource.adapt(resource, closing)) {
            Assert.assertSame(adapter.get(), resource);
            Assert.assertFalse(adapter.get().get());
        }

        Assert.assertTrue(resource.get());
    }

    /**
     * Tests failed closing.
     */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testClose_Failure() {
        final SafeResource.Handler<Object> closing = r -> {
            throw new UnsupportedOperationException();
        };

        try (SafeResource<Object> adapter = SafeResource.adapt(new Object(), closing)) {
            // Do nothing
        }
    }

    /**
     * Tests a {@code null} resource.
     */
    @Test
    public void testNullResource() {
        try (SafeResource<Object> adapter = SafeResource.adapt(null, r -> Assert.fail())) {
            // Do nothing
        }
    }
}
