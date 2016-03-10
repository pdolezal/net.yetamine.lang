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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

/**
 * An immutable implementation of {@link ByteSequence}.
 *
 * <p>
 * Because this class represents an immutable sequence, it carries a copy of the
 * original data and provides only copies or read-only interface to access them.
 */
public final class ByteContainer implements ByteSequence {

    /** Empty instance singleton. */
    private static final ByteContainer EMPTY = new ByteContainer(new byte[0]);

    /** Data of the object. */
    private final byte[] array;
    /** Cached byte buffer. */
    private ByteBuffer buffer;
    /** Cached hash code. */
    private int hashCode;

    /**
     * Create a new instance that just wraps the given array.
     *
     * <p>
     * The caller is responsible for passing the ownership of the given array to
     * the new instance and it must not change the array after that (preferably,
     * the only reference to the array should exist within the new instance).
     *
     * @param data
     *            the array to accept. It must not be {@code null}.
     */
    private ByteContainer(byte[] data) {
        assert (data != null);
        array = data;
    }

    /**
     * @return an empty instance
     */
    public static ByteContainer empty() {
        return EMPTY;
    }

    /**
     * Returns an instance for the given array.
     *
     * @param data
     *            the array. It must not be {@code null}.
     *
     * @return the instance
     */
    public static ByteContainer of(byte[] data) {
        return (data.length == 0) ? empty() : new ByteContainer(data.clone());
    }

    /**
     * Returns an instance for the given array part.
     *
     * @param data
     *            the array. It must not be {@code null}.
     * @param from
     *            the start index, inclusive
     * @param to
     *            the end index, exclusive
     *
     * @return the instance
     *
     * @throws IndexOutOfBoundsException
     *             if {@code from} or {@code to} are negative, if {@code to} is
     *             greater than the length of the array, or if {@code from} is
     *             greater than {@code to}
     */
    public static ByteContainer of(byte[] data, int from, int to) {
        final int length = length(from, to, data.length);
        if (length == 0) {
            return empty();
        }

        final byte[] array = new byte[length];
        System.arraycopy(data, from, array, 0, length);
        return new ByteContainer(array);
    }

