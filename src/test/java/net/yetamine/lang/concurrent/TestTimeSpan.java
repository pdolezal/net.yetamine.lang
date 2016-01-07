package net.yetamine.lang.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link TimeSpan}.
 */
public final class TestTimeSpan {

    /**
     * Tests construction means.
     */
    @Test
    public void testConstruction() {
        final long value = 12345L;

        for (TimeUnit unit : TimeUnit.values()) {
            final TimeSpan t = new TimeSpan(value, unit);
            Assert.assertEquals(t.value(), value);
            Assert.assertEquals(t.unit(), unit);
        }

        for (TimeUnit unit : TimeUnit.values()) {
            final TimeSpan t = TimeSpan.of(value, unit);
            Assert.assertEquals(t.value(), value);
            Assert.assertEquals(t.unit(), unit);
        }

        for (TimeUnit unit : TimeUnit.values()) {
            final TimeSpan t = TimeSpan.of(0, unit);
            Assert.assertEquals(t.value(), 0);
        }

        for (TimeUnit unit : TimeUnit.values()) {
            final TimeSpan t = TimeSpan.of(-123L, unit);
            Assert.assertEquals(t.value(), 0);
        }

        for (TimeUnit valueUnit : TimeUnit.values()) {
            for (TimeUnit showUnit : TimeUnit.values()) {
                final TimeSpan t1 = new TimeSpan(value, valueUnit, showUnit);
                Assert.assertEquals(t1.nanoseconds(), valueUnit.toNanos(value));
                Assert.assertEquals(t1.unit(), showUnit);

                final TimeSpan t2 = TimeSpan.of(value, valueUnit, showUnit);
                Assert.assertEquals(t2.unit(), showUnit);
                Assert.assertEquals(t2, t1);

                final TimeSpan t3 = TimeSpan.of(-123L, valueUnit, showUnit);
                Assert.assertEquals(t3.unit(), showUnit);
                Assert.assertEquals(t3.value(), 0);
            }
        }
    }

    /**
     * Tests {@link Object#equals(Object)} and {@link Object#hashCode()}.
     *
     * @param t
     *            the testing instance. It must not be {@code null}.
     */
    @Test(dataProvider = "instances")
    public void testEquals(TimeSpan t) {
        final TimeSpan n = new TimeSpan(t.value(), t.unit());
        Assert.assertTrue(n.equals(t));
        Assert.assertTrue(t.equals(n));
        Assert.assertEquals(n.hashCode(), t.hashCode());
        Assert.assertEquals(t.compareTo(n), 0);
        Assert.assertEquals(n.compareTo(t), 0);
    }

    /**
     * Tests {@link Comparable#compareTo(Object)}.
     *
     * @param t
     *            the testing instance. It must not be {@code null}.
     */
    @Test(dataProvider = "instances")
    public void testCompare(TimeSpan t) {
        final TimeSpan next = new TimeSpan(t.value() + 1, t.unit());
        final TimeSpan prev = new TimeSpan(t.value() - 1, t.unit());
        Assert.assertTrue(next.compareTo(t) > 0);
        Assert.assertTrue(t.compareTo(next) < 0);
        Assert.assertTrue(prev.compareTo(t) < 0);
        Assert.assertTrue(t.compareTo(prev) > 0);

        for (TimeUnit unit : TimeUnit.values()) {
            final TimeSpan u = t.unit(unit);
            Assert.assertEquals(u.compareTo(t), 0);
            Assert.assertEquals(t.compareTo(u), 0);
            Assert.assertTrue((unit == t.unit()) || !t.equals(u));
            Assert.assertTrue((unit == t.unit()) || !u.equals(t));
        }
    }

    /**
     * Tests {@link TimeSpan#nanoseconds()}.
     *
     * @param t
     *            the testing instance. It must not be {@code null}.
     */
    @Test(dataProvider = "instances")
    public void testNanoseconds(TimeSpan t) {
        Assert.assertEquals(t.nanoseconds(), t.unit().toNanos(t.value()));
    }

