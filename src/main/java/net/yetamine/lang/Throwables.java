package net.yetamine.lang;

/**
 * A utility class for dealing with exceptions.
 */
public final class Throwables {

    /**
     * Calls {@link Throwable#initCause(Throwable)} on the given exception and
     * returns the exception then.
     *
     * <p>
     * This method is suitable for compact initialization of the exceptions that
     * have no cause-chaining constructor:
     *
     * <pre>
     * try {
     *     // Some code that might raise an IOException
     * } catch (IOException e) {
     *     throw Throwables.init(new NoSuchElementException(), e);
     * }
     * </pre>
     *
     * To detect wrong callers that supply {@code null} exception to initialize,
     * causing an unexpected {@link NullPointerException}, the method checks the
     * argument with an {@code assert}.
     *
     * @param <X>
     *            the type of the exception to process
     * @param t
     *            the exception to process. It must not be {@code null}.
     * @param cause
     *            the cause to be set
     *
     * @return the given exception
     */
    public static <X extends Throwable> X init(X t, Throwable cause) {
        assert (t != null);
        t.initCause(cause);
        return t;
    }

    private Throwables() {
        throw new AssertionError();
    }
}
