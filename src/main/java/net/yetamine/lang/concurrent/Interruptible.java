package net.yetamine.lang.concurrent;

/**
 * Provides the capability of interrupting an asynchronous operation.
 */
public interface Interruptible {

    /**
     * Interrupts the operation linked to this instance.
     *
     * <p>
     * If the operation has been interrupted already, this method does nothing.
     * This method should throw no exceptions either.
     */
    void interrupt();
}
