package net.yetamine.lang.formatting;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A proxy for an object that has no suitable {@link Object#toString()} method
 * override. For {@link net.yetamine.lang.Introspection}, this class could be
 * useful as well for representing the keys of the values that are designated
 * for human-friendly representation of a property.
 */
public final class ToString implements Supplier<String> {

    /** Identifier to display. */
    private final String value;

    /**
     * Creates a new instance.
     *
     * @param representation
     *            the identifier to present. It must not be {@code null}.
     */
    public ToString(String representation) {
        value = Objects.requireNonNull(representation);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * @see java.util.function.Supplier#get()
     */
    public String get() {
        return toString();
    }
}
