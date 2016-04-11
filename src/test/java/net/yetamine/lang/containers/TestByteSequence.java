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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link ByteContainer} and {@link ByteArrayView}.
 */
public final class TestByteSequence {

    /**
     * Tests {@link ByteContainer#empty()} and its behavior.
     */
    @Test
    public void testEmpty() {
        final ByteSequence empty = ByteSequence.empty();

        Assert.expectThrows(IndexOutOfBoundsException.class, () -> empty.valueAt(0));

        Assert.assertEquals(empty.length(), 0);
        Assert.assertEquals(empty.hashCode(), 0);

        Assert.assertEquals(empty.array().length, 0);
        Assert.assertEquals(empty.buffer().capacity(), 0);
        Assert.assertEquals(empty.stream().count(), 0L);

        try (InputStream is = empty.reader()) {
            Assert.assertEquals(is.read(), -1);
        } catch (IOException e) {
            Assert.fail();
        }

        try (ReadableByteChannel channel = empty.reader()) {
            final ByteBuffer buffer = ByteBuffer.allocate(1);
            Assert.assertEquals(channel.read(buffer), -1);
            Assert.assertEquals(buffer.remaining(), 1);
        } catch (IOException e) {
            Assert.fail();
        }

        Assert.assertEquals(empty.copy(0, 0).length(), 0);
        Assert.assertEquals(empty.view(0, 0).length(), 0);
        Assert.assertEquals(empty.copy(0, 0), empty);
        Assert.assertEquals(empty.view(0, 0), empty);
    }

    /**
     * Tests the containers with the given data.
     *
     * @param data
     *            the data to test. It must not be {@code null}.
     */
    @Test(dataProvider = "arrays")
    public void test(byte[] data) {
        test(data, ByteContainer.of(data));
        test(data, ByteContainer.of(ByteBuffer.wrap(data)));
        test(data, ByteArrayView.of(data));
        test(data, ByteBufferView.of(ByteBuffer.wrap(data)));

        final int from = data.length / 2;
        final byte[] half = Arrays.copyOfRange(data, from, data.length);
        test(half, ByteContainer.of(data, from, data.length));
        test(half, ByteArrayView.of(data, from, data.length));

        if (from > 0) {
            Assert.assertFalse(ByteSequences.equals(ByteArrayView.of(data), ByteArrayView.of(half)));
            Assert.assertFalse(ByteContainer.of(data).equals(ByteContainer.of(half)));
            Assert.assertFalse(ByteArrayView.of(data).equals(ByteArrayView.of(half)));
            Assert.assertFalse(
                    ByteBufferView.of(ByteBuffer.wrap(data)).equals(ByteBufferView.of(ByteBuffer.wrap(half))));
        }
    }

    /**
     * Tests the containers with the given data.
     *
     * @param data
     *            the data to test. It must not be {@code null}.
     * @param sequence
     *            the equal sequence. It must not be {@code null}.
     */
    private void test(byte[] data, ByteSequence sequence) {
        Assert.assertEquals(data.length, sequence.length());

        final ByteBuffer buffer = sequence.buffer();
        Assert.assertEquals(buffer.remaining(), data.length);

        final int[] ints = sequence.stream().toArray();
        Assert.assertEquals(ints.length, data.length);

        try (ByteSequenceReader source = sequence.reader()) {
            for (int i = 0; i < data.length; i++) {
                Assert.assertEquals(sequence.valueAt(i), data[i]);
                Assert.assertEquals(buffer.get(i), data[i]);
                Assert.assertEquals(source.read(), ints[i]);
                Assert.assertEquals(ints[i], data[i]);
            }

            Assert.assertEquals(source.read(), -1);

            source.reset();
            Assert.assertEquals(source.available(), data.length);
            final ByteBuffer read = ByteBuffer.wrap(new byte[data.length]);
            Assert.assertEquals(source.read(read), data.length);
            Assert.assertEquals(read.array(), data);
        } catch (IOException e) {
            Assert.fail();
        }

        Assert.assertEquals(sequence, ByteContainer.of(data));
        Assert.assertEquals(sequence, ByteArrayView.of(data));

        int hashCode = 0;
        for (byte b : data) {
            hashCode = 31 * hashCode + b;
        }

        Assert.assertEquals(ByteSequences.hashCode(sequence), hashCode);
        Assert.assertEquals(ByteSequences.hashCode(sequence), hashCode);

        Assert.assertEquals(sequence.array(), data);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "arrays")
    public static Object[][] arrays() {
        return new Object[][] {
            // @formatter:off
            { new byte[0]                                       },
            { new byte[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 }      },
            { new byte[] { 3, 2, 1 }                            },
            { new byte[] { 3, 1 }                               },
            { new byte[] { 2 }                                  }
            // @formatter:on
        };
    }

