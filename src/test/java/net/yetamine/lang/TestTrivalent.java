package net.yetamine.lang;

import java.util.Arrays;
import java.util.Collection;

import net.yetamine.lang.containers.Box;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link Trivalent}.
 */
public final class TestTrivalent {

    /**
     * Tests operation commutativity.
     */
    public void testCommutativity() {
        final Collection<Trivalent> values = Arrays.asList(Trivalent.values());
        for (Trivalent a : values) {
            for (Trivalent b : values) {
                Assert.assertEquals(a.and(b), b.and(a));
                Assert.assertEquals(a.or(b), b.or(a));
            }
        }
    }

    /**
     * Tests {@link Trivalent#and(Trivalent)} and its overloads.
     *
     * @param value
     *            this value. It must not be {@code null}.
     * @param other
     *            the operand. It must not be {@code null}.
     * @param result
     *            the expected result. It must not be {@code null}.
     */
    @Test(dataProvider = "and")
    public void testAnd(Trivalent value, Trivalent other, Trivalent result) {
        Assert.assertEquals(value.and(other), result);

        if (other.isBoolean()) {
            final boolean asBoolean = other.asBoolean();
            Assert.assertEquals(value.and(asBoolean), result);
            Assert.assertEquals(value.and(Boolean.valueOf(asBoolean)), result);
        }
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "and")
    public static Object[][] and() {
        return new Object[][] {
            // @formatter:off
            { Trivalent.UNKNOWN,    Trivalent.UNKNOWN,  Trivalent.UNKNOWN   },
            { Trivalent.UNKNOWN,    Trivalent.FALSE,    Trivalent.FALSE     },
            { Trivalent.UNKNOWN,    Trivalent.TRUE,     Trivalent.UNKNOWN   },

            { Trivalent.FALSE,      Trivalent.UNKNOWN,  Trivalent.FALSE     },
            { Trivalent.FALSE,      Trivalent.FALSE,    Trivalent.FALSE     },
            { Trivalent.FALSE,      Trivalent.TRUE,     Trivalent.FALSE     },

            { Trivalent.TRUE,       Trivalent.UNKNOWN,  Trivalent.UNKNOWN   },
            { Trivalent.TRUE,       Trivalent.FALSE,    Trivalent.FALSE     },
            { Trivalent.TRUE,       Trivalent.TRUE,     Trivalent.TRUE      }
            // @formatter:on
        };
    }

    /**
     * Tests {@link Trivalent#or(Trivalent)} and its overloads.
     *
     * @param value
     *            this value. It must not be {@code null}.
     * @param other
     *            the operand. It must not be {@code null}.
     * @param result
     *            the expected result. It must not be {@code null}.
     */
    @Test(dataProvider = "or")
    public void testOr(Trivalent value, Trivalent other, Trivalent result) {
        Assert.assertEquals(value.or(other), result);

        if (other.isBoolean()) {
            final boolean asBoolean = other.asBoolean();
            Assert.assertEquals(value.or(asBoolean), result);
            Assert.assertEquals(value.or(Boolean.valueOf(asBoolean)), result);
        }
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "or")
    public static Object[][] or() {
        return new Object[][] {
            // @formatter:off
            { Trivalent.UNKNOWN,    Trivalent.UNKNOWN,  Trivalent.UNKNOWN   },
            { Trivalent.UNKNOWN,    Trivalent.FALSE,    Trivalent.UNKNOWN   },
            { Trivalent.UNKNOWN,    Trivalent.TRUE,     Trivalent.TRUE      },

            { Trivalent.FALSE,      Trivalent.UNKNOWN,  Trivalent.UNKNOWN   },
            { Trivalent.FALSE,      Trivalent.FALSE,    Trivalent.FALSE     },
            { Trivalent.FALSE,      Trivalent.TRUE,     Trivalent.TRUE      },

            { Trivalent.TRUE,       Trivalent.UNKNOWN,  Trivalent.TRUE      },
            { Trivalent.TRUE,       Trivalent.FALSE,    Trivalent.TRUE      },
            { Trivalent.TRUE,       Trivalent.TRUE,     Trivalent.TRUE      }
            // @formatter:on
        };
    }

