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

package net.yetamine.lang.containers;

import net.yetamine.lang.concurrent.Invalidable;
import net.yetamine.lang.functional.Producer;

/**
 * A value supplier that may defer the computation of the value and use some
 * caching strategy to avoid repeated computations, until the value is
 * explicitly invalidated.
 *
 * @param <T>
 *            the type of the element
 */
public interface InvalidableValue<T> extends Invalidable, Producer<T> {

    /**
     * This method releases the value that the implementation may cache. Until
     * calling this method, {@link #get()} should yield the equal value (or
     * rather the same value if the value can be shared).
     *
     * @see net.yetamine.lang.concurrent.Invalidable#invalidate()
     */
    void invalidate();
}
