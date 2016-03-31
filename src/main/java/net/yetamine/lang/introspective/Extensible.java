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

package net.yetamine.lang.introspective;

/**
 * Declares additional abilities and traits extending the basic contract of an
 * interface to help dynamic adaptation in order to achieve better efficiency.
 *
 * <p>
 * This interface is a mixin that allows clients of the base interface(s) to
 * optimize their behavior dynamically according to the contract extensions,
 * which this interface allows to declare.
 *
 * <p>
 * The default implementation declares no extensions, therefore inheriting the
 * interface breaks nothing, allowing the clients to use the base interface as
 * if no extensions were available.
 */
public interface Extensible {

    /**
     * Returns an instance of this interface that describes the given argument.
     *
     * @param o
     *            the object to query
     *
     * @return the {@link Extensible} interface of the given object, or an
     *         instance that returns empty extensions if the argument does not
     *         implement this interface (including the case of a {@code null})
     */
    static Extensible query(Object o) {
        return (o instanceof Extensible) ? (Extensible) o : MissingExtensible.INSTANCE;
    }

    /**
     * Returns the extensions of this instance.
     *
     * <p>
     * An instance should returns always the same result, or add more extensions
     * over time. Revoking an extension after it had been declared might confuse
     * some clients.
     *
     * <p>
     * The default implementation returns empty extension declaration.
     *
     * @return the extensions of this instance
     */
    default Extensions extensions() {
        return Extensions.empty();
    }
}

/**
 * A missing {@link Extensible} support helper.
 */
enum MissingExtensible implements Extensible {

    /** Common surrogate for missing {@link Extensible} support. */
    INSTANCE;
}
