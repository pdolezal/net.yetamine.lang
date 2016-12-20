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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import net.yetamine.lang.closeables.SafeCloseable;

/**
 * A builder for the {@link ByteSequence} which provides traditional I/O
 * interfaces.
 */
public final class ByteSequenceBuilder extends ByteArrayOutputStream implements SafeCloseable, WritableByteChannel {

    /**
     * Creates a new instance.
     */
    public ByteSequenceBuilder() {
        // Default constructor
    }

    /**
     * Creates a new instance with the given buffer preallocation.
     *
     * @param size
     *            the initial size of the construction buffer. It must not be
     *            negative.
     */
    public ByteSequenceBuilder(int size) {
        super(size);
    }

    /**
     * Builds an instance of {@link ByteSequence} providing an immutable
     * representation of the accumulated data.
     *
     * @return the built instance
     */
    /**
     * Returns an instance representing the valid contents of the buffer.
     *
     * @return the current contents of this output stream, as an immutable
     *         {@link ByteSequence} instance
     */
    public ByteSequence toByteSequence() {
        return ByteArrayView.of(toByteArray());
    }

    // Fluent interface

    /**
     * Clears accumulated data like {@link #reset()} does.
     *
     * @return this instance
     */
    public ByteSequenceBuilder clear() {
        reset();
        return this;
    }

    /**
     * Appends the given sequence.
     *
     * @param seq
     *            the sequence to append. It must not be {@code null}.
     *
     * @return this instance
     */
    public ByteSequenceBuilder append(ByteSequence seq) {
        return append(seq.buffer());
    }

    /**
     * Appends the given buffer like {@link #write(ByteBuffer)} does.
     *
     * @param src
     *            the buffer to append. It must not be {@code null}.
     *
     * @return this instance
     */
    public ByteSequenceBuilder append(ByteBuffer src) {
        write(src);
        return this;
    }

    /**
     * Appends the given array like {@link #write(byte[])} does.
     *
     * @param b
     *            the array to append. It must not be {@code null}.
     *
     * @return this instance
     */
    public ByteSequenceBuilder append(byte[] b) {
        write(b);
        return this;
    }

    /**
     * Appends the given array like {@link #write(byte[], int, int)} does.
     *
     * @param b
     *            the array to append. It must not be {@code null}.
     * @param off
     *            the offset to start at
     * @param len
     *            the length of data to append
     *
     * @return this instance
     */
    public ByteSequenceBuilder append(byte[] b, int off, int len) {
        write(b, off, len);
        return this;
    }

    /**
     * Appends the given byte as {@link #write(int)} does.
     *
     * @param b
     *            the byte to append
     *
     * @return this instance
     */
    public ByteSequenceBuilder append(int b) {
        write(b);
        return this;
    }

    /**
     * Appends the given byte as {@link #write(int)} does, just uses a byte
     * parameter already.
     *
     * @param b
     *            the byte to append
     *
     * @return this instance
     */
    public ByteSequenceBuilder append(byte b) {
        return append((int) b);
    }

    // Stream interface

    /**
     * @see java.nio.channels.Channel#isOpen()
     */
    public boolean isOpen() {
        return true;
    }

    /**
     * @see java.io.ByteArrayOutputStream#close()
     */
    @Override
    public void close() {
        // Do nothing as the parent class tells
    }

    /**
     * @see java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)
     */
    public int write(ByteBuffer src) {
        if (src.hasArray()) { // Optimized way
            final int result = src.remaining();
            write(src.array(), src.arrayOffset(), src.remaining());
            return result;
        }

        int result = 0;
        synchronized (this) {
            while (src.hasRemaining()) {
                write(src.get());
                ++result;
            }
        }

        return result;
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] b) {
        write(b, 0, b.length);
    }
}
