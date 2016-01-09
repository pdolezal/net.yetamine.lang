# net.yetamine.lang #

This repository provides a library with small extensions of the Java runtime libraries.

What can be found here:

* A utility for smart quoting of values for message formatting (especially for logging and debugging purposes).
* Support for adapting arbitrary objects for use in try-with-resources.
* Support classes for dealing with `TimeUnit`-based quantities.
* Simple fix-sized containers like `Tuple2` and `Tuple3`.
* A mutable `Box` for objects, which is useful for accumulators and for "out" method parameters.
* Companions for `Optional`: `Choice` and `Single`.
* Trivalent logic value type `Trivalent`.
* `Cloneable` support – no more fiddling with reflection at home.
* Support for serializable singleton and multiton implementations.
* And some more minor additions, mostly to ease working with functional interfaces. Especially interesting is the `Traversing` interface that shows a pattern to `null`-safe traversal through graph structures (this should interest all JSON lovers).


## Examples ##

Let's have a look at a few code snippets demonstrating some of the functionality that this library provides.


### Error messages ###

Error messages should contain the values that indicate the root of the problem. It is nice to have the actual values quoted in a way, so that they do not blend with the rest of the message, but when the value could be `null`, a problem comes.

Having quotes in the message template does not help always. For instance, does the message *Element is 'null'.* mean that the element is `null` or that the element displays as a string *null*? It should be *Element is null.* without the quotes when the element is indeed `null`. Here `Quoting` helps.

And let's combine `Quoting` with `Throwables` in the snippet. Some exceptions don't have any constructor for setting their cause which becomes annoying when the cause is present. So…

```{java}
try {
    // Do some stuff
} catch (IllegalArgumentException e) {
    // Null-tolerant quoting that makes the difference between "null" and null
    final String m = String.format("No element found for %s.", Quoting.single(key));
    // When missing cause-providing constructor, this is a compact alternative
    throw Throwables.init(new NoSuchElementException(m), e);
}
```


### Looking for the single occurrence ###

While `Optional` is fine and `Stream::find*` methods work well, they are not much useful when we need to know whether the provided element is indeed only one of the possibilities. What if the stream provides more of them? A compact solution? Using `Single`:

```{java}
// Filter the elements in a collection and get first one, ensuring that no other exists
element = Single.head(collection.stream().filter(condition)).orElseThrow(NoSuchElementException::new);
// Now 'element' contains the single value produced by the stream. If there are more values available,
// or no values at all, NoSuchElementException is thrown instead.
```

Throwing an exception might be too harsh. Perhaps we want to find an optimum, perhaps the preference is ordered (e.g., the later prevails), but we also want to warn there are more options and only one of them could be applied:

```{java}
// Find an element such that no other element is greater. There may be more such (equally good) 
// elements though and we want to pick one, but perhaps warn there are more available options.
optimum = collection.stream().collect(Single.collector(Single.optimum(Comparator.naturalOrder()));

if (optimum.single().isFalse()) { // Is that optimum really the only one?
    LOGGER.warn("Multiple optimal elements found, using {}.", optimum.get());
}
```


### Tuples ###

Java is hostile towards tuples and therefore alternative solutions, often better in semantic expressiveness, must be applied instead. But sometimes having a tuple is useful anyway, especially when dealing with maps and streams of map entries. Indeed, transforming map entries in a stream or retruning two or three values from a function at once is painful. Is it really necessary to make a special type everytime, even when the functionality is internal or very local? Use rather `Tuple2` or `Tuple3`.

But let's see something more appealing. What about zipping?

```{java}
// We have two lists of corresponding values:
final List<String> en = Arrays.asList("red", "green", "blue");
final List<String> de = Arrays.asList("rot", "grün", "blau");

// And we want a map like this: {red=rot, green=grün, blue=blau}
final Map<String, String> colors = new LinkedHashMap<>();
Tuple2.zip(en, de).forEach(t -> t.accept(colors::put));
// Here it is!
```

If `en` and `de` were streams, we could use `Collectors` to make the map:

