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
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link UnaryOperation}.
 */
public final class TestUnaryOperation {

    /**
     * Tests {@link UnaryOperation#apply(Object)}.
     */
    @Test
    public void testApply() {
        Assert.assertEquals(UnaryOperation.from((Integer i) -> i + 1).apply(1), Integer.valueOf(2));
    }

    /**
     * Tests {@link UnaryOperation#andNext(Function)}.
     */
    @Test
    public void testAndThen() {
        final UnaryOperation<Integer> operation = i -> i + 1;
        final UnaryOperation<Integer> andThen = operation.andNext(operation);
        Assert.assertEquals(andThen.apply(1), Integer.valueOf(3));
    }

    /**
     * Tests {@link UnaryOperation#onlyIf(java.util.function.Predicate)}.
     */
    @Test
    public void testOnlyIf() {
        // Increments only even numbers
        final UnaryOperation<Integer> even = UnaryOperation.from((Integer i) -> i + 1).onlyIf(i -> (i % 2) == 0);
        Assert.assertEquals(even.apply(1), Integer.valueOf(1));
        Assert.assertEquals(even.apply(2), Integer.valueOf(3));
    }

    /**
     * Tests {@link UnaryOperation#sequential(Iterable)}.
     */
    @Test
    public void testSequential() {
        final UnaryOperation<Integer> a1 = i -> i + 1;
        final UnaryOperation<Integer> a2 = i -> i * i;
        final UnaryOperation<Integer> a3 = UnaryOperation.identity();

        final UnaryOperation<Integer> a = UnaryOperation.sequential(Arrays.asList(a1, a2, a3));
        Assert.assertEquals(a.apply(2), Integer.valueOf(9));
    }
}
