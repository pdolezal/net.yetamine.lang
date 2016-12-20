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

package net.yetamine.lang.functional;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Acceptor}.
 */
public final class TestAcceptor {

    /**
     * Tests {@link Acceptor#apply(Object)}.
     */
    @Test
    public void testApply() {
        // This Acceptor increments the value in the given box
        final Acceptor<AtomicInteger> increment = Acceptor.from(b -> b.set(b.get() + 1));
        final AtomicInteger value = new AtomicInteger(1);
        final AtomicInteger result = increment.apply(value);
        Assert.assertSame(result, value);
        Assert.assertEquals(result.get(), 2);
    }

    /**
     * Tests {@link Acceptor#andThen(java.util.function.Consumer)}.
     */
    @Test
    public void testAndThen() {
        final Acceptor<AtomicInteger> acceptor = b -> b.set(b.get() + 1);
        final Acceptor<AtomicInteger> andThen = acceptor.andThen(acceptor);

        final AtomicInteger value = new AtomicInteger(1);
        final AtomicInteger result = andThen.apply(value);
        Assert.assertSame(result, value);
        Assert.assertEquals(result.get(), 3);
    }

    /**
     * Tests {@link Acceptor#onlyIf(java.util.function.Predicate)}.
     */
    @Test
    public void testOnlyIf() {
        final Acceptor<AtomicInteger> acceptor = b -> b.set(b.get() + 1);

        // Increments only even numbers
        final Acceptor<AtomicInteger> even = acceptor.onlyIf(b -> (b.get() % 2) == 0);

        final AtomicInteger value = new AtomicInteger(0);
        Assert.assertEquals(even.apply(value).get(), 1);
        Assert.assertEquals(even.apply(value).get(), 1); // Not incremented second time
    }

    /**
     * Tests {@link Acceptor#finish(Function)}.
     */
    @Test
    public void testFinish() {
        final Acceptor<AtomicReference<Object>> acceptor = b -> b.set(new Object());
        final Function<AtomicReference<Object>, Object> function = acceptor.finish(AtomicReference::get);

        final AtomicReference<Object> value = new AtomicReference<>();
        Assert.assertSame(function.apply(value), value.get());
    }

    /**
     * Tests {@link Acceptor#sequential(Iterable)}.
     */
    @Test
    public void testSequential() {
        final Acceptor<AtomicInteger> a1 = b -> b.set(b.get() + 1);
        final Acceptor<AtomicInteger> a2 = b -> b.set(b.get() * 2);

        final Acceptor<AtomicInteger> a = Acceptor.sequential(Arrays.asList(a1, a2));
        Assert.assertEquals(a.apply(new AtomicInteger(2)).get(), 6);
    }
}
