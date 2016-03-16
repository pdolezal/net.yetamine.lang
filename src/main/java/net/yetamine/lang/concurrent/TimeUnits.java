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
