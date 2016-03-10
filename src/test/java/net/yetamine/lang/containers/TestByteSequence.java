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
        final ByteSequence empty = ByteContainer.empty();

        Assert.expectThrows(IndexOutOfBoundsException.class, () -> empty.valueAt(0));

        Assert.assertEquals(empty.length(), 0);
        Assert.assertEquals(empty.hashCode(), 0);

        Assert.assertEquals(empty.array().length, 0);
        Assert.assertEquals(empty.buffer().capacity(), 0);
        Assert.assertEquals(empty.stream().count(), 0L);

        try (InputStream is = empty.inputStream()) {
            Assert.assertEquals(is.read(), -1);
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

        final int from = data.length / 2;
        final byte[] half = Arrays.copyOfRange(data, from, data.length);
        test(half, ByteContainer.of(data, from, data.length));
        test(half, ByteArrayView.of(data, from, data.length));

        if (from > 0) {
            Assert.assertFalse(ByteSequence.equals(ByteArrayView.of(data), ByteArrayView.of(half)));
            Assert.assertFalse(ByteContainer.of(data).equals(ByteContainer.of(half)));
            Assert.assertFalse(ByteArrayView.of(data).equals(ByteArrayView.of(half)));
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

        try (InputStream is = sequence.inputStream()) {
            for (int i = 0; i < data.length; i++) {
                Assert.assertEquals(sequence.valueAt(i), data[i]);
                Assert.assertEquals(buffer.get(i), data[i]);
                Assert.assertEquals(is.read(), ints[i]);
                Assert.assertEquals(ints[i], data[i]);
            }

            Assert.assertEquals(is.read(), -1);
        } catch (IOException e) {
            Assert.fail();
        }

        Assert.assertEquals(sequence, ByteContainer.of(data));
        Assert.assertEquals(sequence, ByteArrayView.of(data));

        int hashCode = 0;
        for (byte b : data) {
            hashCode = 31 * hashCode + b;
        }

        Assert.assertEquals(ByteSequence.hashCode(sequence), hashCode);
        Assert.assertEquals(ByteSequence.hashCode(sequence), hashCode);

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
}
