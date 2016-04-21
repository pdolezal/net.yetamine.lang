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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link FluentCollection}.
 *
 * <p>
 * The implementation is written, so it could be used for both lists and sets if
 * the underlying set respects the insertion order. To run the original tests as
 * a part of the new test set, override {@link #createFluent()}.
 */
public class TestFluentCollection {

    /**
     * Creates a new blank (mutable) collection.
     *
     * <p>
     * The default implementation uses {@link ArrayList} as the underlying
     * implementation.
     *
     * @param <E>
     *            the type of the elements
     *
     * @return the collection adapter
     */
    protected <E> FluentCollection<E> createFluent() {
        return FluentCollection.adapt(new ArrayList<>());
    }

    /**
     * Tests the core collection methods.
     */
    @Test
    public void testCollection() {
        final Collection<Object> c = new ArrayList<>();

        final FluentCollection<Object> f = FluentCollection.adapt(c);
        Assert.assertEquals(f.container(), c);
        Assert.assertEquals(f, c);
        Assert.assertEquals(Collections.singleton(f), f.self().collect(Collectors.toSet()));

        Assert.assertSame(c, f.withCollection(Function.identity()));
        Assert.assertSame(f, f.withCollection(o -> f));
    }

    /**
     * Tests {@link FluentCollection#some()} and its friends.
     */
    @Test
    public void testSome() {
        final FluentCollection<String> empty = createFluent();
        final FluentCollection<String> one = createFluent();
        final FluentCollection<String> two = createFluent();

        one.add("one");
        two.add("one");
        two.add("two");

        Assert.expectThrows(NoSuchElementException.class, () -> empty.some());
        Assert.assertEquals("one", one.some());
        Assert.assertEquals("one", two.some());

        Assert.assertFalse(empty.peekAtSome().isPresent());
        Assert.assertEquals("one", one.peekAtSome().get());
        Assert.assertEquals("one", two.peekAtSome().get());
    }

    /**
     * Tests {@link FluentCollection#include(Object)}.
     */
    @Test
    public void testInclude() {
        final FluentCollection<String> f = createFluent();

        Assert.assertFalse(f.contains("hello"));
        Assert.assertSame(f.include("hello"), f);
        Assert.assertTrue(f.contains("hello"));
    }

    /**
     * Tests {@link FluentCollection#includeMore(Object...)}.
     */
    @Test
    public void testIncludeMore() {
        final FluentCollection<String> f = createFluent();

        Assert.assertFalse(f.contains("Hello"));
        Assert.assertFalse(f.contains("Dolly"));
        Assert.assertSame(f.includeMore("Hello", "Dolly"), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "Dolly"));
        Assert.assertSame(f.includeMore("!", "!!"), f);
        Assert.assertEquals(f, Arrays.asList("Hello", "Dolly", "!", "!!"));
    }

    /**
     * Tests {@link FluentCollection#includeAll(Collection)}.
     */
    @Test
    public void testIncludeAll() {
        final FluentCollection<String> f = createFluent();

        Assert.assertFalse(f.contains("Hello"));
        Assert.assertFalse(f.contains("Dolly"));
        Assert.assertSame(f.includeAll(Arrays.asList("Hello", "Dolly")), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "Dolly"));
        Assert.assertSame(f.includeAll(Arrays.asList("!", "!!")), f);
        Assert.assertEquals(f, Arrays.asList("Hello", "Dolly", "!", "!!"));
    }

    /**
     * Tests {@link FluentCollection#contain(Object)}.
     */
    @Test
    public void testContain() {
        final FluentCollection<String> f = createFluent();

        Assert.assertFalse(f.contains("hello"));
        Assert.assertSame(f.contain("hello"), f);
        Assert.assertTrue(f.contains("hello"));
        Assert.assertSame(f.contain("hello"), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("hello"));
    }

    /**
     * Tests {@link FluentCollection#containMore(Object...)}.
     */
    @Test
    public void testContainMore() {
        final FluentCollection<String> f = createFluent();

        Assert.assertFalse(f.contains("Hello"));
        Assert.assertFalse(f.contains("Dolly"));
        Assert.assertSame(f.containMore("Hello", "Dolly"), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "Dolly"));
        Assert.assertSame(f.containMore("!", "!!"), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "Dolly", "!", "!!"));
        Assert.assertSame(f.containMore("!", "!!"), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "Dolly", "!", "!!"));
    }

    /**
     * Tests {@link FluentCollection#discard(Object)}.
     */
    @Test
    public void testDiscard() {
        final FluentCollection<String> f = createFluent();

        f.includeMore("Hello", "Dolly", "!");
        Assert.assertSame(f.discard("Dolly"), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "!"));
    }

    /**
     * Tests {@link FluentCollection#discardIf(java.util.function.Predicate)}.
     */
    @Test
    public void testDiscardIf() {
        final FluentCollection<String> f = createFluent();

        f.includeMore("Hello", "Dolly", "!");
        Assert.assertSame(f.discardIf(s -> s.length() == 1), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "Dolly"));
    }

    /**
     * Tests {@link FluentCollection#discardAll()}.
     */
    @Test
    public void testDiscardAll() {
        final FluentCollection<String> f = createFluent();

        f.includeMore("Hello", "Dolly", "!");
        Assert.assertSame(f.discardAll(Arrays.asList("Hello", "Dolly")), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Collections.singletonList("!"));
    }

    /**
     * Tests {@link FluentCollection#preserveAll(Collection)}.
     */
    @Test
    public void testPreserveAll() {
        final FluentCollection<String> f = createFluent();

        f.includeMore("Hello", "Dolly", "!");
        Assert.assertSame(f.preserveAll(Arrays.asList("Hello", "Little", "Dolly")), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), Arrays.asList("Hello", "Dolly"));
    }

    /**
     * Tests {@link FluentCollection#preserveAll(Collection)}.
     */
    @Test
    public void testForAll() {
        final FluentCollection<String> f = createFluent();
        final List<String> l = new ArrayList<>();

        final List<String> d = Arrays.asList("Hello", "Dolly", "!");
        Assert.assertSame(f.includeAll(d).forAll(l::add), f);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), d);
        Assert.assertEquals(f.stream().collect(Collectors.toList()), l);
    }
}
