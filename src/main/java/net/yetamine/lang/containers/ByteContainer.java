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
 * An immutable implementation of {@link ByteSequence}.
 *
 * <p>
 * Because this class represents an immutable sequence, it carries a copy of the
 * original data and provides only copies or read-only interface to access them.
 */
public final class ByteContainer implements ByteSequence {

    /** Empty instance singleton. */
    private static final ByteContainer EMPTY = new ByteContainer(new byte[0]);

    /** Data storage. */
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
     * Returns an empty instance.
     *
     * @return an empty instance
     */
    public static ByteContainer empty() {
        return EMPTY;
    }

    /**
     * Returns an instance equal to the given source, but ensuring that the
     * result is an instance of this class, hence immutable for sure.
     *
     * @param source
     *            the source of the data. It must not be {@code null}.
     *
     * @return an instance equal to the given source
     */
    public static ByteContainer secure(ByteSequence source) {
        return (source instanceof ByteContainer) ? (ByteContainer) source : of(source.buffer());
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
    public static ByteContainer from(int... data) {
        if (data.length == 0) {
            return empty();
        }

        final byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) data[i];
        }

        return new ByteContainer(result);
    }

    /**
     * Returns an instance for the given array.
     *
     * @param data
     *            the array. It must not be {@code null}.
     *
     * @return the instance
     */
    public static ByteContainer of(byte... data) {
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
        final int length = ByteSequences.length(from, to, data.length);
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

        if (result == 0) {
            result = ByteSequences.hashCode(this);
            hashCode = result;
        }

        return result;
    }

    /**
     * Computes SHA-1 digest.
     *
     * @param sequence
     *            the sequence to process. It must not be {@code null}.
     *
     * @return the digest
     */
    public static ByteContainer sha1(ByteSequence sequence) {
        return new ByteContainer(ByteSequences.sha1(sequence));
    }

    /**
     * Computes SHA-1 digest.
     *
     * @return the digest
     */
    public ByteContainer sha1() {
        return sha1(this);
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
    public ByteContainer copy(int from, int to) {
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
        final int length = ByteSequences.length(from, to, array.length);

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
}
