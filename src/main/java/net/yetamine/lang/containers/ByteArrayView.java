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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.stream.IntStream;

/**
 * An implementation of {@link ByteSequence} that provides a read-only view on a
 * byte array.
 *
 * <p>
 * The view may refer to mutable data, but it is strongly discouraged, because
 * the interface provides no locks to ensure atomicity of the changes. However,
 * if the use of an instance is synchronized externally and {@code view()} is
 * used for making the instance, the instance may work with non-constant data
 * with a mild performance penalty for hash code computation.
 */
public final class ByteArrayView implements ByteSequence {

    /** Data of the object. */
    private final byte[] array;
    /** Origin of the view. */
    private final int origin;
    /** Length of the view. */
    private final int length;
    /** Cached byte buffer. */
    private ByteBuffer buffer;
    /** Cached hash code. */
    private volatile int hashCode;

    /**
     * Creates a new instance.
     *
     * @param data
     *            the array to view. It must not be {@code null}.
     * @param from
     *            the start index, inclusive
     * @param count
     *            the number of array elements to view. It must be positive.
     */
    private ByteArrayView(byte[] data, int from, int count) {
        array = data;
        origin = from;
        length = count;

        assert (length > 0);
    }

    /**
     * Creates a new instance that works with a non-constant data.
     *
     * @param data
     *            the array to view. It must not be {@code null}.
     * @param from
     *            the start index, inclusive
     * @param count
     *            the number of array elements to view. It must be positive.
     * @param overload
     *            overloading discriminator
     */
    private ByteArrayView(byte[] data, int from, int count, Void overload) {
        this(data, from, count);
        hashCode = Integer.MIN_VALUE;
    }

    /**
     * Creates a new instance that may point to non-constant data.
     *
     * @param data
     *            the buffer to view. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static ByteSequence view(byte... data) {
        return (data.length == 0) ? ByteSequence.empty() : new ByteArrayView(data, 0, data.length, null);
    }

    /**
     * Creates a new instance that may point to non-constant data.
     *
     * @param data
     *            the array to view. It must not be {@code null}.
     * @param from
     *            the starting offset of the view (inclusive)
     * @param to
     *            the ending offset of the view (exclusive)
     *
     * @return the new instance
     *
     * @throws IndexOutOfBoundsException
     *             if an offset is out of the array's bounds
     */
    public static ByteSequence view(byte[] data, int from, int to) {
        final int length = ByteSequences.length(from, to, data.length);
        return (length == 0) ? ByteSequence.empty() : new ByteArrayView(data, from, length, null);
    }

    /**
     * Creates a new instance.
     *
     * @param data
     *            the data to view, using just lower 8 bits, ignoring the
     *            others. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static ByteSequence from(int... data) {
        return ByteContainer.from(data);
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * Use this method if the source data won't be modified in the future, e.g.,
     * when passing a dedicated copy of the array.
     *
     * @param data
     *            the array to view. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static ByteSequence of(byte... data) {
        return (data.length == 0) ? ByteSequence.empty() : new ByteArrayView(data, 0, data.length);
    }

    /**
     * Creates a new instance.
     *
     * <p>
     * Use this method if the source data won't be modified in the future, e.g.,
     * when working with a constant private copy of an array.
     *
     * @param data
     *            the array to view. It must not be {@code null}.
     * @param from
     *            the starting offset of the view (inclusive)
     * @param to
     *            the ending offset of the view (exclusive)
     *
     * @return the new instance
     *
     * @throws IndexOutOfBoundsException
     *             if an offset is out of the array's bounds
     */
    public static ByteSequence of(byte[] data, int from, int to) {
        final int length = ByteSequences.length(from, to, data.length);
        return (length == 0) ? ByteSequence.empty() : new ByteArrayView(data, from, length);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ByteSequences.toString(this);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ByteSequences.equals(this, obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = hashCode;

        if (result == Integer.MIN_VALUE) {
            return ByteSequences.hashCode(this);
        }

        if (result == 0) {
            result = ByteSequences.hashCode(this);
            hashCode = result;
        }

        return result;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#length()
     */
    public int length() {
        return length;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#valueAt(int)
     */
    public byte valueAt(int index) {
        return array[origin + ByteSequences.index(index, (0 <= index) && (index < length))];
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#copy(int, int)
     */
    public ByteContainer copy(int from, int to) {
        return ByteContainer.of(array, origin + from, origin + to);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#view(int, int)
     */
    public ByteSequence view(int from, int to) {
        final int range = ByteSequences.length(from, to, length);
        return (range == 0) ? ByteSequence.empty() : new ByteArrayView(array, origin + from, range);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#array()
     */
    public byte[] array() {
        final byte[] result = new byte[length];
        System.arraycopy(array, origin, result, 0, length);
        return result;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#array(int, int)
     */
    public byte[] array(int from, int to) {
        final int range = ByteSequences.length(from, to, length);

        if (range == 0) {
            return ByteSequence.empty().array();
        }

        final byte[] result = new byte[range];
        System.arraycopy(array, origin, result, 0, range);
        return result;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#string(java.nio.charset.Charset)
     */
    public String string(Charset encoding) {
        return new String(array, origin, length, encoding);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#buffer()
     */
    public ByteBuffer buffer() {
        ByteBuffer result = buffer;

        if (result == null) {
            result = ByteBuffer.wrap(array, origin, length).slice();
            buffer = result;
        }

        return result.asReadOnlyBuffer();
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#stream()
     */
    public IntStream stream() {
        return IntStream.range(origin, origin + length).map(i -> array[i]);
    }
}
