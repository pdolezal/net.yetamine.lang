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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

/**
 * Represents a token for a (generic) type.
 *
 * <p>
 * The idea of this class is based on <i>super type token</i> technique which
 * allows to capture generic type parameters when the actual type parameters,
 * which would be lost otherwise, must be preserved. The capture requires to
 * make usually an anonymous class based on this class with the desired type
 * parameters that shall be captured, for instance:
 *
 * <pre>
 * TypeToken&lt;List&lt;String&gt;&gt; list = new TypeToken&lt;List&lt;String&gt;&gt;() {};
 * </pre>
 *
 * <p>
 * Beware that the parameterized type information must be captured via a
 * subclass, not just by making an instance.
 *
 * @param <T>
 *            the generic type parameter
 */
public class TypeToken<T> {

    /** Type represented by the generic type instance. */
    private final Type type;
    /** The raw parameter type. */
    private final Class<?> raw;

    /**
     * Creates a new instance that derives the generic type and class from the
     * type parameter.
     *
     * <p>
     * This constructor supports the anonymous subclass case shown above.
     *
     * @throws IllegalArgumentException
     *             if the generic type parameter value is not provided by any of
     *             the subclasses
     */
    protected TypeToken() {
        type = typeArgument(getClass(), TypeToken.class);
        raw = typeClass(type);
    }

    /**
     * Creates a new instance, supplying the generic type information and
     * deriving the class.
     *
     * @param genericType
     *            the generic type. It must not be an instance of {@link Class}
     *            or {@link ParameterizedType} whose raw type is an instance of
     *            {@code Class}
     */
    public TypeToken(Type genericType) {
        type = Objects.requireNonNull(genericType);
        raw = typeClass(type);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (this == obj) || ((obj instanceof TypeToken) && type.equals(((TypeToken<?>) obj).type));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return type.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return String.format("TypeToken[%s]", type);
    }

    /**
     * Returns the type represented by the generic type instance.
     *
     * @return the type represented by the generic type instance
     */
    public final Type type() {
        return type;
    }

    /**
     * Returns the {@link Class} instance representing the type that declared
     * the type represented by this generic type instance.
     *
     * @return the representation of the type that declared the type represented
     *         by this generic type instance
     */
    public final Class<?> raw() {
        return raw;
    }

    /**
     * Returns the {@link Class} instance representing the type that declared
     * the supplied type.
     *
     * @param type
     *            the type to inspect. It must not be {@code null}.
     *
     * @return the representation of the supplied type
     */
    private static Class<?> typeClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() instanceof Class<?>) {
                return (Class<?>) parameterizedType.getRawType();
            }
        } else if (type instanceof GenericArrayType) {
            final GenericArrayType array = (GenericArrayType) type;
            final Class<?> componentRawType = typeClass(array.getGenericComponentType());
            return arrayClass(componentRawType);
        }

        final String f = "Type parameter '%s' is not a class or a parametrized type whose raw type is a class.";
        throw new IllegalArgumentException(String.format(f, type));
    }

    /**
     * Returns the {@link Class} instance of an array for the given component
     * class.
     *
     * @param component
     *            the class of the array component. It must not be {@code null}.
     *
     * @return the array class
     */
    private static Class<?> arrayClass(Class<?> component) {
        try {
            return Array.newInstance(component, 0).getClass();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns the value of the type parameter.
     *
     * @param clazz
     *            a subclass the base class to inspect. It must not be
     *            {@code null}.
     * @param base
     *            the base class having the type parameter the value. It must
     *            not be {@code null}.
     *
     * @return the parameterized type
     */
    private static Type typeArgument(Class<?> clazz, Class<?> base) {
        final Deque<Type> superClasses = new ArrayDeque<>();
        Class<?> currentClass = clazz;
        Type currentType;

        do { // Collect superclasses
            currentType = currentClass.getGenericSuperclass();
            superClasses.push(currentType);
            if (currentType instanceof Class) {
                currentClass = (Class<?>) currentType;
            } else if (currentType instanceof ParameterizedType) {
                currentClass = (Class<?>) ((ParameterizedType) currentType).getRawType();
            }
        } while (!currentClass.equals(base));

        // Find and return which superclass supplies the type argument
        for (TypeVariable<?> tv = base.getTypeParameters()[0]; !superClasses.isEmpty();) {
            currentType = superClasses.pop();

            if (currentType instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType) currentType;
                final Class<?> rt = (Class<?>) pt.getRawType();

                final int argIndex = Arrays.asList(rt.getTypeParameters()).indexOf(tv);

                if (0 <= argIndex) {
                    final Type typeArg = pt.getActualTypeArguments()[argIndex];

                    if (typeArg instanceof TypeVariable<?>) {
                        // The type argument is another type variable, look for
                        // the value of that variable in subclasses
                        tv = (TypeVariable<?>) typeArg;
                        continue;
                    }

                    return typeArg; // Found it
                }
            }

            break; // Type argument not supplied: break to throw the exception
        }

        final String f = "Type '%s' does not specify the generic type parameter.";
        throw new IllegalArgumentException(String.format(f, currentType));
    }
}
