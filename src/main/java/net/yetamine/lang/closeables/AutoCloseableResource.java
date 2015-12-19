package net.yetamine.lang.closeables;

/**
 * A generic interface for adapting resource-like objects that do not implement
 * {@link AutoCloseable} (yet) and therefore try-with-resources can't manage
 * them.
 *
 * @param <T>
 *            the type of the adapted resource
 * @param <X>
 *            the type of the exception that the {@link AutoCloseable#close()}
 *            method may throw for this resource
 */
public interface AutoCloseableResource<T, X extends Exception> extends AutoCloseable {

    /**
     * Provides the adapted resource.
     *
     * <p>
     * Implementations should not return {@code null}, or at least until closing
     * the resource; after closing the resource, implementations should throw an
     * exception (like {@link IllegalStateException}) rather than returning
     * {@code null}. A lightweight implementation may keep returning always the
     * same result, regardless the state.
     *
     * @return the adapted resource
     */
    T resource();

    /**
     * @see java.lang.AutoCloseable#close()
     */
    void close() throws X;
}
