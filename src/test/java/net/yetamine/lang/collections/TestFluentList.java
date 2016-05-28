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

package net.yetamine.lang.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link FluentList}.
 */
public class TestFluentList extends TestFluentCollection {

    /**
     * @see net.yetamine.lang.collections.TestFluentCollection#createFluent()
     */
    @Override
    protected <E> FluentList<E> createFluent() {
        return FluentList.adapt(new ArrayList<>());
    }

    /**
     * Tests the core list methods.
     */
    @Test
    public void testList() {
        final List<Object> c = new ArrayList<>();
        final FluentList<Object> f = FluentList.adapt(c);
        Assert.assertEquals(f.container(), c);
        Assert.assertEquals(f, c);
        Assert.assertEquals(Collections.singleton(f), f.self().stream().collect(Collectors.toSet()));

        Assert.assertSame(c, f.that().map(Function.identity()));
        Assert.assertSame(f, f.that().map(o -> f));
    }

    /**
     * Tests {@link FluentList#index(int)}.
     */
    @Test
    public void testPosition() {
        final FluentList<Object> f = createFluent();

        // Tests empty one
        Assert.assertEquals(f.index(0), 0);
        Assert.assertEquals(f.index(1), 1);
        Assert.assertEquals(f.index(-1), -1);
        Assert.assertEquals(f.index(-2), -2);

        // Tests one with a single element
        f.add(new Object());
        Assert.assertEquals(f.index(0), 0);
        Assert.assertEquals(f.index(1), 1);
        Assert.assertEquals(f.index(-1), 0);
        Assert.assertEquals(f.index(-2), -1);

        // Tests one with several elements
        f.includeMore(new Object(), new Object(), new Object());
        Assert.assertEquals(f.size(), 4);
        Assert.assertEquals(f.index(0), 0);
        Assert.assertEquals(f.index(1), 1);
        Assert.assertEquals(f.index(4), 4);
        Assert.assertEquals(f.index(-1), 3);
        Assert.assertEquals(f.index(-2), 2);
        Assert.assertEquals(f.index(-4), 0);
    }

    /**
     * Tests {@link FluentList#see(int)}.
     */
    @Test
    public void testSee() {
        final FluentList<Object> f = createFluent();

        Assert.expectThrows(IndexOutOfBoundsException.class, () -> f.see(0));

        final Object o = new Object();
        f.add(o);
        Assert.assertSame(f.see(0).get(), o);

        f.add(null);
        Assert.assertFalse(f.see(1).isPresent());
    }

    /**
     * Tests {@link FluentList#head()} and related methods.
     */
    @Test
    public void testHead() {
        final FluentList<Object> f = createFluent();

        // Tests empty one
        Assert.expectThrows(NoSuchElementException.class, () -> f.head());
        Assert.expectThrows(NoSuchElementException.class, () -> f.seeHead());
        Assert.assertFalse(f.peekAtHead().isPresent());

        // Tests non-empty one
        final Object o = new Object();
        f.add(o);

        Assert.assertSame(f.head(), o);
        Assert.assertSame(f.seeHead().get(), o);
        Assert.assertSame(f.peekAtHead().get(), o);
    }

    /**
     * Tests {@link FluentList#last()} and related methods.
     */
    @Test
    public void testLast() {
        final FluentList<Object> f = createFluent();

        // Tests empty one
        Assert.expectThrows(NoSuchElementException.class, () -> f.last());
        Assert.expectThrows(NoSuchElementException.class, () -> f.seeLast());
        Assert.assertFalse(f.peekAtLast().isPresent());

        // Tests non-empty one
        final Object o = new Object();
        f.add(o);

        Assert.assertSame(f.last(), o);
        Assert.assertSame(f.seeLast().get(), o);
        Assert.assertSame(f.peekAtLast().get(), o);
    }

    /**
     * Tests {@link FluentList#peekAt(int)}.
     */
    @Test
    public void testPeekAt() {
        final FluentList<Object> f = createFluent();

        // Tests empty one
        Assert.assertFalse(f.peekAt(0).isPresent());
        Assert.assertFalse(f.peekAt(1).isPresent());
        Assert.assertFalse(f.peekAt(-1).isPresent());

        // Tests non-empty one
        final Object o1 = new Object();
        final Object o2 = new Object();
        f.includeMore(o1, o2);

        Assert.assertSame(f.peekAt(0).get(), o1);
        Assert.assertSame(f.peekAt(1).get(), o2);
        Assert.assertFalse(f.peekAt(-1).isPresent());
        Assert.assertFalse(f.peekAt(f.size()).isPresent());
    }

    /**
     * Tests {@link FluentList#patch(int, Function)}.
     */
    @Test
    public void testPatch() {
        final FluentList<Integer> f = createFluent();
        IntStream.range(0, 10).forEachOrdered(f::add);

        final Set<Integer> indices = new HashSet<>(Arrays.asList(0, 1, 2, f.size() - 1));
        indices.forEach(i -> f.patch(i, v -> v + 1));

        IntStream.range(0, f.size()).filter(indices::contains).forEach(i -> {
            Assert.assertEquals((int) f.get(i), i + 1);
        });

        IntStream.range(0, f.size()).filter(i -> !indices.contains(i)).forEach(i -> {
            Assert.assertEquals((int) f.get(i), i);
        });
    }

    /**
     * Tests {@link FluentList#patchAll(Function)}.
     */
    @Test
    public void testPatchAll() {
        final FluentList<Integer> f = createFluent();
        IntStream.range(0, 10).forEachOrdered(f::add);

        f.patchAll(i -> i + 1);

        IntStream.range(0, f.size()).forEach(i -> {
            Assert.assertEquals((int) f.get(i), i + 1);
        });
    }
}
