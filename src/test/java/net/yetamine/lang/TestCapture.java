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

package net.yetamine.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Capture}.
 */
public final class TestCapture {

    /**
     * Tests {@link Capture#set(java.util.Collection)}.
     */
    @Test
    public void testSet() {
        final Set<Integer> source = new HashSet<>();
        Assert.assertSame(Capture.set(source), Collections.emptySet());

        source.add(1);
        final Set<Integer> copy = new HashSet<>(source);
        final Set<Integer> capture = Capture.set(source);
        Assert.assertEquals(capture, source);
        Assert.assertEquals(capture, copy);
        source.add(2);
        Assert.assertEquals(capture, copy);

        Assert.expectThrows(UnsupportedOperationException.class, () -> capture.add(2));
    }

    /**
     * Tests
     * {@link Capture#sortedSet(java.util.Collection, java.util.function.Function)}.
     */
    @Test
    public void testSortedSet() {
        final SortedSet<Integer> source = new TreeSet<>();
        Assert.assertSame(Capture.sortedSet(source, TreeSet<Integer>::new), Collections.emptySortedSet());

        source.add(1);
        final SortedSet<Integer> copy = new TreeSet<>(source);
        final SortedSet<Integer> capture = Capture.sortedSet(source, TreeSet<Integer>::new);
        Assert.assertEquals(capture, source);
        Assert.assertEquals(capture, copy);
        source.add(2);
        Assert.assertEquals(capture, copy);

        Assert.expectThrows(UnsupportedOperationException.class, () -> capture.add(2));
    }

    /**
     * Tests
     * {@link Capture#navigableSet(java.util.Collection, java.util.function.Function)}.
     */
    @Test
    public void testNavigableSet() {
        final NavigableSet<Integer> source = new TreeSet<>();
        Assert.assertSame(Capture.navigableSet(source, TreeSet<Integer>::new), Collections.emptyNavigableSet());

        source.add(1);
        final NavigableSet<Integer> copy = new TreeSet<>(source);
        final NavigableSet<Integer> capture = Capture.navigableSet(source, TreeSet<Integer>::new);
        Assert.assertEquals(capture, source);
        Assert.assertEquals(capture, copy);
        source.add(2);
        Assert.assertEquals(capture, copy);

        Assert.expectThrows(UnsupportedOperationException.class, () -> capture.add(2));
    }

    /**
     * Tests {@link Capture#list(java.util.Collection)}.
     */
    @Test
    public void testList() {
        final List<Integer> source = new ArrayList<>();
        Assert.assertSame(Capture.list(source), Collections.emptyList());

        source.add(1);
        final List<Integer> copy = new ArrayList<>(source);
        final List<Integer> capture = Capture.list(source);
        Assert.assertEquals(capture, source);
        Assert.assertEquals(capture, copy);
        source.add(2);
        Assert.assertEquals(capture, copy);

        Assert.expectThrows(UnsupportedOperationException.class, () -> capture.add(2));
    }

    /**
     * Tests {@link Capture#map(java.util.Map)}.
     */
    @Test
    public void testMap() {
        final Map<Integer, Integer> source = new HashMap<>();
        Assert.assertSame(Capture.map(source), Collections.emptyMap());

        source.put(1, 2);
        final Map<Integer, Integer> copy = new HashMap<>(source);
        final Map<Integer, Integer> capture = Capture.map(source);
        Assert.assertEquals(capture, source);
        Assert.assertEquals(capture, copy);
        source.put(2, 4);
        Assert.assertEquals(capture, copy);

        Assert.expectThrows(UnsupportedOperationException.class, () -> capture.put(3, 6));
    }

    /**
     * Tests
     * {@link Capture#navigableMap(java.util.NavigableMap, java.util.function.Function)}.
     */
    @Test
    public void testNavigableMap() {
        final NavigableMap<Integer, Integer> source = new TreeMap<>();
        Assert.assertSame(Capture.navigableMap(source, TreeMap<Integer, Integer>::new), Collections.emptySortedMap());

        source.put(1, 2);
        final NavigableMap<Integer, Integer> copy = new TreeMap<>(source);
        final NavigableMap<Integer, Integer> capture = Capture.navigableMap(source, TreeMap<Integer, Integer>::new);
        Assert.assertEquals(capture, source);
        Assert.assertEquals(capture, copy);
        source.put(2, 4);
        Assert.assertEquals(capture, copy);

        Assert.expectThrows(UnsupportedOperationException.class, () -> capture.put(3, 6));
    }

    /**
     * Tests
     * {@link Capture#sortedMap(java.util.SortedMap, java.util.function.Function)}.
     */
    @Test
    public void testSortedMap() {
        final SortedMap<Integer, Integer> source = new TreeMap<>();
        Assert.assertSame(Capture.sortedMap(source, TreeMap<Integer, Integer>::new), Collections.emptySortedMap());

        source.put(1, 2);
        final SortedMap<Integer, Integer> copy = new TreeMap<>(source);
        final SortedMap<Integer, Integer> capture = Capture.sortedMap(source, TreeMap<Integer, Integer>::new);
        Assert.assertEquals(capture, source);
        Assert.assertEquals(capture, copy);
        source.put(2, 4);
        Assert.assertEquals(capture, copy);

        Assert.expectThrows(UnsupportedOperationException.class, () -> capture.put(3, 6));
    }
}
