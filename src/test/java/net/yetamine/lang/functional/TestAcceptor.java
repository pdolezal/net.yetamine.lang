package net.yetamine.lang.functional;

import java.util.Arrays;
import java.util.function.Function;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.Box;

/**
 * Tests {@link Acceptor}.
 */
public final class TestAcceptor {

    /**
     * Tests {@link Acceptor#apply(Object)}.
     */
    @Test
    public void testApply() {
        // This Acceptor increments the value in the given box
        final Acceptor<Box<Integer>> acceptor = b -> b.replace(i -> i + 1);

        final Box<Integer> value = Box.of(1);
        final Box<Integer> result = acceptor.apply(value);
        Assert.assertSame(result, value);
        Assert.assertEquals(Integer.valueOf(2), result.get());
    }

    /**
     * Tests {@link Acceptor#andThen(java.util.function.Consumer)}.
     */
    @Test
    public void testAndThen() {
        final Acceptor<Box<Integer>> acceptor = b -> b.replace(i -> i + 1);
        final Acceptor<Box<Integer>> andThen = acceptor.andThen(acceptor);

        final Box<Integer> value = Box.of(1);
        final Box<Integer> result = andThen.apply(value);
        Assert.assertSame(result, value);
        Assert.assertEquals(Integer.valueOf(3), result.get());
    }

    /**
     * Tests {@link Acceptor#onlyIf(java.util.function.Predicate)}.
     */
    @Test
    public void testOnlyIf() {
        final Acceptor<Box<Integer>> acceptor = b -> b.replace(i -> i + 1);

        // Increments only even numbers
        final Acceptor<Box<Integer>> even = acceptor.onlyIf(b -> (b.get() % 2) == 0);

        final Box<Integer> value = Box.of(0);
        Assert.assertEquals(even.apply(value).get(), Integer.valueOf(1));
        Assert.assertEquals(even.apply(value).get(), Integer.valueOf(1)); // Not incremented second time
    }

    /**
     * Tests {@link Acceptor#finish(Function)}.
     */
    @Test
    public void testFinish() {
        final Acceptor<Box<Object>> acceptor = b -> b.accept(new Object());
        final Function<Box<Object>, Object> function = acceptor.finish(Box::get);

        final Box<Object> value = Box.empty();
        Assert.assertSame(function.apply(value), value.get());
    }

    /**
     * Tests {@link Acceptor#sequential(Iterable)}.
     */
    @Test
    public void testSequential() {
        final Acceptor<Box<Integer>> a1 = b -> b.replace(i -> i + 1);
        final Acceptor<Box<Integer>> a2 = b -> b.replace(i -> i * i);

        final Acceptor<Box<Integer>> a = Acceptor.sequential(Arrays.asList(a1, a2));
        Assert.assertEquals(a.apply(Box.of(2)).get(), Integer.valueOf(9));
    }
}
