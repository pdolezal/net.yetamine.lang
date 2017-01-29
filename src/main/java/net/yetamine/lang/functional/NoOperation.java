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

package net.yetamine.lang.functional;

/**
 * An implementation of a "no operation", which is especially useful for making
 * lamdas.
 */
public final class NoOperation {

    /**
     * Prevents creating instances of this class.
     */
    private NoOperation() {
        throw new AssertionError();
    }

    /**
     * Does nothing.
     *
     * <p>
     * This method is the actual implementation of the "no operation", which is
     * useful mainly for making lambdas like nothing-doing {@link Runnable}.
     */
    public static void execute() {
        // Do nothing
    }

    /**
     * Does nothing.
     *
     * <p>
     * This method is the actual implementation of the "no operation", which is
     * useful mainly for making one-argument lambdas.
     *
     * @param <T>
     *            the type of the argument
     * @param argument
     *            the argument to be ignored
     */
    public static <T> void execute(T argument) {
        // Do nothing
    }

    /**
     * Does nothing.
     *
     * <p>
     * This method is the actual implementation of the "no operation", which is
     * useful mainly for making two-argument lambdas.
     *
     * @param <T>
     *            the type of the first argument
     * @param <U>
     *            the type of the second argument
     * @param argument1
     *            the argument to be ignored
     * @param argument2
     *            the argument to be ignored
     */
    public static <T, U> void execute(T argument1, U argument2) {
        // Do nothing
    }

    /**
     * Does nothing.
     *
     * <p>
     * This method is the actual implementation of the "no operation", which is
     * useful mainly for making three-argument lambdas.
     *
     * @param <T>
     *            the type of the first argument
     * @param <U>
     *            the type of the second argument
     * @param <V>
     *            the type of the third argument
     * @param argument1
     *            the argument to be ignored
     * @param argument2
     *            the argument to be ignored
     * @param argument3
     *            the argument to be ignored
     */
    public static <T, U, V> void execute(T argument1, U argument2, V argument3) {
        // Do nothing
    }
}
