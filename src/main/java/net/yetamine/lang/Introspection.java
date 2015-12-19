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
