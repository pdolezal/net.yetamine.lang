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

package net.yetamine.lang.containers.bytes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A read-only representation of a byte sequence.
 *
 * <p>
 * This interface is designed as an encapsulation of an array representation. As
 * such, it behaves in a similar way: all index values must fit in valid ranges,
 * otherwise {@link IndexOutOfBoundsException} shall be thrown.
 */
public interface ByteSequence extends Comparable<ByteSequence> {

    /**
     * Returns an empty instance.
     *
     * @return an empty instance
     */
    static ByteSequence empty() {
        return ByteContainer.empty();
    }

    /**
     * Returns a new builder for the default implementation.
     *
     * @return the new builder
     */
    public static ByteSequenceBuilder builder() {
        return new ByteSequenceBuilder();
    }

    // General methods

    /**
     * Compares the specified object with this instance for equality and returns
     * {@code true} iff the object is a {@link ByteSequence} of the same length
     * and with all elements (returned by {@link #valueAt(int)}) equal for same
     * indices.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    boolean equals(Object obj);

    /**
     * Returns the hash code of the content.
     *
     * <p>
     * The hash code must be computed according to {@link String#hashCode()}
     * algorithm.
     *
     * @see java.lang.Object#hashCode()
     */
    int hashCode();

    /**
     * Provides string representation of the content.
     *
     * <p>
     * Implementations should implement this method to produce a string of
     * hexadecimal digit pairs, each pair for a single byte of the content,
     * using lower-case representation.
     *
     * @return java.lang.Object#toString()
     */
    String toString();

    /**
     * Compares to another sequence.
     *
     * <p>
     * Byte sequences are compared lexicographically (like strings, just using
     * bytes instead of chars). However, instead of comparing bytes as signed,
     * the bytes are compared as unsigned, so that the comparison of the string
     * representation (as described by {@link #toString()}) is then consistent.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * @see java.lang.String#compareTo(java.lang.String)
     */
    default int compareTo(ByteSequence o) {
        return ByteSequences.compare(this, o);
    }

    // Native access methods

    /**
     * Returns the length of this sequence (the number of elements in the
     * sequence).
     *
     * @return the number of elements in the sequence
     */
    int length();

    /**
     * Returns the value at the specified index.
     *
     * <p>
     * An index ranges from zero to {@code length() - 1}. The first value of the
     * sequence is at index zero, the next at index one, and so on, as for array
     * indexing.
     *
     * @param index
     *            the index of the value to be returned
     *
     * @return the specified value
     *
     * @throws IndexOutOfBoundsException
     *             if the given index is negative or not less than
     *             {@link #length()}
     */
    byte valueAt(int index);

    /**
     * Returns a a subsequence of this sequence.
     *
     * <p>
     * The subsequence starts with the value at the index {@code from} and ends
     * with the value at index {@code to - 1}. The length of the subsequence is
     * therefore <tt>to - from</tt> (if <tt>from == to</tt>, then an empty
     * sequence is returned).
     *
     * <p>
     * This method makes a brand new instance with its own copy of the data,
     * which is convenient if the source is large and temporary and the result
     * is small and should persist for longer time. Compare this method to the
     * {@link #view(int, int)} method that provides just a view without making
     * an own copy of the data.
     *
     * @param from
     *            the start index, inclusive
     * @param to
     *            the end index, exclusive
     *
     * @return the specified subsequence
     *
     * @throws IndexOutOfBoundsException
     *             if {@code from} or {@code to} are negative, if {@code to} is
     *             greater than {@code length()}, or if {@code from} is greater
     *             than {@code to}
     */
    ByteSequence copy(int from, int to);

    /**
     * Returns a subsequence of this sequence.
     *
     * <p>
     * This method is similar to {@link #copy(int, int)}, but does not make any
     * copy of the data, which is useful for short-living instances providing a
     * view on a large sequence. Instead, it provides just a view on the data.
     *
     * @param from
     *            the start index, inclusive
     * @param to
     *            the end index, exclusive
     *
     * @return the specified subsequence
     *
     * @throws IndexOutOfBoundsException
     *             if {@code from} or {@code to} are negative, if {@code to} is
     *             greater than {@code length()}, or if {@code from} is greater
     *             than {@code to}
     */
    ByteSequence view(int from, int to);

