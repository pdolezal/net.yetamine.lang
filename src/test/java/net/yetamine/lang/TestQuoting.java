package net.yetamine.lang;

import java.util.function.Function;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests {@link Quoting}.
 */
public final class TestQuoting {

    /**
     * Tests quoting with constant fallback.
     */
    @Test
    public void testQuoteWithConstant() {
        final String o = "string";
        Assert.assertEquals(Quoting.quote(o, '*', "none"), "*string*");
        Assert.assertEquals(Quoting.quote(o, "*", "none"), "*string*");
        Assert.assertEquals(Quoting.quote(o, '*', (String) null), "*string*");
        Assert.assertEquals(Quoting.quote(o, "*", (String) null), "*string*");
        Assert.assertEquals(Quoting.quote(null, '*', "none"), "none");
        Assert.assertEquals(Quoting.quote(null, "*", "none"), "none");
        Assert.assertNull(Quoting.quote(null, '*', (String) null));
        Assert.assertNull(Quoting.quote(null, "*", (String) null));
    }

    /**
     * Tests quoting with dynamic fallback.
     */
    @Test
    public void testQuoteWithSupplier() {
        final String o = "string";
        Assert.assertEquals(Quoting.quote(o, '*', () -> "none"), "*string*");
        Assert.assertEquals(Quoting.quote(o, "*", () -> "none"), "*string*");
        Assert.assertEquals(Quoting.quote(o, '*', () -> null), "*string*");
        Assert.assertEquals(Quoting.quote(o, "*", () -> null), "*string*");
        Assert.assertEquals(Quoting.quote(null, '*', () -> "none"), "none");
        Assert.assertEquals(Quoting.quote(null, "*", () -> "none"), "none");
        Assert.assertNull(Quoting.quote(null, '*', () -> null));
        Assert.assertNull(Quoting.quote(null, "*", () -> null));
    }

    /**
     * Tests quoting without a supplier.
     */
    @Test(expectedExceptions = { AssertionError.class })
    public void testQuoteWithoutSupplier1() {
        final Supplier<String> supplier = null;
        Quoting.quote("string", '*', supplier);
    }

    /**
     * Tests quoting without a supplier.
     */
    @Test(expectedExceptions = { AssertionError.class })
    public void testQuoteWithoutSupplier2() {
        final Supplier<String> supplier = null;
        Quoting.quote("string", "*", supplier);
    }

    /**
     * Tests {@link Quoting#single(Object)}.
     */
    @Test
    public void testSingle() {
        Assert.assertEquals(Quoting.single(null), "null");
        Assert.assertEquals(Quoting.single("string"), "'string'");
    }

    /**
     * Tests {@link Quoting#normal(Object)}.
     */
    @Test
    public void testNormal() {
        Assert.assertEquals(Quoting.normal(null), "null");
        Assert.assertEquals(Quoting.normal("string"), "\"string\"");
    }

    /**
     * Tests {@link Quoting} as a function instance with different parameters.
     *
     * @param quoting
     *            the quoting function. It must not be {@code null}.
     * @param value
     *            the value to quote. It must not be {@code null}.
     * @param expected
     *            the expected value
     * @param nulled
     *            the value for using {@code null} as the value to quote instead
     *            of the given one
     */
    @Test(dataProvider = "functions")
    public void testFunction(Function<Object, String> quoting, Object value, String expected, String nulled) {
        Assert.assertEquals(quoting.apply(value), expected);
        Assert.assertEquals(quoting.apply(null), nulled);
    }

    @SuppressWarnings("javadoc")
    @DataProvider(name = "functions")
    public static Object[][] functions() {
        return new Object[][] {
            // @formatter:off
            { Quoting.with("*"),                        "string", "*string*", "null"    },
            { Quoting.with("*").missing("none"),        "string", "*string*", "none"    },
            { Quoting.with("*").missing((String) null), "string", "*string*", null      },
            { Quoting.with("*").missing(() -> "none"),  "string", "*string*", "none"    },
            { Quoting.with("*").missing(() -> null),    "string", "*string*", null      },

            { Quoting.with("<", ">"),                       "string", "<string>", "null"    },
            { Quoting.with("<", ">").missing("none"),       "string", "<string>", "none"    },
            { Quoting.with("<", ">").missing(() -> "none"), "string", "<string>", "none"    },
            { Quoting.with("<", ">").missing(() -> null),   "string", "<string>", null      },

            { Quoting.with("<", ">").escape(s -> s.substring(0, 1)),                    "string", "<s>", "null" },
            { Quoting.with("<", ">").escape(s -> s.substring(0, 1)).missing("none"),    "string", "<s>", "none" },
            { Quoting.with("<", ">").missing("none").escape(s -> s.substring(0, 1)),    "string", "<s>", "none" }
            // @formatter:on
        };
    }
}