    /**
     * Tests {@link ByteSequence#toString()}.
     */
    @Test
    public void testToString() {
        Assert.assertEquals(ByteSequence.empty().toString(), "");
        Assert.assertEquals(ByteArrayView.from(Byte.MAX_VALUE, Byte.MIN_VALUE, -1).toString(), "7f80ff");
        Assert.assertEquals(ByteArrayView.from(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0).toString(), "0a09080706050403020100");
    }

    /**
     * Tests {@link ByteSequences#compare(ByteSequence, ByteSequence)}.
     *
     * @param array1
     *            the first array
     * @param array2
     *            the second array
     * @param result
     *            the expected result
     */
    @Test(dataProvider = "compare")
    public void testCompare(byte[] array1, byte[] array2, int result) {
        final ByteSequence seq1 = ByteArrayView.of(array1);
        final ByteSequence seq2 = ByteArrayView.of(array2);


        final int compare = ByteSequences.compare(seq1, seq2);
        Assert.assertEquals(compare, result);
        Assert.assertEquals(ByteSequences.compare(seq2, seq1), -compare);

        Assert.assertEquals(sgn(seq1.toString().compareTo(seq2.toString())), sgn(result));
    }

    private static int sgn(int value) {
        return (value < 0) ? -1 : (value > 0) ? 1 : 0;
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "compare")
    public static Object[][] compare() {
        return new Object[][] {
            // @formatter:off
            {
                new byte[0],
                new byte[0],
                0
            },

            {
                new byte[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1  },
                new byte[] { 10, 9, 8, 7, 6, 5, 4, 3, 2     },
                1
            },

            {
                new byte[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1  },
                new byte[] { 10, 9, 8                       },
                7
            },

            {
                new byte[] { 3, 1 },
                new byte[] { 0, 1 },
                3
            },

            {
                new byte[] { -1, 1 },
                new byte[] {  0, 1 },
                0xFF
            },

            {
                new byte[] { Byte.MIN_VALUE },
                new byte[] { Byte.MAX_VALUE },
                1
            }
            // @formatter:on
        };
    }

    /**
     * Tests views on non-constant data.
     */
    @Test
    public void testNonConstant() {
        final byte[] array = new byte[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1  };

        final ByteSequence arrayCopy = ByteContainer.of(array);
        final ByteSequence arrayView = ByteArrayView.view(array);
        final ByteSequence bufferView = ByteBufferView.view(ByteBuffer.wrap(array));

        Assert.assertEquals(arrayView, arrayCopy);
        Assert.assertEquals(arrayView.hashCode(), arrayCopy.hashCode());

        Assert.assertEquals(bufferView, arrayCopy);
        Assert.assertEquals(bufferView.hashCode(), arrayCopy.hashCode());

        Assert.assertEquals(bufferView, arrayView);
        Assert.assertEquals(bufferView.hashCode(), arrayView.hashCode());

        ++array[0]; // Modify the array
        Assert.assertNotEquals(arrayView, arrayCopy);
        Assert.assertNotEquals(arrayView.hashCode(), arrayCopy.hashCode());

        Assert.assertNotEquals(bufferView, arrayCopy);
        Assert.assertNotEquals(bufferView.hashCode(), arrayCopy.hashCode());

        Assert.assertEquals(bufferView, arrayView);
        Assert.assertEquals(bufferView.hashCode(), arrayView.hashCode());
    }
}