    // Alternative access methods

    /**
     * Returns the sequence as an independent array.
     *
     * @return an independent array representation
     */
    byte[] array();

    /**
     * Returns a part of the sequence as an independent array.
     *
     * @param from
     *            the start index, inclusive
     * @param to
     *            the end index, exclusive
     *
     * @return the part of the array
     *
     * @throws IndexOutOfBoundsException
     *             if {@code from} or {@code to} are negative, if {@code to} is
     *             greater than {@code length()}, or if {@code from} is greater
     *             than {@code to}
     */
    byte[] array(int from, int to);

    /**
     * Converts the sequence into a string using the given character set.
     *
     * @param encoding
     *            the character set to use for the conversion. It must not be
     *            {@code null}.
     *
     * @return the string representation
     */
    String string(Charset encoding);

    /**
     * Returns a new read-only {@link ByteBuffer} instance providing the
     * sequence content.
     *
     * @return a new read-only buffer view of the content
     */
    ByteBuffer buffer();

    /**
     * Provides a new serial stream that supplies the sequence.
     *
     * @return a new stream providing the content
     */
    IntStream stream();

    /**
     * Provides a new channel/stream that supplies the sequence.
     *
     * <p>
     * The default implementation wraps {@link #buffer()}. The implementation is
     * thread safe as long as the underlying source is, and although it needs no
     * closing actually, it prohibits using after closing.
     *
     * @return a new stream providing the content
     */
    default ByteSequenceReader reader() {
        return new DefaultByteSequenceReader(buffer());
    }
}

/**
 * The default implementation of {@link ByteSequenceReader}.
 */
final class DefaultByteSequenceReader extends ByteSequenceReader {

    /** Buffer with the data. */
    private final ByteBuffer buffer;

    /**
     * @param storage
     *            the source of the data. It must not be {@code null}.
     *
     */
    public DefaultByteSequenceReader(ByteBuffer storage) {
        buffer = Objects.requireNonNull(storage);
        buffer.mark();
    }

    /**
     * @see java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)
     */
    public synchronized int read(ByteBuffer dst) throws IOException {
        final int remaining = buffer.remaining();
        if (remaining == 0) { // End of the stream
            return dst.hasRemaining() ? -1 : 0;
        }

        final int limit = buffer.limit();
        final int length = Math.min(remaining, dst.remaining());

        try {
            buffer.limit(length);
            dst.put(buffer);
        } finally {
            buffer.limit(limit);
        }

        return length;
    }

    /**
     * @see java.io.InputStream#read()
     */
    @Override
    public synchronized int read() throws IOException {
        if (buffer.hasRemaining()) {
            return buffer.get();
        }

        return -1;
    }

    /**
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        final int result = Math.min(buffer.remaining(), len);
        buffer.get(b, off, result);
        return result;
    }

    /**
     * @see net.yetamine.lang.containers.bytes.ByteSequenceReader#available()
     */
    @Override
    public synchronized int available() {
        return buffer.remaining();
    }

    /**
     * @see net.yetamine.lang.containers.bytes.ByteSequenceReader#mark(int)
     */
    @Override
    public synchronized void mark(int readlimit) {
        if (buffer != null) {
            buffer.mark();
        }
    }

    /**
     * @see net.yetamine.lang.containers.bytes.ByteSequenceReader#skip(long)
     */
    @Override
    public synchronized long skip(long n) {
        if (n <= 0) {
            return 0;
        }

        final int result = Math.min((int) Math.min(n, Integer.MAX_VALUE), buffer.remaining());
        buffer.position(buffer.position() + result);
        return result;
    }

    /**
     * @see net.yetamine.lang.containers.bytes.ByteSequenceReader#reset()
     */
    @Override
    public synchronized void reset() {
        buffer.reset();
    }
}
