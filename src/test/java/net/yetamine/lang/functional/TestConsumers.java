package net.yetamine.lang.functional;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.Box;

/**
 * Tests {@link Consumers}.
 */
public final class TestConsumers {


    /**
     * Tests conditional consumers.
     */
    @Test
    public void testConditional() {
        final Predicate<Box<Integer>> even = b -> b.get() % 2 == 0;
        final Consumer<Box<Integer>> consumer = b -> b.replace(i -> i + 1);
        final Consumer<Box<Integer>> conditional = Consumers.conditional(even, consumer);

        final Box<Integer> value = Box.of(0);

        conditional.accept(value);
        Assert.assertEquals(value.get(), Integer.valueOf(1));

        conditional.accept(value);
        Assert.assertEquals(value.get(), Integer.valueOf(1)); // Not incremented second time
    }

    /**
     * Tests {@link Consumers#sequential(Iterable)}.
     */
    @Test
    public void testSequential() {
        final Consumer<Box<Integer>> a1 = b -> b.replace(i -> i + 1);
        final Consumer<Box<Integer>> a2 = b -> b.replace(i -> i * i);

        final Consumer<Box<Integer>> a = Consumers.sequential(Arrays.asList(a1, a2));

        final Box<Integer> value = Box.of(2);
        a.accept(value);
        Assert.assertEquals(value.get(), Integer.valueOf(9));
    }
}
