package net.yetamine.lang.functional;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.Box;

/**
 * Tests {@link BiConsumers}.
 */
public final class TestBiConsumers {


    /**
     * Tests conditional consumers.
     */
    @Test
    public void testConditional() {
        final BiPredicate<Box<Integer>, Integer> notEqual = (b, i) -> !b.get().equals(i);
        final BiConsumer<Box<Integer>, Integer> consumer = (b, i) -> b.replace(v -> v + i);
        final BiConsumer<Box<Integer>, Integer> conditional = BiConsumers.conditional(notEqual, consumer);

        final Box<Integer> value = Box.of(0);

        conditional.accept(value, 1);
        Assert.assertEquals(value.get(), Integer.valueOf(1));

        conditional.accept(value, 1);
        Assert.assertEquals(value.get(), Integer.valueOf(1)); // Not incremented second time
    }

    /**
     * Tests {@link BiConsumers#sequential(Iterable)}.
     */
    @Test
    public void testSequential() {
        final BiConsumer<Box<Integer>, Integer> a1 = (b, i) -> b.replace(v -> v + i);
        final BiConsumer<Box<Integer>, Integer> a2 = (b, i) -> b.replace(v -> v * i);

        final BiConsumer<Box<Integer>, Integer> a = BiConsumers.sequential(Arrays.asList(a1, a2));

        final Box<Integer> value = Box.of(2);
        a.accept(value, 2);
        Assert.assertEquals(value.get(), Integer.valueOf(8));
    }
}
