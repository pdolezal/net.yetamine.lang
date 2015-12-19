package net.yetamine.lang.closeables;

/**
 * An extension of {@link AutoCloseable} that throws no checked exceptions.
 *
 * <p>
 * Implementations of this interface should avoid throwing any exceptions if
 * possible. Invoking this method on a closed instance should have no effect.
 */
public interface SafeCloseable extends AutoCloseable {

    /**
     * @see java.lang.AutoCloseable#close()
     */
    void close();
}
