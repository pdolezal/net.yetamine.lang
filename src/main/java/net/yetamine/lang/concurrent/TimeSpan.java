package net.yetamine.lang.concurrent;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Represents a positive period of time, e.g., low-level timeouts for blocking
 * methods.
 */
public final class TimeSpan implements Serializable, Comparable<TimeSpan> {

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /** Zero-representing instances for all units. */
    private static final Map<TimeUnit, TimeSpan> ZERO = new EnumMap<>(TimeUnit.class);

    static {
        for (TimeUnit unit : TimeUnit.values()) {
            ZERO.put(unit, new TimeSpan(0, unit));
        }
    }

    /** Length of the period in nanoseconds. */
    private final long nanoseconds;
    /** Unit of the period to show. */
    private final TimeUnit unit;

    /**
     * Creates a new instance.
     *
     * @param value
     *            the value of the time span. It must not be negative.
     * @param valueUnit
     *            the unit of the value. It must not be {@code null}.
     * @param showUnit
     *            the unit to display the value later. It must not be
     *            {@code null}.
     */
    public TimeSpan(long value, TimeUnit valueUnit, TimeUnit showUnit) {
        nanoseconds = valueUnit.toNanos(value);

        if (value < 0) {
            // Intentionally keep the value here as direct TimeUnit; this is precondition
            // check and the caller is responsible for passing correct input. If fails to
            // do so, the message should be helpful, but also as direct as possible.
            throw new IllegalArgumentException(String.format("%d %s", value, valueUnit));
        }

        unit = Objects.requireNonNull(showUnit);
    }

    /**
     * Creates a new instance.
     *
     * @param value
     *            the value of the time span. It must not be negative.
     * @param valueUnit
     *            the unit of the value. It must not be {@code null}.
     */
    public TimeSpan(long value, TimeUnit valueUnit) {
        this(value, valueUnit, valueUnit);
    }

    /**
     * Returns an instance representing the given time span.
     *
     * @param value
     *            the value of the time span. If negative, zero time span is
     *            returned instead.
     * @param valueUnit
     *            the unit of the value. It must not be {@code null}.
     * @param showUnit
     *            the unit to display the value later. It must not be
     *            {@code null}.
     *
     * @return an instance representing the given time span
     */
    public static TimeSpan of(long value, TimeUnit valueUnit, TimeUnit showUnit) {
        if (value <= 0) { // Force the contract
            Objects.requireNonNull(valueUnit);
            return ZERO.get(Objects.requireNonNull(showUnit));
        }

        return new TimeSpan(value, valueUnit, showUnit);
    }

    /**
     * Returns an instance representing the given time span.
     *
     * @param value
     *            the value of the time span. If negative, zero time span is
     *            returned instead.
     * @param valueUnit
     *            the unit of the value. It must not be {@code null}.
     *
     * @return an instance representing the given time span
     */
    public static TimeSpan of(long value, TimeUnit valueUnit) {
        return (value > 0) ? new TimeSpan(value, valueUnit) : ZERO.get(Objects.requireNonNull(valueUnit));
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuilder().append(value()).append(' ').append(TimeUnits.symbolOf(unit)).toString();
    }

    /**
     * An instance is considered equal to another instance if the other instance
     * represents the same amount of nanoseconds and uses the same unit which is
     * <em>not</em> consistent with {@link #compareTo(TimeSpan)}.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof TimeSpan) {
            final TimeSpan o = (TimeSpan) obj;
            return (nanoseconds == o.nanoseconds) && (unit == o.unit);
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(nanoseconds, unit);
    }

    /**
     * This class defines natural ordering using {@link #nanoseconds()} only,
     * the {@link #unit()} is not relevant, which is <em>not</em> consistent to
     * {@link #equals(Object)} (but it is similar to {@link BigInteger} and its
     * {@code equals} inconsistency due to precision).
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(TimeSpan o) {
        return Long.compare(nanoseconds, o.nanoseconds);
    }

    /**
     * Returns the exact number of nanoseconds regardless of the used unit. This
     * method captures any range of the values.
     *
     * @return the number of nanoseconds
     */
    public long nanoseconds() {
        return nanoseconds;
    }

