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
    public static <T> Class<? extends T> classOf(T object) {
        return (Class<? extends T>) object.getClass();
    }

    /**
     * Casts a {@link Class} instance to match its actual generic argument to
     * the desired type, which is often the result of the type inference when
     * compiling the code.
     *
     * <p>
     * The cast is unchecked and can't be checked in runtime, therefore the user
     * takes the responsibility for the correctness of such a cast. However, the
     * cast is useful for generics which are non-reifiable and such a cast can't
     * be avoided.
     *
     * <p>
     * A typical and harmless example of using this method is for casting from
     * raw to a generic reifiable type:
     *
     * <pre>
     * final Class&lt;Set&lt;?&gt;&gt; set = Types.cast(HashSet.class);
     * </pre>
     *
     * @param <T>
     *            the type of resulting values
     * @param clazz
     *            the class to cast to
     *
     * @return the given argument cast to the desired formal type
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> cast(Class<?> clazz) {
        return (Class<T>) clazz;
    }

    private Types() {
        throw new AssertionError();
    }
}
