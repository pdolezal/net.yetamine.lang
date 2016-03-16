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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link Singleton}.
 */
public final class TestSingleton {

    /**
     * Testing singleton implementation.
     */
    public static final class TestingSingleton extends Singleton {

        /** Serialization version: 1 */
        private static final long serialVersionUID = 1L;

        /** Sole instance of the singleton. */
        private static final TestingSingleton INSTANCE = new TestingSingleton();

        /**
         * Creates a new instance.
         */
        private TestingSingleton() {
            // Default constructor
        }

        /**
         * Provides an instance.
         *
         * @return an instance
         */
        @Singleton.AccessPoint
        public static TestingSingleton getInstance() {
            return INSTANCE;
        }
    }

    /**
     * Tests singleton serialization.
     *
     * @throws IOException
     *             if something fails
     * @throws ClassNotFoundException
     *             if something fails
     */
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final TestingSingleton instance = TestingSingleton.getInstance();

        Assert.assertNotNull(instance);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(instance);
        }

        final TestingSingleton reconstructed;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            reconstructed = (TestingSingleton) ois.readObject();
        }

        Assert.assertSame(reconstructed, instance);
    }

    /**
     * Tests singleton lookup functionality.
     */
    @Test
    public void testLookup() {
        final TestingSingleton instance = TestingSingleton.getInstance();

        Assert.assertSame(Singleton.Instance.lookup(TestingSingleton.class), instance);
        Assert.assertSame(Singleton.Instance.lookup(TestSingleton.class, TestingSingleton.class, ""), instance);
        Assert.assertNull(Singleton.Instance.lookup(TestSingleton.class, TestingSingleton.class, "null"));
    }

    /**
     * Tests singleton lookup failure.
     *
     * @param provider
     *            the provider of the singleton
     * @param type
     *            the type of the singleton
     * @param identifier
     *            the identifier of the access point
     */
    @Test(expectedExceptions = { IllegalArgumentException.class }, dataProvider = "missingLookup")
    public void testLookupMiss(Class<?> provider, Class<?> type, String identifier) {
        Singleton.Instance.lookup(provider, type, identifier);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "missingLookup")
    public static Object[][] missingLookup() {
        return new Object[][] {
            // @formatter:off
            { TestingSingleton.class,   TestingSingleton.class, "missing"   },
            { TestSingleton.class,      TestingSingleton.class, "missing"   },
            { Object.class,             Object.class,           ""          }
            // @formatter:on
        };
    }

    /**
     * Tests singleton lookup failure.
     *
     * @param provider
     *            the provider of the singleton
     * @param type
     *            the type of the singleton
     * @param identifier
     *            the identifier of the access point
     */
    @Test(expectedExceptions = { ClassCastException.class }, dataProvider = "failingLookup")
    public void testLookupFail(Class<?> provider, Class<?> type, String identifier) {
        Singleton.Instance.lookup(provider, type, identifier);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "failingLookup")
    public static Object[][] failingLookup() {
        return new Object[][] {
            // @formatter:off
            { TestingSingleton.class,   TestSingleton.class,    ""              },
            { TestSingleton.class,      TestSingleton.class,    ""              },
            { TestSingleton.class,      TestSingleton.class,    "alternative"   }
            // @formatter:on
        };
    }

    // Helper testing methods for lookups

    @SuppressWarnings("javadoc")
    @Singleton.AccessPoint
    public static TestingSingleton getInstance1() {
        return TestingSingleton.getInstance();
    }

    @SuppressWarnings("javadoc")
    @Singleton.AccessPoint("alternative")
    public static TestingSingleton getInstance2() {
        return TestingSingleton.getInstance();
    }

    @SuppressWarnings("javadoc")
    @Singleton.AccessPoint("null")
    public static TestingSingleton getInstance3() {
        return null; // To distinguish
    }
}
