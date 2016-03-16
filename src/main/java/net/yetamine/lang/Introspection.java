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

package net.yetamine.lang;

import java.util.Collections;
import java.util.Map;

/**
 * Indicates support for retrieving additional information of an instance to aid
 * debugging, logging and other kind of introspective processing.
 *
 * <p>
 * The values provided as the introspection data should provide human-friendly
 * implementation of the {@link Object#toString()} method to achieve the goal.
 */
public interface Introspection {

    /**
     * Provides the set of elements describing features or capabilities of this
     * instance.
     *
     * <p>
     * The default implementation returns an empty set. Implementations are
     * supposed to override this method and provide a set of actual values.
     *
     * @return the set of introspective descriptions; never {@code null}, but
     *         possibly unmodifiable
     */
    default Map<?, ?> introspect() {
        return Collections.emptyMap();
    }
}