    /**
     * Tests {@link Trivalent#negation()}.
     *
     * @param value
     *            this value. It must not be {@code null}.
     * @param negation
     *            the expected result. It must not be {@code null}.
     */
    @Test(dataProvider = "negation")
    public void testNegation(Trivalent value, Trivalent negation) {
        Assert.assertEquals(value.negation(), negation);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "negation")
    public static Object[][] negation() {
        return new Object[][] {
            // @formatter:off
            { Trivalent.UNKNOWN,    Trivalent.UNKNOWN   },
            { Trivalent.FALSE,      Trivalent.TRUE      },
            { Trivalent.TRUE,       Trivalent.FALSE     }
            // @formatter:on
        };
    }

    /**
     * Tests interoperability with Boolean types.
     */
    @Test
    public void testBooleans() {
        Assert.assertEquals(Trivalent.FALSE.toBoolean().get(), Boolean.FALSE);
        Assert.assertEquals(Trivalent.TRUE.toBoolean().get(), Boolean.TRUE);
        Assert.assertFalse(Trivalent.UNKNOWN.toBoolean().isPresent());

        Assert.assertEquals(Trivalent.fromBoolean(Boolean.FALSE), Trivalent.FALSE);
        Assert.assertEquals(Trivalent.fromBoolean(Boolean.TRUE), Trivalent.TRUE);

        Assert.assertEquals(Trivalent.fromBoolean(false), Trivalent.FALSE);
        Assert.assertEquals(Trivalent.fromBoolean(true), Trivalent.TRUE);

        Assert.assertTrue(Trivalent.TRUE.isBoolean());
        Assert.assertTrue(Trivalent.FALSE.isBoolean());
        Assert.assertFalse(Trivalent.UNKNOWN.isBoolean());

        Assert.assertFalse(Trivalent.FALSE.asBoolean());
        Assert.assertTrue(Trivalent.TRUE.asBoolean());
    }

    /**
     * Tests casting to non-Booleans.
     */
    @Test(expectedExceptions = { ClassCastException.class })
    public void testNonBoolean() {
        Trivalent.UNKNOWN.asBoolean();
    }

    /**
     * Tests indicative methods.
     *
     * @param value
     *            this value. It must not be {@code null}.
     * @param isTrue
     *            the result for {@link Trivalent#isTrue()}
     * @param isFalse
     *            the result for {@link Trivalent#isFalse()}
     * @param isUnknown
     *            the result for {@link Trivalent#isUnknown()}
     */
    @Test(dataProvider = "indicatives")
    public void testIndicatives(Trivalent value, boolean isTrue, boolean isFalse, boolean isUnknown) {
        Assert.assertEquals(value.isUnknown(), isUnknown);
        Assert.assertEquals(value.isFalse(), isFalse);
        Assert.assertEquals(value.isTrue(), isTrue);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "indicatives")
    public static Object[][] indicatives() {
        return new Object[][] {
            // @formatter:off
            { Trivalent.UNKNOWN,    false, false, true },
            { Trivalent.FALSE,      false, true, false },
            { Trivalent.TRUE,       true, false, false }
            // @formatter:on
        };
    }

    /**
     * Tests all "if" methods.
     */
    public void testIfs() {
        final Box<Boolean> box = Box.empty();

        for (Trivalent value : Trivalent.values()) {
            box.accept(Boolean.FALSE);
            value.ifUnknown(() -> box.accept(Boolean.TRUE));
            Assert.assertEquals(box.get().booleanValue(), value.isUnknown());

            box.accept(Boolean.FALSE);
            value.ifTrue(() -> box.accept(Boolean.TRUE));
            Assert.assertEquals(box.get().booleanValue(), value.isTrue());

            box.accept(Boolean.FALSE);
            value.ifFalse(() -> box.accept(Boolean.TRUE));
            Assert.assertEquals(box.get().booleanValue(), value.isFalse());

            box.accept(null); // Simulate Trivalent with null Boolean
            value.ifBoolean(b -> box.accept(b));

            if (value.isBoolean()) {
                Assert.assertEquals(box.get().booleanValue(), value.asBoolean());
            } else {
                Assert.assertNull(box.get());
            }
        }
    }
}