    /**
     * Tests conversion and unit changing method.
     *
     * @param t
     *            the testing instance. It must not be {@code null}.
     */
    @Test(dataProvider = "instances")
    public void testConversions(TimeSpan t) {
        for (TimeUnit unit : TimeUnit.values()) {
            final TimeSpan converted = t.unit(unit);
            Assert.assertEquals(converted.unit(), unit);
            Assert.assertEquals(converted.value(), t.convert(unit));
            Assert.assertEquals(converted.value(), unit.convert(t.nanoseconds(), TimeUnit.NANOSECONDS));
        }
    }

    /**
     * Tests arithmetics.
     *
     * @param origin
     *            the origin for operations
     */
    @Test(dataProvider = "instances")
    public void testArithmetics(TimeSpan origin) {
        final TimeSpan zero = TimeSpan.of(0, origin.unit());

        Assert.assertEquals(origin.plus(zero), origin);
        Assert.assertEquals(zero.plus(origin), origin);

        Assert.assertEquals(origin.minus(zero), origin);
        Assert.assertEquals(zero.minus(origin), zero);

        final TimeSpan some = TimeSpan.of(10, origin.unit());

        Assert.assertEquals(origin.plus(some), some.plus(origin));
        Assert.assertEquals(origin, origin.plus(some).minus(some));
        Assert.assertEquals(origin, some.plus(origin).minus(some));
        Assert.assertTrue(origin.plus(some).compareTo(origin) > 0);

        final TimeSpan max = TimeSpan.of(Long.MAX_VALUE, origin.unit());

        Assert.assertEquals(origin.plus(max), max);
        Assert.assertEquals(max.plus(origin), max);

        Assert.assertEquals(origin.minus(max), zero);
    }

    /**
     * Provides instances with different values for all time units.
     *
     * @return various instances
     */
    @DataProvider(name = "instances")
    public static Object[][] instances() {
        final TimeUnit[] units = TimeUnit.values();
        final Object[][] result = new Object[units.length][];

        for (TimeUnit unit : units) {
            result[unit.ordinal()] = new Object[] { new TimeSpan(12345L, unit) };
        }

        return result;
    }

    /**
     * Tests {@link TimeSpan#truncate(TimeUnit)}.
     */
    @Test
    public void testTruncating() {
        Stream.of(TimeUnit.values()).skip(1).forEach(unit -> {
            final TimeSpan t = new TimeSpan(2L, unit);
            final TimeSpan s = new TimeSpan(t.nanoseconds() + 1L, TimeUnit.NANOSECONDS, unit);

            Assert.assertFalse(t.equals(s));
            Assert.assertFalse(s.equals(t));
            Assert.assertTrue(t.compareTo(s) < 0);
            Assert.assertEquals(t, s.truncate());
        });
    }

    /**
     * Tests {@link TimeSpan#shift(long, TimeUnit)}.
     *
     * @param origin
     *            the origin of the shift. It must not be {@code null}.
     * @param shift
     *            the shift
     * @param unit
     *            the unit of the shift. It must not be {@code null}.
     * @param expected
     *            the expected value. It must not be {@code null}.
     */
    @Test(dataProvider = "shifting")
    public void testShifting(TimeSpan origin, long shift, TimeUnit unit, TimeSpan expected) {
        Assert.assertEquals(origin.shift(shift, unit), expected);

        if (0 <= shift) { // Should be equivalent
            Assert.assertEquals(origin.plus(TimeSpan.of(shift, unit)), expected);
        }
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "shifting")
    public static Object[][] shifting() {
        final List<Object[]> result = new ArrayList<>();

        for (TimeUnit unit : TimeUnit.values()) {
            // @formatter:off
            result.add(new Object[] { TimeSpan.of(0, unit), 10, unit, TimeSpan.of(10, unit) });
            result.add(new Object[] { TimeSpan.of(10, unit), -5, unit, TimeSpan.of(5, unit) });
            result.add(new Object[] { TimeSpan.of(5, unit), -10, unit, TimeSpan.of(0, unit) });
            result.add(new Object[] { TimeSpan.of(Long.MAX_VALUE, unit), 10, unit, TimeSpan.of(Long.MAX_VALUE, unit) });
            result.add(new Object[] { TimeSpan.of(10, unit), Long.MAX_VALUE, unit, TimeSpan.of(Long.MAX_VALUE, unit) });
            // @formatter:on
        }

        return result.toArray(new Object[result.size()][]);
    }
}
