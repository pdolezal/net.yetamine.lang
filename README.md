# net.yetamine.lang #

This repository provides a library with small extensions of the Java runtime libraries.

What can be found here:

* Quoting helper for formatting messages (especially for logging and debugging purposes).
* Support for adapting arbitrary objects for use in try-with-resources.
* Utilities for dealing with `TimeUnit`-based quantities.
* Simple fix-sized containers like `Tuple2` and `Tuple3`.
* Extensions for `Optional` processing.
* Trivalent logic value type.
* `Cloneable` support.
* Support for serializable singleton and multiton implementations.
* And some more minor additions.


## Examples ##

Using `Quoting` and `Throwables`:

```{java}
try {
    // Do some stuff
} catch (IllegalArgumentException e) {
    // Null-tolerant quoting that does not quote null
    final String m = String.format("No element found for %s.", Quoting.single(key));
    // When missing cause-providing constructor, this is a compact alternative
    throw Throwables.init(new NoSuchElementException(m), e);
}
```

Using `Single` to find an element, which must be single, in a stream:

```{java}
element = Single.from(collection.stream().filter(condition)).orElseThrow(NoSuchElementException::new);
// Now 'element' contains the single value produced by the stream. If there are more values available,
// or no values at all, NoSuchElementException is thrown instead.
```

A function wants to return two values, using `Tuple2`:

```{java}
Tuple2<String, Integer> foo() {
    // Some code
    return Tuple2.of(string, integer);
}

// Using then in the classical way
final Tuple2<String, Integer> result = foo();
if (result.get2() > 0) {
    System.out.println(result.get1());
}

// Or the equivalent code in a more functional manner
foo().map(t -> (t.get2() > 0) ? Optional.of(t.get1()) : Optional.empty()).ifPresent(System.out::println);
```

And there is much more. Feel free to discover better patterns and let us know!


## Prerequisites ##

For building this project is needed:

* JDK 8 or newer.
* Maven 3.3 or newer.

For using the built library is needed:

* JRE 8 or newer.


## Licensing ##

The whole content of this repository is licensed under the [CC BY-SA 4.0][CC-BY-SA] license. Contributions are accepted only under the same licensing terms, under the terms of [CC BY 4.0][CC-BY], or under a public domain license (like [CC0][CC0]), so that the work based on the contributions might be published under the CC BY-SA license terms.

[CC-BY-SA]:  http://creativecommons.org/licenses/by-sa/4.0/
[CC-BY]:     http://creativecommons.org/licenses/by/4.0/
[CC0]:       http://creativecommons.org/choose/zero/

[![Yetamine logo](http://petr.dolezal.matfyz.cz/files/Yetamine_small.svg "Our logo")](http://petr.dolezal.matfyz.cz/files/Yetamine_large.svg)
