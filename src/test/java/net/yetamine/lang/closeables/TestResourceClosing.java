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

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link ResourceClosing}.
 */
public final class TestResourceClosing {

    /**
     * Tests {@link ResourceClosing#closeAll(Object...)}.
     */
    @Test
    public void testCloseAll_Success() {
        final AtomicInteger r1 = new AtomicInteger(1);
        final AtomicInteger r2 = new AtomicInteger(2);
        final AtomicInteger r3 = new AtomicInteger(3);

        ResourceClosing.<AtomicInteger, RuntimeException> from(r -> r.set(0)).closeAll(r1, r2, r3);

        Assert.assertEquals(0, r1.get());
        Assert.assertEquals(0, r2.get());
        Assert.assertEquals(0, r3.get());
    }

    /**
     * Tests {@link ResourceClosing#closeAll(Object...)}.
     */
    @Test
    public void testCloseAll_Failure() {
        final AtomicInteger r1 = new AtomicInteger(1);
        final AtomicInteger r2 = new AtomicInteger(2);
        final AtomicInteger r3 = new AtomicInteger(3);

        Assert.expectThrows(RuntimeException.class, () -> {
            ResourceClosing.from((AtomicInteger r) -> {
                if (r.getAndSet(0) == 2) {
                    throw new RuntimeException();
                }
            }).closeAll(r1, r2, r3);
        });

        Assert.assertEquals(0, r1.get());
        Assert.assertEquals(0, r2.get());
        Assert.assertEquals(0, r3.get());
    }
}
