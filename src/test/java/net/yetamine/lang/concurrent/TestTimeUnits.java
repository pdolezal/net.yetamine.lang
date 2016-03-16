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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link TimeUnits}.
 */
public final class TestTimeUnits {

    /**
     * Tests construction means.
     *
     * @param unit
     *            the unit to use. It must not be {@code null}.
     * @param symbol
     *            the symbol
     */
    @Test(dataProvider = "symbols")
    public void testSymbols(TimeUnit unit, String symbol) {
        Assert.assertEquals(TimeUnits.symbolOf(unit), symbol);
    }

    /**
     * Tests if all available {@link TimeUnit} constants are known.
     */
    @Test
    public void testKnown() {
        final Set<TimeUnit> known = new HashSet<>();
        for (Object[] symbol : symbols()) {
            known.add((TimeUnit) symbol[0]);
        }

        Assert.assertEquals(known, EnumSet.allOf(TimeUnit.class));
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "symbols")
    public static Object[][] symbols() {
        return new Object[][] {
            // @formatter:off
            { TimeUnit.NANOSECONDS,     "ns"    },
            { TimeUnit.MICROSECONDS,    "μs"    }, // Intentionally using direct character instead of escape in the test!
            { TimeUnit.MILLISECONDS,    "ms"    },
            { TimeUnit.SECONDS,         "s"     },
            { TimeUnit.MINUTES,         "min"   },
            { TimeUnit.HOURS,           "h"     },
            { TimeUnit.DAYS,            "d"     }
            // @formatter:on
        };
    }
}
