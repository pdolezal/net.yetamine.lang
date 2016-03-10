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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.stream.IntStream;

/**
 * A read-only representation of a byte sequence.
 *
 * <p>
 * This interface is designed as an encapsulation of an array representation. As
 * such, it behaves in a similar way: all index values must fit in valid ranges,
 * otherwise {@link IndexOutOfBoundsException} shall be thrown.
 */
public interface ByteSequence {

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
     * Implements {@link #equals(Object)}.
     *
     * @param that
     *            the instance to compare. It must not be {@code null}.
     * @param obj
     *            the object to compare
     *
     * @return {@code true} iff the given object argument is equal to the given
     *         instance
     */
    static boolean equals(ByteSequence that, Object obj) {
        if (that == obj) {
            return true;
        }

        if (obj instanceof ByteSequence) {
            final ByteSequence o = (ByteSequence) obj;

            final int length = that.length();
            if (length != o.length()) {
                return false;
            }

            for (int i = 0; i < length; i++) {
                if (that.valueAt(i) != o.valueAt(i)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Implements {@link #hashCode()}.
     *
     * @param that
     *            the instance for which the hash code shall be computed. It
     *            must not be {@code null}.
     *
     * @return the hash code
     */
    static int hashCode(ByteSequence that) {
        int result = 0;

        final int length = that.length();
        for (int i = 0; i < length; i++) {
            result = 31 * result + that.valueAt(i);
        }

        return result;
    }

    // Core sequence methods

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

    // Alternative representations/sources

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
     * Provides a new stream that supplies the sequence.
     *
     * @return a new stream providing the content
     */
    InputStream inputStream();
}
