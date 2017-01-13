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

import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Tools for dealing with iterators.
 */
public final class Iterators {

    /**
     * Makes a {@link Stream} from the given iterator.
     *
     * <p>
     * The stream has an unknown size, it is sequential and not ordered.
     *
     * @param <T>
     *            the type of the elements
     * @param iterator
     *            the iterator to use. It must not be {@code null}.
     *
     * @return the stream
     */
    public static <T> Stream<T> stream(Iterator<? extends T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    private Iterators() {
        throw new AssertionError();
    }
}
