/*
 * Copyright 2017 Yetamine
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

package net.yetamine.lang;

/**
 * Recursive self type representing {@code K&lt;T&gt;}, which allows similar
 * behavior to higher-kinded types.
 *
 * @param <K>
 *            the recursive kind type
 * @param <T>
 *            the component type of the kind type
 */
public interface Kind1<K extends Kind1<K, ?>, T> {
    // Type-defining interface
}
