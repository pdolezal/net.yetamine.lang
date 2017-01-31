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

package net.yetamine.lang.snippets;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.testng.annotations.Test;

import net.yetamine.lang.formatting.Quoting;
import net.yetamine.lang.introspective.Extensible;
import net.yetamine.lang.introspective.Extensions;

/**
 * This class demonstrates using {@link Extensions}.
 *
 * <p>
 * This demo shows a client that relies on caching heavily, but prevents
 * stacking of caching decorators, because they could have some overhead too.
 */
public final class UsingExtensions {

    /**
     * Tests and measures the runs.
     */
    @Test
    public void demo() {
        { // Use the implementation with no caching
            System.out.println("Running queries with no caching");
            final long origin = System.nanoTime();
            process(ORACLE);
            final long diff = System.nanoTime() - origin;
            System.out.format("Elapsed: %d ms%n", TimeUnit.NANOSECONDS.toMillis(diff));
        }

        { // Let's compare the previous one to a caching function instead
            System.out.println("Running queries with single level of caching");
            final long origin = System.nanoTime();
            process(new CachingFunction<>(ORACLE));
            final long diff = System.nanoTime() - origin;
            System.out.format("Elapsed: %d ms%n", TimeUnit.NANOSECONDS.toMillis(diff));
        }

        { // Well, maybe somebody incorporates the caching inside its method...
            System.out.println("Running queries with double level of caching");
            final long origin = System.nanoTime();
            cachingQuery(new CachingFunction<>(ORACLE)); // But what happends when the decorators stack?
            final long diff = System.nanoTime() - origin;
            System.out.format("Elapsed: %d ms%n", TimeUnit.NANOSECONDS.toMillis(diff));
        }

        { // Let's use a smarter way of incorporating the caching mechanism inside the method
            System.out.println("Running queries with smart caching - applying caching");
            final long origin = System.nanoTime();
            smartQuery(ORACLE);
            final long diff = System.nanoTime() - origin;
            System.out.format("Elapsed: %d ms%n", TimeUnit.NANOSECONDS.toMillis(diff));
        }

        { // And let's compare it to the case when we get an already-caching function
            System.out.println("Running queries with smart caching - avoiding double caching");
            final long origin = System.nanoTime();
            smartQuery(new CachingFunction<>(ORACLE));
            final long diff = System.nanoTime() - origin;
            System.out.format("Elapsed: %d ms%n", TimeUnit.NANOSECONDS.toMillis(diff));
        }
    }

    /**
     * Runs the process with a caching function.
     *
     * @param oracle
     *            the oracle to ask. It must not be {@code null}.
     */
    private static void cachingQuery(Function<String, String> oracle) {
        process(new CachingFunction<>(oracle));
    }

    /**
     * Runs the process with a caching function if necessary.
     *
     * @param oracle
     *            the oracle to ask. It must not be {@code null}.
     */
    private static void smartQuery(Function<String, String> oracle) {
        // Decide dynamically and without instanceof and marker interfaces
        process(Extensions.of(oracle).contains(FEATURE_CACHING) ? oracle : new CachingFunction<>(oracle));
    }

    /**
     * Performs some questions to the oracle.
     *
     * @param oracle
     *            the oracle to ask. It must not be {@code null}.
     */
    private static void process(Function<String, String> oracle) {
        for (String word : Arrays.asList("hello", "hello", "hello", "world!")) {
            System.out.println(oracle.apply(word));
        }
    }

    /**
     * Responds with some result after a while of thinking (this can emulate a
     * slow network service or demanding computation).
     */
    private static final Function<String, String> ORACLE = s -> {
        try { // Simulate some random, but possibly large amount of time to respond
            Thread.sleep(TimeUnit.SECONDS.toMillis(s.length() / 2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new StringBuilder().append("echo ").append(Quoting.single(s)).toString();
    };

    /** Extension marking that the object supports caching. */
    static final Object FEATURE_CACHING = new Object();

    /**
     * A caching function.
     *
     * @param <T>
     *            the type of the argument
     * @param <R>
     *            the type of the result
     */
    private static final class CachingFunction<T, R> implements Extensible, Function<T, R> {

        /** Extensions supported by this class. */
        private static final Extensions EXTENSIONS = Extensions.list(FEATURE_CACHING);

        /** Oracle to ask. */
        private final Function<? super T, ? extends R> implementation;
        /** Cache of the results. */
        private final Map<T, R> cache = new ConcurrentHashMap<>();

        /**
         * Creates a new instance.
         *
         * @param f
         *            the function to decorate. It must not be {@code null}.
         */
        public CachingFunction(Function<? super T, ? extends R> f) {
            implementation = Objects.requireNonNull(f);
        }

        /**
         * @see java.util.function.Function#apply(java.lang.Object)
         */
        public R apply(T t) {
            return cache.computeIfAbsent(t, a -> {
                try { // Simulate some fixed overhead of the caching implementation
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return implementation.apply(a);
            });
        }

        /**
         * @see net.yetamine.lang.introspective.Extensible#extensions()
         */
        public Extensions extensions() {
            return EXTENSIONS;
        }
    }
}
