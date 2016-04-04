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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilities for implementing byte containers.
 */
public final class ByteSequences {

    /**
     * Implements {@link ByteSequence#equals(Object)}.
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
     * Implements {@link ByteSequence#hashCode()}.
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

    /**
     * Implements {@link ByteSequence#toString()}.
     *
     * @param that
     *            the instance for which the hash code shall be computed. It
     *            must not be {@code null}.
     *
     * @return the string representation
     */
    public static String toString(ByteSequence that) {
        final StringBuilder result = new StringBuilder(that.length() * 2);
        that.stream().mapToObj(value -> String.format("%02x", (byte) value)).forEach(result::append);
        return result.toString();
    }

    /**
     * Compares two sequences.
     *
     * <p>
     * Byte sequences are compared lexicographically (like strings, just using
     * bytes instead of chars). However, instead of comparing bytes as signed,
     * the bytes are compared as unsigned, so that the comparison of the string
     * representation (as described by {@link #toString()}) is then consistent.
     *
     * <p>
     * This method implements {@link ByteSequence#compareTo(ByteSequence)}
     * actually.
     *
     * @param that
     *            the first instance to compare. It must not be {@code null}.
     * @param some
     *            the other instance to compare. It must not be {@code null}.
     *
     * @return the result of comparison
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * @see java.lang.String#compareTo(java.lang.String)
     */
    public static int compare(ByteSequence that, ByteSequence some) {
        if (that == some) {
            return 0;
        }

        final int length1 = that.length();
        final int length2 = some.length();
        final int lim = Math.min(length1, length2);

        for (int i = 0; i < lim; i++) {
            final int byte1 = that.valueAt(i) & 0xFF;
            final int byte2 = some.valueAt(i) & 0xFF;
            if (byte1 != byte2) {
                return byte1 - byte2;
            }
        }

        return length1 - length2;
    }

    /**
     * Computes SHA-1 digest.
     *
     * @param sequence
     *            the sequence to compute the digest. It must not be
     *            {@code null}.
     *
     * @return the digest
     */
    public static byte[] sha1(ByteSequence sequence) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(sequence.buffer());
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Checks a bounds condition.
     *
     * @param index
     *            the index to check
     * @param condition
     *            {@code true} if the index is valid
     *
     * @return the index
     */
    static int index(int index, boolean condition) {
        if (condition) {
            return index;
        }

        throw new IndexOutOfBoundsException("Index out of bounds: " + index);
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

        final int result = index(to, to <= length) - index(from, from < length);

        if (result < 0) {
            final String f = "Invalid index range: [%d; %d)";
            throw new IndexOutOfBoundsException(String.format(f, from, to));
        }

        return result;
    }

    private ByteSequences() {
        throw new AssertionError();
    }
}
