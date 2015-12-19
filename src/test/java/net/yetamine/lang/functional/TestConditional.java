package net.yetamine.lang.functional;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.yetamine.lang.containers.Box;

/**
 * Tests {@link Conditional}.
 *
 * <p>
 * This is more a demonstration how to use it rather than a regular test.
 */
@SuppressWarnings("javadoc")
public final class TestConditional {

    @Test
    public void testStatic1() {
        final List<Integer> l = Arrays.asList(1, 2, 3);
        final Box<Boolean> box = Box.empty();

        // Static variant: consumer executed
        if (Conditional.ifAbsent(l.stream().findFirst(), i -> box.accept(i == 1))) {
            box.accept(false);
        }

        Assert.assertTrue(box.get());
    }

    @Test
    public void testStatic2() {
        final List<Integer> l = Arrays.asList(1, 2, 3);
        final Box<Boolean> box = Box.empty();

        // Static variant: consumer not executed, if condition true (hence if block executed)
        if (Conditional.ifAbsent(l.stream().filter(i -> i < 0).findFirst(), i -> box.accept(false))) {
            box.accept(true);
        }

        Assert.assertTrue(box.get());
    }

    @Test
    public void testInstance1() {
        final List<Integer> l = Arrays.asList(1, 2, 3);
        final Box<Boolean> box = Box.empty();

        // Static variant: consumer executed
        if (Conditional.when(l.stream().findFirst()).absent(i -> box.accept(i == 1))) {
            box.accept(false);
        }

        Assert.assertTrue(box.get());
    }

    @Test
    public void testInstance2() {
        final List<Integer> l = Arrays.asList(1, 2, 3);
        final Box<Boolean> box = Box.empty();

        // Instance variant: consumer not executed, if condition true (hence if block executed)
        if (Conditional.when(l.stream().filter(i -> i < 0).findFirst()).absent(i -> box.accept(false))) {
            box.accept(true);
        }

        Assert.assertTrue(box.get());
    }

}
