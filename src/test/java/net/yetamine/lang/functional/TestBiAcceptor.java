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
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link BiAcceptor}.
 */
public final class TestBiAcceptor {

    /**
     * Tests {@link BiAcceptor#onlyIf(BiPredicate)}.
     */
    @Test
    public void testOnlyIf() {
        final BiPredicate<AtomicInteger, Integer> notEqual = (b, i) -> (b.get() != i);
        final BiConsumer<AtomicInteger, Integer> consumer = (b, i) -> b.set(b.get() + i);
        final BiConsumer<AtomicInteger, Integer> conditional = BiAcceptor.from(consumer).onlyIf(notEqual);

        final AtomicInteger value = new AtomicInteger(0);

        conditional.accept(value, 1);
        Assert.assertEquals(value.get(), 1);

        conditional.accept(value, 1);
        Assert.assertEquals(value.get(), 1); // Not incremented second time
    }

    /**
     * Tests {@link BiAcceptor#sequential(Iterable)}.
     */
    @Test
    public void testSequential() {
        final BiConsumer<AtomicInteger, Integer> a1 = (b, i) -> b.set(b.get() + i);
        final BiConsumer<AtomicInteger, Integer> a2 = (b, i) -> b.set(b.get() * i);

        final BiConsumer<AtomicInteger, Integer> a = BiAcceptor.sequential(Arrays.asList(a1, a2));

        final AtomicInteger value = new AtomicInteger(2);
        a.accept(value, 2);
        Assert.assertEquals(value.get(), 8);
    }
}
