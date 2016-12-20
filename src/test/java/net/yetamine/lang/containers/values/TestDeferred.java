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

package net.yetamine.lang.containers.values;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.values.Deferred;

/**
 * Tests {@link Deferred}.
 */
public final class TestDeferred {

    /**
     * Tests caching and invalidation of the value.
     */
    @Test
    public void test() {
        final Deferred<?> v = new Deferred<>(Object::new);

        final Object o = v.get();
        Assert.assertSame(v.get(), o);

        v.invalidate();
        final Object p = v.get();
        Assert.assertNotSame(p, o);
        Assert.assertSame(v.get(), p);
    }
}
