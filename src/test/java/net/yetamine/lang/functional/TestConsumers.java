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
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.Box;

/**
 * Tests {@link Consumers}.
 */
public final class TestConsumers {

    /**
     * Tests conditional consumers.
     */
    @Test
    public void testConditional() {
        final Predicate<Box<Integer>> even = b -> (b.get() % 2) == 0;
        final Consumer<Box<Integer>> consumer = b -> b.patch(i -> i + 1);
        final Consumer<Box<Integer>> conditional = Consumers.conditional(even, consumer);

        final Box<Integer> value = Box.of(0);

        conditional.accept(value);
        Assert.assertEquals(value.get(), Integer.valueOf(1));

        conditional.accept(value);
        Assert.assertEquals(value.get(), Integer.valueOf(1)); // Not incremented second time
    }

    /**
     * Tests {@link Consumers#sequential(Iterable)}.
     */
    @Test
    public void testSequential() {
        final Consumer<Box<Integer>> a1 = b -> b.patch(i -> i + 1);
        final Consumer<Box<Integer>> a2 = b -> b.patch(i -> i * i);

        final Consumer<Box<Integer>> a = Consumers.sequential(Arrays.asList(a1, a2));

        final Box<Integer> value = Box.of(2);
        a.accept(value);
        Assert.assertEquals(value.get(), Integer.valueOf(9));
    }
}
