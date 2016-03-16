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

package net.yetamine.lang.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * Represents a time point, e.g., a deadline relative to the system time.
 *
 * <p>
 * The implementation works with nanosecond granularity, employing the whole
 * positive range of the <em>long</em> data type to mitigate the risk of the
 * integer overflow, however, it is limited with the range.
 */
public final class TimePoint implements Comparable<TimePoint> {

    /**
     * Origin of the system clock. All values are offsets relative to this
     * value, which limits the risk of integer overflow.
     */
    private static final long ORIGIN = System.nanoTime();

    /**
     * The constant denoting the most past moment available within the
     * implementation resolution.
     */
    private static final TimePoint PAST = new TimePoint(0);

    /**
     * The constant denoting the most future moment available within the
     * implementation resolution.
     */
    private static final TimePoint FUTURE = new TimePoint(Long.MAX_VALUE);

    /** Offset from {@link #ORIGIN} in nanoseconds. */
    private final long point;

    /**
     * Creates a new instance.
     *
     * @param originOffset
     *            the offset from {@link #ORIGIN}. It should be non-negative.
     */
    private TimePoint(long originOffset) {
        assert (originOffset >= 0);
        point = originOffset;
    }

    /**
     * Returns an instance determining a point within the specified offset from
     * the current clock value.
     *
     * @param offset
     *            the offset from the current clock value
     * @param unit
     *            the unit in which the offset is given. It must not be
     *            {@code null}.
     *
     * @return the instance representing the timestamp
     */
    public static TimePoint within(long offset, TimeUnit unit) {
        final long nanoOffset = unit.toNanos(offset);
        final long now = now();

        // Avoid wrapping the past to the future
        if ((nanoOffset < 0) && (nanoOffset < -now)) {
            return past();
        }

        try {
            return new TimePoint(Math.addExact(now, nanoOffset));
        } catch (ArithmeticException e) {
            return future();
        }
    }

    /**
     * Returns an instance determining a point within the specified offset from
     * the current clock value.
     *
     * @param span
     *            the offset from the current clock value. It must not be
     *            {@code null}.
     *
     * @return the instance representing the given future timestamp
     */
    public static TimePoint within(TimeSpan span) {
        final long nanoOffset;
        try { // Saturate the overflow with the future()
            nanoOffset = Math.addExact(now(), span.convert(TimeUnit.NANOSECONDS));
        } catch (ArithmeticException e) {
            return future();
        }

        return new TimePoint(nanoOffset);
    }

    /**
     * Returns an instance with zero offset from the present.
     *
     * @return the instance pointing to the presence
     */
    public static TimePoint present() {
        return new TimePoint(now());
    }

    /**
     * Returns an instance at the most past moment in the implementation
     * resolution.
     *
     * @return the most past instance
     */
    public static TimePoint past() {
        return PAST;
    }

    /**
     * Returns an instance at the most future moment in the implementation
     * resolution.
     *
     * @return the most future instance
     */
    public static TimePoint future() {
        return FUTURE;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("TimePoint[%s]", Long.toUnsignedString(ORIGIN + point));
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return ((obj instanceof TimePoint) && (((TimePoint) obj).point == point));
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(point);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(TimePoint o) {
        return Long.compareUnsigned(point, o.point);
    }

    /**
     * Returns the remaining time as a {@link TimeSpan} instance.
     *
     * @param unit
     *            the unit of the time. It must not be {@code null}.
     *
     * @return the {@link TimeSpan} instance representing the remaining time, or
     *         representing zero if the remaining time would be negative
     */
    public TimeSpan remaining(TimeUnit unit) {
        return TimeSpan.of(remainingTime(), TimeUnit.NANOSECONDS, unit);
    }

    /**
     * Returns the remaining time to another time point as a {@link TimeSpan}
     * instance.
     *
     * @param p
     *            the other time point. It must not be {@code null}.
     * @param unit
     *            the unit of the time. It must not be {@code null}.
     *
     * @return a {@link TimeSpan} instance representing the remaining time, or
     *         representing zero if the remaining time would be negative
     */
    public TimeSpan remainingTo(TimePoint p, TimeUnit unit) {
        return TimeSpan.of(p.point - point, TimeUnit.NANOSECONDS, unit);
    }

    /**
     * Returns the remaining time in nanoseconds.
     *
     * @param unit
     *            the unit of the time. It must not be {@code null}.
     *
     * @return the remaining time, or zero if {@link #passed()}
     */
    public long remainingNanos(TimeUnit unit) {
        return Math.max(remainingTime(), 0);
    }

    /**
     * Makes the current thread wait for the time point using the given object.
     *
     * @param obj
     *            the object to be waited for. It must not be {@code null}.
     *
     * @throws InterruptedException
     *             if the waiting has been interrupted
     */
    public void waitFor(Object obj) throws InterruptedException {
        TimeUnit.NANOSECONDS.timedWait(obj, remainingTime());
    }

    /**
     * Indicates whether the time point lies in the future.
     *
     * @return {@code true} if the time point has not passed
     */
    public boolean isFuture() {
        return (remainingTime() > 0);
    }

    /**
     * Indicates whether the time point lies in the past.
     *
     * @return {@code true} if the time point passed already
     */
    public boolean isPast() {
        return (remainingTime() < 0);
    }

    /**
     * Indicates whether the time point has passed.
     *
     * @return {@code true} if the time point passed or is just about to pass
     *         (i.e. the time point is the present moment)
     */
    public boolean passed() {
        return (remainingTime() <= 0);
    }

    /**
     * Makes a time point shifted by the specified offset.
     *
     * @param value
     *            the shift offset
     * @param unit
     *            the unit of the value. It must not be {@code null}.
     *
     * @return the time point shifted by the specified offset from this time
     *         point
     */
    public TimePoint plus(long value, TimeUnit unit) {
        final long nanoOffset = unit.toNanos(value);
        // Avoid wrapping the past in the future again
        if ((nanoOffset < 0) && (nanoOffset < -point)) {
            return past();
        }

        try {
            return new TimePoint(Math.addExact(point, nanoOffset));
        } catch (ArithmeticException e) {
            return future();
        }
    }

    /**
     * Makes a time point shifted by the specified offset.
     *
     * @param value
     *            the shift offset. It must not be {@code null}.
     *
     * @return the time point shifted by the specified offset from this time
     *         point
     */
    public TimePoint plus(TimeSpan value) {
        try {
            return new TimePoint(Math.addExact(point, value.nanoseconds()));
        } catch (ArithmeticException e) {
            return future();
        }
    }

    /**
     * Returns the time remaining to the time point.
     *
     * @return the remaining time; negative if the time point passed, positive
     *         if the time point lies in the future
     */
    private long remainingTime() {
        return point - now();
    }

    /**
     * Returns the offset of "now".
     *
     * @return the number of nanoseconds of "now" from {@link #ORIGIN}
     */
    private static long now() {
        return System.nanoTime() - ORIGIN;
    }
}
