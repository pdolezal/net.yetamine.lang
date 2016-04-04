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

import net.yetamine.lang.Throwables;

/**
 * An implementation of {@link ByteSequence} that provides a read-only view on a
 * byte array, but leaving the original reference to the array to the caller,
 * hence the content could be modified externally.
 */
public final class ByteBufferView implements ByteSequence {

    /** Source byte buffer. */
    private ByteBuffer buffer;

    /**
     * Creates a new instance.
     *
     * @param source
     *            the source buffer. It must not be {@code null} and it may not
     *            be empty.
     */
    private ByteBufferView(ByteBuffer source) {
        assert (source.remaining() > 0);
        buffer = source;
    }

    /**
     * Creates a new instance.
     *
     * @param data
     *            the buffer to view. It must not be {@code null}.
     *
     * @return the new instance
     */
    public static ByteSequence of(ByteBuffer data) {
        final ByteBuffer buffer = data.slice();
        return buffer.hasRemaining() ? new ByteBufferView(buffer) : ByteSequence.empty();
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
        return ByteSequences.hashCode(this);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#length()
     */
    public int length() {
        return buffer.remaining();
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#valueAt(int)
     */
    public byte valueAt(int index) {
        return buffer.get(index);
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#copy(int, int)
     */
    public ByteContainer copy(int from, int to) {
        return ByteContainer.of(buffer(from, to));
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#view(int, int)
     */
    public ByteSequence view(int from, int to) {
        return (from == to) ? ByteSequence.empty() : of(buffer(from, to));
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#array()
     */
    public byte[] array() {
        final int length = length();
        final byte[] result = new byte[length];
        buffer.duplicate().get(result);
        return result;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#array(int, int)
     */
    public byte[] array(int from, int to) {
        final int range = ByteSequences.length(from, to, length());

        if (range == 0) {
            return ByteSequence.empty().array();
        }

        final ByteBuffer slice = buffer(from, to);
        final byte[] result = new byte[range];
        slice.get(result);
        return result;
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#string(java.nio.charset.Charset)
     */
    public String string(Charset encoding) {
        return encoding.decode(buffer()).toString();
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#buffer()
     */
    public ByteBuffer buffer() {
        return buffer.asReadOnlyBuffer();
    }

    /**
     * @see net.yetamine.lang.containers.ByteSequence#stream()
     */
    public IntStream stream() {
        return IntStream.range(0, length()).map(this::valueAt);
    }

    /**
     * Returns a buffer duplicate with position and limit set according to the
     * given arguments.
     *
     * @param from
     *            the start index, inclusive
     * @param to
     *            the end index, exclusive
     *
     * @return the buffer duplicaet
     *
     * @throws IndexOutOfBoundsException
     *             if an index is out of bounds
     */
    private ByteBuffer buffer(int from, int to) {
        final ByteBuffer result = buffer.duplicate();

        try { // Get the view
            result.position(from).limit(to);
        } catch (IllegalArgumentException e) {
            throw Throwables.init(new IndexOutOfBoundsException(), e);
        }

        return result;
    }
}
