package net.yetamine.lang.concurrent;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Represents a positive period of time, e.g., low-level timeouts for blocking
 * methods.
 */
public final class TimeUnits {

    /** (Almost) international unit names. */
    private static final Map<TimeUnit, String> NAMES = new EnumMap<>(TimeUnit.class);

    static {
        NAMES.put(TimeUnit.NANOSECONDS, "ns");
        NAMES.put(TimeUnit.MICROSECONDS, "\u03BCs");
        NAMES.put(TimeUnit.MILLISECONDS, "ms");
        NAMES.put(TimeUnit.SECONDS, "s");
        NAMES.put(TimeUnit.MINUTES, "min");
        NAMES.put(TimeUnit.HOURS, "h");
        NAMES.put(TimeUnit.DAYS, "d");

        for (TimeUnit unit : TimeUnit.values()) {
            NAMES.computeIfAbsent(unit, u -> unit.toString().toLowerCase(Locale.ENGLISH));
        }
    }

    /**
     * Returns the SI symbol of the unit.
     *
     * <p>
     * Actually, hours and days have no SI unit symbol, but <i>h</i> and
     * <i>d</i> are usuallly acceptable and established replacement that may be
     * used as the surrogate.
     *
     * <p>
     * If the JRE library extends {@link TimeUnit} in an unexpected way, this
     * method may return the name of the enum constant in lower case as the
     * fallback.
     *
     * @param unit
     *            the unit. It must not be {@code null}.
     *
     * @return the SI symbol of the unit
     */
    public static String symbolOf(TimeUnit unit) {
        return NAMES.get(Objects.requireNonNull(unit));
    }

    private TimeUnits() {
        throw new AssertionError();
    }
}
