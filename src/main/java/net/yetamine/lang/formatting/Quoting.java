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

package net.yetamine.lang.formatting;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A utility class for quoting strings, which is especially useful for
 * formatting error and debugging or log messages.
 */
public final class Quoting implements Function<Object, String> {

    // Support for quoting as a function object

    /** Opening quote. */
    private final String opening;
    /** Closing quote. */
    private final String closing;
    /** Supplier of the missing content surrogate. */
    private final Supplier<String> missing;
    /** Escaping function for processing existing content. */
    private final Function<? super String, String> escape;

    /**
     * Creates a new instance.
     *
     * @param open
     *            the opening quote. It must not be {@code null}.
     * @param close
     *            the closing quote. It must not be {@code null}.
     * @param none
     *            the supplier of the value which is the surrogate for
     *            {@code null}. It must not be {@code null}.
     * @param escaping
     *            the escaping function. It must not be {@code null}.
     */
    private Quoting(String open, String close, Supplier<String> none, Function<? super String, String> escaping) {
        closing = close;
        opening = open;
        missing = none;
        escape = escaping;

        assert (opening != null);
        assert (closing != null);
        assert (missing != null);
        assert (escape != null);
    }

    /**
     * Creates a simple quoting function, using the given quote string for both
     * opening and closing quote.
     *
     * @param quote
     *            the string to use for both opening and closing quotes. It must
     *            not be {@code null}.
     * @param none
     *            the surrogate for {@code null} arguments
     *
     * @return the quoting function
     */
    public static Quoting simple(String quote, String none) {
        Objects.requireNonNull(quote);
        return new Quoting(quote, quote, () -> none, Function.identity());
    }

    /**
     * Creates a simple quoting function, using the given quote string for both
     * opening and closing quote; for {@code null} arguments, {@code "null"} is
     * returned.
     *
     * @param quote
     *            the string to use for both opening and closing quotes. It must
     *            not be {@code null}.
     *
     * @return the quoting function
     */
    public static Quoting with(String quote) {
        return simple(quote, "null");
    }

    /**
     * Creates a simple quoting function, using the given opening and closing
     * quote; for {@code null} arguments, {@code "null"} is returned.
     *
     * @param opening
     *            the string to use for opening quotes. It must not be
     *            {@code null}.
     * @param closing
     *            the string to use for closing quotes. It must not be
     *            {@code null}.
     *
     * @return the quoting function
     */
    public static Quoting with(String opening, String closing) {
        Objects.requireNonNull(opening);
        Objects.requireNonNull(closing);
        return new Quoting(opening, closing, () -> "null", Function.identity());
    }

    /**
     * Returns a quoting function with the same parameters, but using the given
     * value instead of {@code null} arguments.
     *
     * @param value
     *            the value to use as the replacement for {@code null} arguments
     *            of the returned function
     *
     * @return the quoting function
     */
    public Quoting missing(String value) {
        return new Quoting(opening, closing, () -> value, escape);
    }

    /**
     * Returns a quoting function with the same parameters, but using the given
     * supplier of values to use instead of {@code null} arguments.
     *
     * @param supplier
     *            the supplier of values to use as the replacement for
     *            {@code null} arguments of the returned function. It must not
     *            be {@code null}.
     *
     * @return the quoting function
     */
    public Quoting missing(Supplier<String> supplier) {
        return new Quoting(opening, closing, Objects.requireNonNull(supplier), escape);
    }

    /**
     * Returns a quoting function with the same parameters, but using the given
     * function for escaping the values of non-{@code null} arguments.
     *
     * @param escaping
     *            the function for escaping the values of non-{@code null}
     *            arguments. It must not be {@code null}.
     *
     * @return the quoting function
     */
    public Quoting escape(Function<? super String, String> escaping) {
        return new Quoting(opening, closing, missing, Objects.requireNonNull(escaping));
    }

    /**
     * Returns the quoted string.
     *
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    public String apply(Object o) {
        if (o == null) {
            return missing.get();
        }

        return new StringBuilder().append(opening).append(escape.apply(o.toString())).append(closing).toString();
    }

    // Optimized common cases for direct use

    /**
     * Quotes an object's string representation, or returns a default value if
     * the argument is {@code null}.
     *
     * @param o
     *            the object to quote
     * @param quote
     *            the quote. It must not be {@code null}.
     * @param none
     *            the default value
     *
     * @return the quoted representation
     */
    public static String quote(Object o, String quote, String none) {
        return (o != null) ? quote(o, quote) : none;
    }

    /**
     * Quotes an object's string representation, or returns a default value if
     * the argument is {@code null}.
     *
     * @param o
     *            the object to quote
     * @param quote
     *            the quote. It must not be {@code null}.
     * @param none
     *            the default value supplier. It must not be {@code null}.
     *
     * @return the quoted representation
     */
    public static String quote(Object o, String quote, Supplier<String> none) {
        // Use assert to check because of performance reasons
        assert (none != null) : "Default value supplier must not be null.";
        return (o != null) ? quote(o, quote) : none.get();
    }

    /**
     * Quotes an object's string representation, or returns a default value if
     * the argument is {@code null}.
     *
     * @param o
     *            the object to quote
     * @param quote
     *            the quote. It must not be {@code null}.
     * @param none
     *            the default value
     *
     * @return the quoted representation
     */
    public static String quote(Object o, char quote, String none) {
        return (o != null) ? quote(o, quote) : none;
    }

    /**
     * Quotes an object's string representation, or returns a default value if
     * the argument is {@code null}.
     *
     * @param o
     *            the object to quote
     * @param quote
     *            the quote. It must not be {@code null}.
     * @param none
     *            the default value supplier. It must not be {@code null}.
     *
     * @return the quoted representation
     */
    public static String quote(Object o, char quote, Supplier<String> none) {
        // Use assert to check because of performance reasons
        assert (none != null) : "Default value supplier must not be null.";
        return (o != null) ? quote(o, quote) : none.get();
    }

    /**
     * Quotes an object's representation with double quotes.
     *
     * <p>
     * This method performs no escaping of the representation. If the argument
     * is {@code null}, the string "null" is returned, which is sufficient to
     * distinguish a missing object from an object that returns such a string.
     *
     * @param o
     *            the object whose representation shall be quoted
     *
     * @return the quoted representation
     */
    public static String normal(Object o) {
        return quote(o, '\"', "null");
    }

    /**
     * Quotes an object's representation with single quotes.
     *
     * <p>
     * This method performs no escaping of the representation. If the argument
     * is {@code null}, the string "null" is returned, which is sufficient to
     * distinguish a missing object from an object that returns such a string.
     *
     * @param o
     *            the object whose representation shall be quoted
     *
     * @return the quoted representation
     */
    public static String single(Object o) {
        return quote(o, '\'', "null");
    }

    /**
     * Quotes an object's representation with single quotes.
     *
     * @param o
     *            the object whose representation shall be quoted. It must not
     *            be {@code null}.
     *
     * @return the quoted representation
     */
    private static String quote(Object o, String quote) {
        final String string = o.toString();
        final int length = (quote.length() * 2) + string.length();
        return new StringBuilder(length).append(quote).append(o).append(quote).toString();
    }

    /**
     * Quotes an object's representation with single quotes.
     *
     * @param o
     *            the object whose representation shall be quoted. It must not
     *            be {@code null}.
     *
     * @return the quoted representation
     */
    private static String quote(Object o, char quote) {
        final String string = o.toString();
        final int length = 2 + string.length();
        return new StringBuilder(length).append(quote).append(o).append(quote).toString();
    }
}
