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

package net.yetamine.lang.closeables;

/**
 * An extension of {@link AutoCloseable} that throws a specific exception class,
 * which is declared by a type parameter for the interface as well and therefore
 * can be used in generic resource management support.
 *
 * @param <X>
 *            the type of the exception that the resource bound to this
 *            interface may throw
 */
public interface PureCloseable<X extends Exception> extends AutoCloseable {

    /**
     * @see java.lang.AutoCloseable#close()
     */
    void close() throws X;
}
