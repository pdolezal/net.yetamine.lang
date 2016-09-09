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

package net.yetamine.lang.creational;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;

import net.yetamine.lang.containers.ByteSequence;
import net.yetamine.lang.containers.ByteSequenceBuilder;

/**
 * A prototype based on a serialized object.
 *
 * @param <T>
 *            the type of the serialized object
 */
public final class Serialized<T> implements Factory<T> {

    /** Serialization data. */
    private final ByteSequence data;

    /**
     * Creates a new instance.
     *
     * @param source
     *            the source object to serialize. It must not be {@code null}.
     *
     * @throws UnsupportedOperationException
     *             if the source can't be serialized
     */
    public Serialized(T source) {
        Objects.requireNonNull(source);
        final ByteSequenceBuilder builder = ByteSequence.builder();
        try (ObjectOutputStream os = new ObjectOutputStream(builder)) {
            os.writeObject(source);
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }

        data = builder.toByteSequence();
        assert (build() != null); // Test the deserialization would work
    }

    /**
     * @see net.yetamine.lang.creational.Factory#build()
     */
    public T build() {
        try (ObjectInputStream is = new ObjectInputStream(data.reader())) {
            @SuppressWarnings("unchecked")
            final T result = (T) is.readObject();
            assert (result != null);
            return result;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
