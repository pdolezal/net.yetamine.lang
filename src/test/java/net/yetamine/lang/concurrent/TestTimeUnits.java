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
