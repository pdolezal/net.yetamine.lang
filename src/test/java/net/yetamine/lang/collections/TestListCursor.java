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
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.Box;

/**
 * Tests {@link ListCursor}.
 */
public class TestListCursor {

    /**
     * Tests core functionality of an empty cursor.
     */
    @Test
    public void testWithEmpty() {
        final List<Integer> list = new ArrayList<>();
        final Cursor<Integer> c = ListCursor.create(list);

        Assert.assertFalse(c.isValid());
        c.ifValid(o -> Assert.fail());

        Assert.assertEquals(c.index(), 0);
        Assert.assertEquals(c.limit(), 0);
        Assert.assertEquals(c.next().index(), 1);

        Assert.assertFalse(c.isValid());
        c.ifValid(o -> Assert.fail());

        Assert.assertEquals(c.head().index(), 0);
        Assert.assertEquals(c.last().index(), -1);
        Assert.assertEquals(c.back().index(), -2);

        Assert.assertFalse(c.isValid());
        c.ifValid(o -> Assert.fail());

        Assert.assertEquals(c.move(2).index(), 0);
        Assert.assertEquals(c.index(-10).index(), -10);
        Assert.assertEquals(c.index(100).index(), 100);
        Assert.assertEquals(c.seek(-5).index(), -5);
        Assert.assertEquals(c.seek(5).index(), 5);

        Assert.expectThrows(IndexOutOfBoundsException.class, () -> c.head().get());
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> c.last().accept(1));
    }

    /**
     * Tests {@link Cursor#head()} and {@link Cursor#last()}.
     */
    @Test
    public void testEnds() {
        final List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(9, 7, 8, 6));

        final Cursor<Integer> c = ListCursor.create(list);

        Assert.assertEquals(c.get(), Integer.valueOf(9));
        Assert.assertSame(c.last(), c);
        Assert.assertEquals(c.get(), Integer.valueOf(6));
        Assert.assertSame(c.head(), c);
        Assert.assertEquals(c.get(), Integer.valueOf(9));
    }

    /**
     * Tests {@link Cursor#next()}.
     */
    @Test
    public void testNext() {
        final List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(9, 7, 8, 6));

        final Cursor<Integer> c = ListCursor.create(list);

        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(c.index(), i);
            Assert.assertEquals(c.get(), list.get(i));
            c.next();
        }

        Assert.assertFalse(c.isValid());
        Assert.assertEquals(c.index(), list.size());
        Assert.assertEquals(c.limit(), list.size());
    }

    /**
     * Tests {@link Cursor#back()}.
     */
    @Test
    public void testBack() {
        final List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(9, 7, 8, 6));

        final Cursor<Integer> c = ListCursor.create(list).last();

        for (int i = list.size(); 0 <= --i;) {
            Assert.assertEquals(c.index(), i);
            Assert.assertEquals(c.get(), list.get(i));
            c.back();
        }

        Assert.assertFalse(c.isValid());
        Assert.assertEquals(c.index(), -1);
    }

    /**
     * Tests a number of reading methods.
     */
    @Test
    public void testReading() {
        final List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(9, 7, 8, 6));

        final Cursor<Integer> c = ListCursor.create(list);

        for (int i = list.size(); 0 <= --i;) {
            c.index(i); // Set the index at first!

            Assert.assertEquals(c.index(), i);
            Assert.assertTrue(c.isValid());

            final Integer value = list.get(i);
            Assert.assertEquals(c.get(), value);

            final Box<Integer> out = Box.empty();
            c.ifValid(m -> out.accept(m.get()));
            Assert.assertEquals(out.get(), value);

            c.nonNull().ifPresent(v -> out.accept(v * 2));
            Assert.assertEquals(out.get(), Integer.valueOf(value * 2));

            Assert.assertEquals(c.map(v -> v.toString()), Integer.toString(value));
            Assert.assertEquals(c.stream().collect(Collectors.toList()), Collections.singletonList(value));

            Assert.assertEquals(list.get(i), value);
        }
    }

    /**
     * Tests {@link Cursor#accept(Object)}.
     */
    @Test
    public void testAccept() {
        final List<Integer> list = new ArrayList<>();
        final Cursor<Integer> c = ListCursor.create(list);

        list.add(0);
        c.accept(1);
        Assert.assertEquals(list, Arrays.asList(1));
        c.accept(2);
        Assert.assertEquals(list, Arrays.asList(2));

        list.add(3);
        Assert.assertEquals(list, Arrays.asList(2, 3));
        c.last().accept(4);
        Assert.assertEquals(list, Arrays.asList(2, 4));
    }

    /**
     * Tests compute methods.
     */
    @Test
    public void testCompute() {
        final List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(9, 7, 8, 6));

        for (Cursor<Integer> c = ListCursor.create(list); c.isValid(); c.next()) {
            final int value = list.get(c.index());
            Assert.assertEquals(c.compute(i -> Integer.valueOf(i * 2)), Integer.valueOf(value * 2));
        }

        Assert.assertEquals(list, Arrays.asList(18, 14, 16, 12));

        for (int i = 0; i < list.size(); i++) {
            final int value = list.get(i);
            final Cursor<Integer> c = ListCursor.create(list).index(i);
            Assert.assertEquals(c.compute((f, v) -> Integer.valueOf(f + v), i), Integer.valueOf(i + value));
        }

        Assert.assertEquals(list, Arrays.asList(18, 15, 18, 15));
    }

    /**
     * Tests {@link Cursor#add(Object)}.
     */
    @Test
    public void testAdd() {
        final List<Integer> list = new ArrayList<>();
        final Cursor<Integer> c = ListCursor.create(list);

        c.last().add(1);
        Assert.assertEquals(list, Arrays.asList(1));
        c.add(2).add(3);
        Assert.assertEquals(list, Arrays.asList(3, 2, 1));
        c.last().add(4).head().add(5);
        Assert.assertEquals(list, Arrays.asList(3, 5, 2, 1, 4));

        list.clear();
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> c.head().add(0));
    }

    /**
     * Tests {@link Cursor#append(Object)}.
     */
    @Test
    public void testAppend() {
        final List<Integer> list = new ArrayList<>();
        final Cursor<Integer> c = ListCursor.create(list);

        c.last().append(1);
        Assert.assertEquals(list, Arrays.asList(1));
        c.append(2).append(3);
        Assert.assertEquals(list, Arrays.asList(1, 2, 3));
        c.last().append(4).head().append(5).append(6);
        Assert.assertEquals(list, Arrays.asList(1, 5, 6, 2, 3, 4));

        list.clear();
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> c.head().append(0));
    }

    /**
     * Tests {@link Cursor#insert(Object)}.
     */
    @Test
    public void testInsert() {
        final List<Integer> list = new ArrayList<>();
        final Cursor<Integer> c = ListCursor.create(list);

        c.head().insert(1);
        Assert.assertEquals(list, Arrays.asList(1));
        c.insert(2).insert(3);
        Assert.assertEquals(list, Arrays.asList(3, 2, 1));
        c.last().insert(4).head().insert(5).insert(6);
        Assert.assertEquals(list, Arrays.asList(6, 5, 3, 2, 4, 1));

        list.clear();
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> c.last().insert(0));
    }
}
