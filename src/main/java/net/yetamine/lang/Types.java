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

package net.yetamine.lang;

/**
 * Utilities for dealing with type information.
 */
public final class Types {

    /**
     * Returns the class of an object.
     *
     * @param <T>
     *            the type of the object
     * @param object
     *            the object. It must not be {@code null}.
     *
     * @return the class of the object
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getClass(T object) {
        return (Class<? extends T>) object.getClass();
    }

    private Types() {
        throw new AssertionError();
    }
}
