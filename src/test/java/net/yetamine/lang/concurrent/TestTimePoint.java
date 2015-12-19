package net.yetamine.lang.concurrent;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests {@link TimePoint}.
 */
public final class TestTimePoint {

    /**
     * Tests construction of the values and their mutual relationships.
     */
    @Test
    public void testConstruction() {
        final TimePoint past = TimePoint.past();
        final TimePoint future = TimePoint.future();
        final TimePoint present = TimePoint.present();

        Assert.assertTrue(past.passed());
        Assert.assertTrue(past.isPast());
        Assert.assertFalse(past.isFuture());

        Assert.assertFalse(future.passed());
        Assert.assertFalse(future.isPast());
        Assert.assertTrue(future.isFuture());

        Assert.assertTrue(future.compareTo(past) > 0);
        Assert.assertTrue(future.compareTo(present) > 0);
        Assert.assertTrue(future.compareTo(future) == 0);

        Assert.assertTrue(past.compareTo(past) == 0);
        Assert.assertTrue(past.compareTo(present) < 0);
        Assert.assertTrue(past.compareTo(future) < 0);

        Assert.assertTrue(present.compareTo(past) > 0);
        Assert.assertTrue(present.compareTo(present) == 0);
        Assert.assertTrue(present.compareTo(future) < 0);
    }

    /**
     * Tests period-based constructions.
     */
    @Test
    public void testWithin() {
        final TimePoint present = TimePoint.present();

        for (TimeUnit unit : TimeUnit.values()) {
            final TimePoint p1 = TimePoint.within(10L, unit);
            Assert.assertTrue(p1.compareTo(present) > 0);

            final TimePoint p2 = TimePoint.within(TimeSpan.of(10L, unit));
            Assert.assertTrue(p2.compareTo(present) > 0);

            Assert.assertTrue(p1.compareTo(p2) <= 0);
        }
    }

    /**
     * Tests time flowing and relationship of the time points.
     *
     * @throws InterruptedException
     *             if waiting is interrupted
     */
    @Test(timeOut = 10000L)
    public void testFlow() throws InterruptedException {
        final TimePoint before = TimePoint.present();
        Assert.assertTrue(before.passed());

        TimeSpan.of(1L, TimeUnit.SECONDS).sleep();
        final TimePoint after = TimePoint.present();

        // The timer has some jitter and we can't be sure that sleep() lasts as
        // long as we specified with the nanosecond accuracy. So we are OK with
        // mere half second delay (realistically, it should be above 900 ms).
        final TimeSpan difference = before.remainingTo(after, TimeUnit.MILLISECONDS);
        Assert.assertTrue(difference.truncate(difference.unit()).value() > 500);

        Assert.assertTrue(after.passed());
        Assert.assertTrue(before.passed());
        Assert.assertTrue(before.isPast());

        Assert.assertEquals(after.remainingNanos(TimeUnit.NANOSECONDS), 0);
        Assert.assertEquals(after.remaining(TimeUnit.NANOSECONDS).value(), 0);

        Assert.assertTrue(before.compareTo(after) < 0);

        Assert.assertEquals(before.plus(difference), after);
        Assert.assertEquals(before.plus(difference.nanoseconds(), TimeUnit.NANOSECONDS), after);
    }

    /**
     * Tests extreme values for additions.
     */
    @Test
    public void testPlus() {
        final TimePoint present = TimePoint.present();

        Assert.assertTrue(present.plus(Integer.MAX_VALUE, TimeUnit.NANOSECONDS).isFuture());
        Assert.assertTrue(present.plus(Long.MAX_VALUE, TimeUnit.NANOSECONDS).isFuture());

        Assert.assertTrue(present.plus(Integer.MIN_VALUE, TimeUnit.NANOSECONDS).isPast());
        Assert.assertTrue(present.plus(Long.MIN_VALUE, TimeUnit.NANOSECONDS).isPast());

        Assert.assertTrue(present.plus(10, TimeUnit.DAYS).isFuture());
    }
}
