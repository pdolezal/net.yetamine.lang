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
import java.nio.channels.ReadableByteChannel;

import net.yetamine.lang.closeables.SafeCloseable;

/**
 * A reader of the {@link ByteSequence} which provides traditional I/O
 * interfaces.
 *
 * <p>
 * Implementations of this interface should be lenient in the similar way like
 * {@link ByteSequenceBuilder} is, i.e., closing them should have no effect on
 * using other methods and throwing an {@link IOException} should never happen.
 */
public abstract class ByteSequenceReader extends InputStream implements SafeCloseable, ReadableByteChannel {

    /**
     * Prepares a new instance.
     */
    protected ByteSequenceReader() {
        // Default constructor
    }

    /**
     * The default implementation does nothing.
     *
     * @see java.nio.channels.Channel#close()
     */
    @Override
    public void close() {
        // Do nothing
    }

    /**
     * The default implementation returns {@code true} always because
     * {@link #close()} should have no effect by default.
     *
     * @see java.nio.channels.Channel#isOpen()
     */
    public boolean isOpen() {
        return true;
    }

    /**
     * Inheriting classes must override this method and they should support this
     * feature.
     *
     * @see java.io.InputStream#available()
     */
    @Override
    public abstract int available();

    /**
     * The default implementation returns {@code true} and inheriting classes
     * should support this feature.
     *
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * @see java.io.InputStream#mark(int)
     */
    @Override
    public abstract void mark(int readlimit);

    /**
     * Inheriting classes must override this method and they should support this
     * feature.
     *
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public abstract long skip(long n);

    /**
     * Inheriting classes must override this method and they should support this
     * feature.
     *
     * @see java.io.InputStream#reset()
     */
    @Override
    public abstract void reset();
}