```{java}
map = Tuple2.zip(en, de).collect(Collectors.toMap(Tuple2::get1, Tuple2::get2));
```


### Having a `Box` is a good thing ###

A mutable box for storing a reference is often useful, e.g., when implementing a `Collector` or when passing a value from a method using an "out" parameter. An `AtomicReference` is often too expensive, a single-element array… well, arrays are better to avoid for many reasons… The `Box` is the best solution then:

```{java}
boolean haveFun(Box<? super String> box) {
    if (new Random().nextInt() % 2 == 0) {
        box.set("Surprise!");
        return true;
   }
   
   return false;
}

// Let's have fun then:
final Box<String> box = Box.empty();

if (haveFun(box)) {
    System.out.println(box.get());
}
```

Well, the `Box` can do more. Check it out to see.


### Sometimes Boolean is not good enough ###

Usually, `true` and `false` work well. But what if you need "I don't know (yet)"? Using a `Boolean` with `null` as the third value is a bad practice. Use rather `Trivalent`:

- It is safe: no `null` unboxing and `NullPointerException` surprise.
- It provides the basic set of operators: `and`, `or`, and `not` according to Kleene's logic.
- It provides a comfortable interoperability with Boolean types.

```{java}
if (resolution.isUnknown()) {
    System.out.println("I have no data yet.");
} else {
    System.out.format("You are %s.", resolution.asBoolean() ? "right" : "wrong");
}
```

For such simple cases, `Trivalent` offers `Optional`-like monadic support:

```{java}
resolution.ifUnknown(() -> System.out.println("I have no data yet.")).ifBoolean(b -> {
	System.out.format("You are %s.", b ? "right" : "wrong");
});
```


### When `Optional` becomes awkward ###

`Optional` is great. But it can only tell that it contains an object or not. Sometimes you might need rather a container that just marks a value as acceptable or not and lets its consumer to decide how to deal with the value, because sometimes even a wrong value is better than none, e.g., when logging or when taking an alternative decision needs some information to take a better path.

But there is one more use case when `Optional` does not work very well, although it should:

```{java}
boolean greet(String name) {
    final Optional<String> result = greeting(name); // Find some optional value
    if (result.isPresent()) {
        System.out.println(result.get());
        return true;
    }
    
    return false; // No greeting for you
}
```

Try to avoid consulting `isPresent` *and* `get`. There are several ways: using `Optional::map`, storing `result.orElse(null)` and testing the intermediate result… or to use `Choice`:

```{java}
boolean greet(String name) {
    return Choice.of(greeting(name)).ifAccepted(System.out::println).isAccepted();
}
```


### Sweet `Traversing` ###

This piece of code might remind you of something:

```{java}
final String connectToAddress = configuration.getSection(NETWORK).getSection(CONNECTION).getValue(ADDRESS);
```

What is some of the objects on the path is missing? Wrapping all in a `catch` block for `NullPointerException` is terrible, but practical. Using `null` checks before diving deeper is clean, but completely impractical. Well, some may know using `Optional` for that:

```{java}
final String connectToAddress = Optional.ofNullable(configuration)
    .map(o -> o.getSection(NETWORK)).map(o -> o.getSection(CONNECTION)).map(o -> o.getValue(ADDRESS))
    .orElseThrow(() -> new IllegalArgumentException("Missing connection address."));
```

Hm. Guessing this *is* better than the `if`-not-`null`-check way, but not so much. Too much clutter here. What about:

```{java}
final String connectToAddress = NETWORK.apply(configuration).flatMap(CONNECT).flatMap(ADDRESS)
    .orElseThrow(() -> new IllegalArgumentException("Missing connection address."));
```

How to do so? Even without any modification of the type of the `configuration` variable? The trick is to let the constants implement the `Traversing` interface (which is basically a slightly polished `Function`) and that's it. Another sweet point of this approach is that the constants can be then even `enum` constants. Have a look at our example for the `Traversing` interface to see it in detail.


### And there is more… ###

Have a look, feel free to discover your ways to use that, and let us know!


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
