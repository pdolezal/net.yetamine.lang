package net.yetamine.lang;

import java.util.Optional;

/**
 * A representation of trivalent logic values, using Kleene's definition for the
 * operations.
 */
public enum Trivalent {

    /**
     * The unknown value.
     */
    UNKNOWN {

        /**
         * @see net.yetamine.lang.Trivalent#and(boolean)
         */
        @Override
        public Trivalent and(boolean right) {
            return (right ? UNKNOWN : FALSE);
        }

        /**
         * @see net.yetamine.lang.Trivalent#or(boolean)
         */
        @Override
        public Trivalent or(boolean right) {
            return (right ? TRUE : UNKNOWN);
        }

        /**
         * @see net.yetamine.lang.Trivalent#and(net.yetamine.lang.Trivalent)
         */
        @Override
        public Trivalent and(Trivalent right) {
            return (right == FALSE) ? FALSE : UNKNOWN;
        }

        /**
         * @see net.yetamine.lang.Trivalent#or(net.yetamine.lang.Trivalent)
         */
        @Override
        public Trivalent or(Trivalent right) {
            return (right == TRUE) ? TRUE : UNKNOWN;
        }

        /**
         * @see net.yetamine.lang.Trivalent#negation()
         */
        @Override
        public Trivalent negation() {
            return UNKNOWN;
        }

        /**
         * @see net.yetamine.lang.Trivalent#toBoolean()
         */
        @Override
        public Optional<Boolean> toBoolean() {
            return Optional.empty();
        }

        /**
         * @see net.yetamine.lang.Trivalent#asBoolean()
         */
        @Override
        public boolean asBoolean() {
            throw new ClassCastException("Trivalent.UNKNOWN::asBoolean");
        }
    },

    /**
     * The false value.
     */
    FALSE {

        /**
         * @see net.yetamine.lang.Trivalent#and(boolean)
         */
        @Override
        public Trivalent and(boolean right) {
            return FALSE;
        }

        /**
         * @see net.yetamine.lang.Trivalent#or(boolean)
         */
        @Override
        public Trivalent or(boolean right) {
            return (right ? TRUE : FALSE);
        }

        /**
         * @see net.yetamine.lang.Trivalent#and(net.yetamine.lang.Trivalent)
         */
        @Override
        public Trivalent and(Trivalent right) {
            return FALSE;
        }

        /**
         * @see net.yetamine.lang.Trivalent#or(net.yetamine.lang.Trivalent)
         */
        @Override
        public Trivalent or(Trivalent right) {
            return right;
        }

        /**
         * @see net.yetamine.lang.Trivalent#negation()
         */
        @Override
        public Trivalent negation() {
            return TRUE;
        }

        /**
         * @see net.yetamine.lang.Trivalent#toBoolean()
         */
        @Override
        public Optional<Boolean> toBoolean() {
            return OPTIONAL_FALSE;
        }

        /**
         * @see net.yetamine.lang.Trivalent#asBoolean()
         */
        @Override
        public boolean asBoolean() {
            return false;
        }
    },

    /**
     * The true value.
     */
    TRUE {

        /**
         * @see net.yetamine.lang.Trivalent#and(boolean)
         */
        @Override
        public Trivalent and(boolean right) {
            return (right ? TRUE : FALSE);
        }

        /**
         * @see net.yetamine.lang.Trivalent#or(boolean)
         */
        @Override
        public Trivalent or(boolean right) {
            return TRUE;
        }

        /**
         *
         * @see net.yetamine.lang.Trivalent#and(net.yetamine.lang.Trivalent)
         */
        @Override
        public Trivalent and(Trivalent right) {
            return right;
        }

        /**
         * @see net.yetamine.lang.Trivalent#or(net.yetamine.lang.Trivalent)
         */
        @Override
        public Trivalent or(Trivalent right) {
            return TRUE;
        }

        /**
         * @see net.yetamine.lang.Trivalent#negation()
         */
        @Override
        public Trivalent negation() {
            return FALSE;
        }

        /**
         * @see net.yetamine.lang.Trivalent#toBoolean()
         */
        @Override
        public Optional<Boolean> toBoolean() {
            return OPTIONAL_TRUE;
        }

        /**
         * @see net.yetamine.lang.Trivalent#asBoolean()
         */
        @Override
        public boolean asBoolean() {
            return true;
        }
    };