    /**
     * Get an new instance from a buffer. The remaining part of the buffer is
     * read and its position is advanced accordingly.
     *
     * @param data
     *            the buffer. It must not be {@code null}.
     *
     * @return the instance
     */
    public static ByteContainer of(ByteBuffer data) {
        final int length = data.remaining();
        if (length == 0) {
            return empty();
        }

        final byte[] payload = new byte[length];
        data.get(payload);
        return new ByteContainer(payload);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ByteSequence.equals(this, obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = hashCode;

        if (result == 0) {
            result = ByteSequence.hashCode(this);
            hashCode = result;
        }

        return result;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#length()
     */
    public int length() {
        return array.length;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#valueAt(int)
     */
    public byte valueAt(int index) {
        return array[index];
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#copy(int, int)
     */
    public ByteSequence copy(int from, int to) {
        return ((from == 0) && (to == length())) ? this : of(array, from, to);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#view(int, int)
     */
    public ByteSequence view(int from, int to) {
        return ByteArrayView.of(array, from, to);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#array()
     */
    public byte[] array() {
        return (array.length == 0) ? array : array.clone();
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#array(int, int)
     */
    public byte[] array(int from, int to) {
        final int length = length(from, to, array.length);

        if (length == 0) {
            return EMPTY.array;
        }

        final byte[] result = new byte[length];
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#string(java.nio.charset.Charset)
     */
    public String string(Charset encoding) {
        return new String(array, encoding);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#buffer()
     */
    public ByteBuffer buffer() {
        ByteBuffer result = buffer;

        if (result == null) {
            result = ByteBuffer.wrap(array);
            buffer = result;
        }

        return result.asReadOnlyBuffer();
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#stream()
     */
    public IntStream stream() {
        return IntStream.range(0, array.length).map(i -> array[i]);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#inputStream()
     */
    public InputStream inputStream() {
        return new ByteArrayInputStream(array);
    }

    /**
     * Implements {@link #toString()}.
     *
     * <p>
     * Although the interface does not require any particular {@code toString}
     * implementation, it is recommended to provide some for debugging purposes
     * at least; this method provides one: a string of hexadecimal digits (with
     * no whitespace) where each digit pair represents a single byte.
     *
     * @param that
     *            the instance for which the hash code shall be computed. It
     *            must not be {@code null}.
     *
     * @return the string representation
     */
    static String toString(ByteSequence that) {
        // Allocate the exact result size (one space less)
        final StringBuilder result = new StringBuilder(that.length() * 2);
        that.stream().mapToObj(value -> String.format("%2x", value)).forEach(result::append);
        return result.toString();
    }

    /**
     * Computes the length of the subsequence between specified indices and
     * checks if they are inside the specified range.
     *
     * @param from
     *            the start index, inclusive. It may be zero if the end index is
     *            zero too, regardless of the length of the underlying array.
     * @param to
     *            the end index, exclusive
     * @param length
     *            the length of the underlying array. It must not be negative.
     *
     * @return the length of the subsequence
     *
     * @throws IndexOutOfBoundsException
     *             if any of the index is not within the range, or when
     *             {@code from} is greated than {@code to}
     */
    static int length(int from, int to, int length) {
        if ((from == 0) && (to == 0)) {
            return 0;
        }

        check(from, from < length);
        check(to, to <= length);

        final int result = to - from;
        if (result < 0) { // No argument here
            throw new IndexOutOfBoundsException();
        }

        return result;
    }

    /**
     * Checks a bounds condition.
     *
     * @param index
     *            the index to check
     * @param condition
     *            {@code true} if the index is valid
     */
    static void check(int index, boolean condition) {
        if (condition) {
            return;
        }

        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
    }

    /**
     * A builder for the {@link ByteContainer} instances.
     */
    public static final class Builder {

        /** Default fragment size for stream consumption. */
        private static final int DEFAULT_FRAGMENT_SIZE = 1024;

        /** List of fragments of the result. */
        private final List<byte[]> fragments = new ArrayList<>();
        /** Length of the content. */
        private int length;

        /**
         * Creates a new instance.
         */
        public Builder() {
            // Default constructor
        }

        /**
         * Appends a copy of the given array to the result.
         *
         * @param value
         *            the value to append. It must not be {@code null}.
         *
         * @return this instance
         */
        public Builder append(byte[] value) {
            if (value.length > 0) {
                fragments.add(value.clone());
                length += value.length;
            }

            return this;
        }

        /**
         * Appends a part of the given array to the result.
         *
         * @param value
         *            the value to append. It must not be {@code null}.
         * @param from
         *            the start index, inclusive
         * @param to
         *            the end index, exclusive
         *
         * @return this instance
         *
         * @throws IndexOutOfBoundsException
         *             if {@code from} or {@code to} are negative, if {@code to}
         *             is greater than the length of the array, or if
         *             {@code from} is greater than {@code to}
         */
        public Builder append(byte[] value, int from, int to) {
            final int range = length(from, to, value.length);

            if (range > 0) {
                final byte[] array = new byte[range];
                fragments.add(array);
                length += range;
            }

            return this;
        }

        /**
         * Appends the content of the given buffer to the result; the buffer's
         * position moves to the limit by this operation.
         *
         * @param value
         *            the value to append. It must not be {@code null}.
         *
         * @return this instance
         */
        public Builder append(ByteBuffer value) {
            final int count = value.remaining();
            if (count == 0) {
                return this;
            }

            final byte[] array = new byte[count];
            value.get(array);
            fragments.add(array);
            length += count;

            return this;
        }

        /**
         * Appends the content of the given stream to the result; the stream is
         * exhausted by this operation.
         *
         * @param value
         *            the value to append. It must not be {@code null}.
         *
         * @return this instance
         */
        public Builder append(IntStream value) {
            final ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_FRAGMENT_SIZE);

            for (PrimitiveIterator.OfInt it = value.iterator(); it.hasNext();) {
                if (buffer.hasRemaining()) { // Until the buffer is full
                    buffer.put((byte) it.nextInt());
                    continue;
                }

                buffer.flip();
                append(buffer);
                buffer.clear();
            }

            // Handle the last half-full buffer
            buffer.flip();
            append(buffer);
            return this;
        }

        /**
         * Builds the instance.
         *
         * @return the built instance
         */
        public ByteContainer build() {
            if (length == 0) {
                return ByteContainer.empty();
            }

            // Compact the fragments
            if (fragments.size() > 1) {
                final byte[] content = new byte[length];

                int position = 0;
                for (byte[] fragment : fragments) {
                    System.arraycopy(fragment, 0, content, position, fragment.length);
                    position += fragment.length;
                }

                fragments.clear();
                fragments.add(content);
            }

            return ByteContainer.of(fragments.get(0)); // Could be cached too
        }
    }
}
