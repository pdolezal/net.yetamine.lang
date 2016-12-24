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

package net.yetamine.lang.concurrent;

import java.util.concurrent.Callable;

/**
 * An extension of {@link Callable} that throws a specific exception class,
 * which is declared by a type parameter for the interface as well.
 *
 * @param <V>
 *            the type of the result
 * @param <X>
 *            the type of the exception that the resource bound to this
 *            interface may throw
 */
@FunctionalInterface
public interface PureCallable<V, X extends Exception> extends Callable<V> {

    /**
     * @see java.util.concurrent.Callable#call()
     */
    V call() throws X;
}