    /** Cache for {@link Optional} of {@link Boolean#FALSE}. */
    static final Optional<Boolean> OPTIONAL_FALSE = Optional.of(Boolean.FALSE);
    /** Cache for {@link Optional} of {@link Boolean#TRUE}. */
    static final Optional<Boolean> OPTIONAL_TRUE = Optional.of(Boolean.TRUE);

    /**
     * Returns the result of this <em>and</em> the operand.
     *
     * @param right
     *            the other operand. It must not be {@code null}.
     *
     * @return the result of the <em>and</em> operation
     */
    public abstract Trivalent and(Trivalent right);

    /**
     * Returns the result of this <em>or</em> the operand.
     *
     * @param right
     *            the other operand. It must not be {@code null}.
     *
     * @return the result of the <em>or</em> operation
     */
    public abstract Trivalent or(Trivalent right);

    /**
     * Returns the result of this and <em>and</em> the operand.
     *
     * @param right
     *            the other operand. It must not be {@code null}.
     *
     * @return the result of <em>and</em> operation
     */
    public Trivalent and(Boolean right) {
        return and(fromBoolean(right));
    }

    /**
     * Returns the result of this <em>or</em> the operand.
     *
     * @param right
     *            the other operand. It must not be {@code null}.
     *
     * @return the result of the <em>or</em> operation
     */
    public Trivalent or(Boolean right) {
        return or(fromBoolean(right));
    }

    /**
     * Return the result of this <em>and</em> the operand.
     *
     * @param right
     *            the other operand. It must not be {@code null}.
     *
     * @return the result of the <em>and</em> operation
     */
    public abstract Trivalent and(boolean right);

    /**
     * Returns the result of this <em>or</em> the operand.
     *
     * @param right
     *            the other operand. It must not be {@code null}.
     *
     * @return the result of the <em>or</em> operation
     */
    public abstract Trivalent or(boolean right);

    /**
     * Returns the negation of this instance.
     *
     * @return the negation of this instance
     */
    public abstract Trivalent negation();

    /**
     * Converts to a boolean.
     *
     * @return the boolean instance
     */
    public abstract Optional<Boolean> toBoolean();

    /**
     * Returns {@code true} iff this instance is {@link #TRUE}.
     *
     * @return {@code true} iff this instance is {@link #TRUE}
     */
    public final boolean isTrue() {
        return (this == TRUE);
    }

    /**
     * Returns {@code true} iff this instance is {@link #FALSE}.
     *
     * @return {@code true} iff this instance is {@link #FALSE}
     */
    public final boolean isFalse() {
        return (this == FALSE);
    }

    /**
     * Returns {@code true} iff this instance is {@link #UNKNOWN}.
     *
     * @return {@code true} iff this instance is {@link #UNKNOWN}
     */
    public final boolean isUnknown() {
        return (this == UNKNOWN);
    }

    /**
     * Returns {@code true} iff this instance is not {@link #UNKNOWN}.
     *
     * @return {@code true} iff this instance is not {@link #UNKNOWN}
     */
    public final boolean isBoolean() {
        return (this != UNKNOWN);
    }

    /**
     * Returns a boolean result if {@link #isBoolean()}, otherwise throws an
     * exception.
     *
     * @return a boolean result
     *
     * @throws ClassCastException
     *             if this instance is not a boolean-compatible
     */
    public abstract boolean asBoolean();

    /**
     * Converts from a boolean value.
     *
     * @param value
     *            the source value
     *
     * @return the appropriate trivalent value
     */
    public static Trivalent fromBoolean(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * Converts from a {@link Boolean} instance.
     *
     * @param value
     *            the source value. It may be {@code null} in which case the
     *            result is {@link #UNKNOWN}.
     *
     * @return the appropriate trivalent value
     */
    public static Trivalent fromBoolean(Boolean value) {
        return (value == null) ? UNKNOWN : fromBoolean(value.booleanValue());
    }
}
