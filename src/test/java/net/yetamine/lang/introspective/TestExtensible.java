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

package net.yetamine.lang.introspective;

import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Extensible}.
 */
public final class TestExtensible implements Extensible {

    /** Unique extension for this test. */
    private static final Object EXTENSION = new Object();

    /**
     * Test {@link Extensible#query(Object)}.
     */
    @Test
    public void testQuery() {
        Assert.assertTrue(Extensible.query(new Object()).extensions().known().isEmpty());
        Assert.assertTrue(Extensible.query(null).extensions().known().isEmpty());
        Assert.assertEquals(Extensible.query(this).extensions(), extensions());
        Assert.assertEquals(Extensible.query(this).extensions().known(), Collections.singleton(EXTENSION));
    }

    /**
     * @see net.yetamine.lang.introspective.Extensible#extensions()
     */
    public Extensions extensions() {
        return Extensions.list(EXTENSION);
    }
}
