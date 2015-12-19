package net.yetamine.lang.closeables;

/**
 * A generic interface for adapting resource-like objects that do not implement
 * {@link SafeCloseable} (yet) and therefore try-with-resources can't manage
 * them, or require yet another catch block to deal with exceptions possibly
 * thrown from the {@link AutoCloseable#close()} method.
 *
 * @param <T>
 *            the type of the adapted resource
 */
public interface SafeCloseableResource<T> extends SafeCloseable, AutoCloseableResource<T, RuntimeException> {

    /**
     * Implementations should rather avoid throwing any exceptions, so that an
     * exception shall be rather a programming error than an actual failure.
     *
     * @see java.lang.AutoCloseable#close()
     */
    void close();
}
