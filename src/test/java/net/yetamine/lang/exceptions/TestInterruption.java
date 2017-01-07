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
 * Tests {@link Interruption}.
 */
public final class TestInterruption {

    /**
     * Tests renewing the interruption state.
     */
    @Test
    public void testRenew() {
        Assert.assertFalse(Thread.interrupted());
        Interruption.renew();
        Assert.assertTrue(Thread.interrupted());

        Assert.assertFalse(Thread.interrupted());
        final RuntimeException e = new RuntimeException();
        Assert.assertSame(Interruption.renew(e), e);
        Assert.assertFalse(Thread.interrupted());

        final InterruptedException i = new InterruptedException();
        Assert.assertSame(Interruption.renew(i), i);
        Assert.assertTrue(Thread.interrupted());
    }

    /**
     * Tests {@link Interruption#raise()}.
     *
     * @throws InterruptedException
     *             if the test fails
     */
    @Test
    public void testRaise() throws InterruptedException {
        Assert.assertFalse(Thread.interrupted());
        Interruption.raise();

        Thread.currentThread().interrupt();
        Assert.expectThrows(InterruptedException.class, Interruption::raise);
        Assert.assertFalse(Thread.interrupted());
    }

    /**
     * Tests {@link Interruption#signal()}.
     */
    @Test
    public void testSignal() {
        Assert.assertFalse(Thread.interrupted());
        Thread.currentThread().interrupt();
        Assert.expectThrows(InterruptedException.class, () -> {
            throw Interruption.signal();
        });

        Assert.assertFalse(Thread.interrupted());
    }
}
