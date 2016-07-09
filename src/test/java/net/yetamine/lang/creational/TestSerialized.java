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

package net.yetamine.lang.creational;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link Serialized}.
 */
public final class TestSerialized {

    /**
     * Tests successful serialization.
     */
    @Test
    public void testSerializable() {
        final String source = "Hello";
        final Serialized<String> serialized = new Serialized<>(source);

        Assert.assertNotSame(serialized.build(), source);
        Assert.assertEquals(serialized.build(), source);
    }

    /**
     * Tests non-serializable source.
     */
    @Test(expectedExceptions = { UnsupportedOperationException.class })
    public void testUnsupported() {
        new Serialized<>(new Object()).build();
    }
}
