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

package net.yetamine.lang.containers.tuples;

import java.util.List;

/**
 * The base interface for all tuples.
 */
public interface Tuple {

    /**
     * Returns the arity of this instance.
     *
     * @return the arity of this instance
     */
    int arity();

    /**
     * Returns the value with the given index.
     *
     * @param index
     *            the index of the value to get
     *
     * @return the value with the given index
     */
    Object get(int index);

    /**
     * Returns a list containing values from this tuple.
     *
     * @return a list containing values from this tuple
     */
    List<?> toList();
}
