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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link FluentSet}.
 */
public class TestFluentSet extends TestFluentCollection {

    /**
     * @see net.yetamine.lang.collections.TestFluentCollection#createFluent()
     */
    @Override
    protected <E> FluentSet<E> createFluent() {
        return FluentSet.adapt(new LinkedHashSet<>());
    }

    /**
     * Tests the core set methods.
     */
    @Test
    public void testSet() {
        final Set<Object> c = new HashSet<>();
        final FluentSet<Object> f = FluentSet.adapt(c);
        Assert.assertEquals(f.container(), c);
        Assert.assertEquals(f, c);
        Assert.assertEquals(Collections.singleton(f), f.self().stream().collect(Collectors.toSet()));

        Assert.assertSame(c, f.that().map(Function.identity()));
        Assert.assertSame(f, f.that().map(o -> f));
    }
}