    /**
     * Returns the value converted to a different unit.
     *
     * @param u
     *            the unit to which the conversion should be performed. It must
     *            not be {@code null}.
     *
     * @return the converted value
     */
    public long convert(TimeUnit u) {
        return u.convert(nanoseconds, TimeUnit.NANOSECONDS);
    }

    /**
     * Returns an instance without fractional part of the precise
     * representation.
     *
     * @param referenceUnit
     *            the unit for truncating. It must not be {@code null}.
     *
     * @return an instance without fractional part of the precise representation
     */
    public TimeSpan truncate(TimeUnit referenceUnit) {
        final long result = referenceUnit.toNanos(convert(referenceUnit));
        return (result == nanoseconds) ? this : of(result, TimeUnit.NANOSECONDS, referenceUnit);
    }

    /**
     * Returns an instance without fractional part of the precise representation
     * using {@link #unit()} for truncating.
     *
     * @return an instance without fractional part of the precise representation
     */
    public TimeSpan truncate() {
        return truncate(unit);
    }

    /**
     * Returns a shifted time span (extended or contracted, depending on the
     * sign of the value); underflow under zero or overflow over the maximum
     * time span is saturated and causes no exception.
     *
     * @param value
     *            the shift
     * @param valueUnit
     *            the unit of the value. It must not be {@code null}.
     *
     * @return the shifted time span
     */
    public TimeSpan shift(long value, TimeUnit valueUnit) {
        try {
            return of(Math.addExact(nanoseconds, valueUnit.toNanos(value)), TimeUnit.NANOSECONDS, unit);
        } catch (ArithmeticException e) {
            return (value > 0) ? new TimeSpan(Long.MAX_VALUE, TimeUnit.NANOSECONDS, unit) : ZERO.get(unit);
        }
    }

    /**
     * Adds a time span; overflow over the maximum time span is saturated and
     * causes no exception.
     *
     * @param value
     *            the value to add. It must not be {@code null}.
     *
     * @return the extended time span
     */
    public TimeSpan plus(TimeSpan value) {
        return shift(value.nanoseconds(), TimeUnit.NANOSECONDS);
    }

    /**
     * Subtracts a time span; underflow under zero is saturated and causes no
     * exception.
     *
     * @param value
     *            the value to add. It must not be {@code null}.
     *
     * @return the extended time span
     */
    public TimeSpan minus(TimeSpan value) {
        return shift(-value.nanoseconds(), TimeUnit.NANOSECONDS);
    }

    /**
     * Waits for an object.
     *
     * @param obj
     *            the object to be waited for. It must not be {@code null}.
     *
     * @throws InterruptedException
     *             if the waiting is interrupted
     *
     * @see TimeUnit#timedWait(Object, long)
     */
    public void timedWait(Object obj) throws InterruptedException {
        TimeUnit.NANOSECONDS.timedWait(obj, nanoseconds);
    }

    /**
     * Waits for a thread to join.
     *
     * @param thread
     *            the thread to be waited for. It must not be {@code null}.
     *
     * @throws InterruptedException
     *             if the waiting is interrupted
     *
     * @see TimeUnit#timedJoin(Thread, long)
     */
    public void timedJoin(Thread thread) throws InterruptedException {
        TimeUnit.NANOSECONDS.timedJoin(thread, nanoseconds);
    }

    /**
     * Lets the current thread sleep for the given time span.
     *
     * @throws InterruptedException
     *             if the waiting is interrupted
     *
     * @see TimeUnit#sleep(long)
     */
    public void sleep() throws InterruptedException {
        TimeUnit.NANOSECONDS.sleep(nanoseconds);
    }

    /**
     * Returns an instance representing the same time span, but with different
     * {@link #unit()}.
     *
     * @param u
     *            the unit of the result. It must not be {@code null}.
     *
     * @return an instance representing the time span using a different unit
     */
    public TimeSpan unit(TimeUnit u) {
        return (u == unit) ? this : of(nanoseconds, TimeUnit.NANOSECONDS, u);
    }

    /**
     * Returns the unit of {@link #value()}.
     *
     * @return the unit of {@link #value()}
     */
    public TimeUnit unit() {
        return unit;
    }

    /**
     * Returns the length of this time span using {@link #unit()}.
     *
     * @return the length of this time span using {@link #unit()}
     */
    public long value() {
        return unit.convert(nanoseconds, TimeUnit.NANOSECONDS);
    }
}
