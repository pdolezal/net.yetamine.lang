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
import java.util.Collection;

/**
 * Tests {@link FluentCollection} and {@link FluentCollectionAdapter}.
 *
 * <p>
 * This test class may be inherited for more implementations, when overriding
 * the {@link #newInstance()} method.
 */
public class TestFluentCollection {

    // TODO: test all methods

    /**
     * Makes a new blank instance of the class to test.
     *
     * <p>
     * The default implementation returns an adaptation of the {@link ArrayList}
     * using {@link FluentCollection#adapt(Collection)}.
     *
     * @param <E>
     *            the type of values
     *
     * @return the new instance
     */
    protected <E> FluentCollection<E> newInstance() {
        return FluentCollection.adapt(new ArrayList<>());
    }
}
