# net.yetamine.lang #

This repository provides a library with small extensions of the Java runtime libraries.

What can be found here:

* Trivalent logic value type `Trivalent`.
* Simple fix-sized containers like `Tuple2` and `Tuple3`.
* An unmodifiable view of a byte sequence, `ByteSequence`, to represent binary constants.
* A mutable `Box` for objects, which is useful for accumulators and for "out" method parameters.
* Companions for `Optional`: `Choice` and `Single`. `Traversing` is notable as well.
* Fluent collection interfaces and adapters to make some operations a bit easier.
* Support for adapting arbitrary objects for use in try-with-resources.
* Support for serializable singleton and multiton implementations.
* `Cloneable` support: no more fiddling with reflection.
* Some minor utilities for using functional interfaces.
* Several introspective utilities.
* Several formatting utilities.


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


### Sometimes Boolean is not good enough ###

Usually, `true` and `false` work well. But what if you need "I don't know (yet)"? Using a `Boolean` with `null` as the third value is a bad practice. Use rather `Trivalent`:

* It is safe: no `null` unboxing and `NullPointerException` surprise.
* It provides the basic set of operators: `and`, `or`, and `not` according to Kleene's logic.
* It provides a comfortable interoperability with Boolean types.

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


### Tuples ###

Java is hostile towards tuples and therefore alternative solutions, often better in semantic expressiveness, must be applied instead. But sometimes having a tuple is useful anyway, especially when dealing with maps and streams of map entries. Indeed, transforming map entries in a stream or returning two or three values from a function at once is painful. Is it really necessary to make a special type everytime, even when the functionality is internal or very local? Use rather `Tuple2` or `Tuple3`:

```{java}
// Usual construction pattern for such classes
final Tuple2<String, String> t2 = Tuple2.of("red", "rot");
// And for friends of static imports a more concise variant (here for Tuple3)
final Tuple3<String, String, Locale> t3 = tuple3("red", "rot", Locale.GERMAN);
```

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
map = Tuple2.zip(en, de).collect(Tuple2.toMap());

// Which is actually a shortcut for:
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


### When `Optional` becomes awkward ###

`Optional` is great and the `Traversing` example shows it. However, it can distinguish only whether an object is present or not and the only invalid value is `null`. Sometimes you might need rather a container that just marks a value as *true* or *false* and lets the consumer to decide how to deal with the value – sometimes even a wrong value is better than none, e.g., when logging or when taking an alternative decision needs some additional information.

Besides that, there is one more use case when `Optional` does not work very well, although it should:

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
    return Choice.of(greeting(name)).ifTrue(System.out::println).isTrue();
}
```


### Is Java Collections Framework fluent? ###

When a title is a question, the answer usually is "No!" This is the case too. Everybody probably knows how verbose task is filling a constant `Map` with a bunch of constant values. It could have been much easier if the interface would be more fluent. Fortunately, there is a way now:

```{java}
static final Map<TimeUnit, String> UNITS = FluentMap.adapt(new EnumMap<TimeUnit, String>(TimeUnit.class))
         .add(TimeUnit.NANOSECONDS, "ns")
         .add(TimeUnit.MICROSECONDS, "μs")
         .add(TimeUnit.MILLISECONDS, "ms")
         .add(TimeUnit.SECONDS, "s")
         .add(TimeUnit.MINUTES, "min")
         .add(TimeUnit.HOURS, "h")
         .add(TimeUnit.DAYS, "d")
         .withMap(Collections::unmodifiableMap);
```

If you like this, you might like following as well. `Map` interface has been upgraded much with Java 8 release and its `compute*` methods made it usable for multimaps, but still… do you really want to write the appropriate lambda at all places where you possibly need to compute the default value? And what if you have something like `Map<String, ? extends Collection<String>>` while you'd like to retain defaults? Well, the fluent adapter for a map can incorporate even constructors for default values:

```{java}
// Let's make a map which binds a supplier to get/make default values
final FluentMap<String, List<String>> m = FluentMap.adapt(new HashMap<>(), ArrayList::new);

// The 'let' method inserts the default provided by the supplier for missing
// values and returns the value, so that can be processed then
m.let("titles").addAll(Arrays.asList("Mr.", "Ms.", "Mrs.", "Miss"));

// Another overload of the 'let' method accepts a function for initializing the
// newly added default values and it supports chaining, so that you can do this
// (here using yet FluentCollection as a fluent adapter for the map's values):
m.let("colors", (k, l) -> FluentCollection.adapt(l).includeMore("red", "green", "blue"));

// Here we have:
// {titles=[Mr., Ms., Mrs., Miss], colors=[red, green, blue]}

// And what if we get somewhere a more restricted map?
final FluentMap<String, ? extends Collection<String>> n = m;

// Still we can add values, but just using the allowed defaults:
n.let("sizes").addAll(Arrays.asList("XXS", "XS", "S", "M", "L", "XL", "XXL"));
n.let("state", (k, l) -> l.add("good"));
```


### Making collection snapshots ###

It is quite common to see code like `Collections.unmodifiableSet(new HashSet<>(source))` to make a copy of a collection, so that it could be stored for an object's needs and perhaps even passed elsewhere, avoiding the danger of unwanted modification. Great, but what if the source is empty? This code snippet makes always a couple of instances: the value holder and the unmodifiable wrapper, although for empty collections a singleton is available. So, `Snapshots.collection(source)` can be used instead; this method and its friends for other collections return an unmodifiable copy of the source, but take care not to create unnecessary instances or wrappers.


### Byte arrays? Why? ###

Arrays are mutable. This becomes a nightmare when an array type appears as a parameter (or a part of). Defensive copying is expensive and often not possible (e.g., when the array is a part of another type that you can't control fully). There are collections for objects, but primitive types keep causing the pain.

Here comes the `ByteSequence`. It can not only carry any binary data, but provides several ways to read them besides its own interface, which is similar to `CharSequence`, hence you can use `byte[]`, `ByteBuffer`, `IntStream` or a mixture of `InputStream` and `ReadableByteChannel`. As data sources, `byte[]` and `ByteBuffer` could be used besides a builder that provides a comfortable mixture of `OutputStream` and `WritableByteChannel`. Enjoy well-defined `equals` too!


### Making contracts more flexible ###

Having too few contract conditions for an interface is bad: clients of the interface don't know what might happen and implementations must be more cautious as well and perhaps employ defensive copying and other measures to avoid legal, although not very clean behavior of their callers. Having too many contract conditions brings problems as well: it could restrict implementations too much, so that some desired functionality can't be implemented with sufficient efficiency or at all.

Here comes `Extensible`: a mixin interface that allows an implementation to declare some extensions of its contract that are optional or not defined by the basic contract. Extension-aware clients may then leverage the extension to employ a more efficient use of the interface. Note that marker interfaces, like `RandomAccess`, could be used as well, but they have several disadvantages – for instance, the extension support can't vary and depends on a particular type, which is especially tricky when using wrappers. The best part comes at the end: you can use `Extensions` with any object. Here is an example:

```{java}
final Consumer<byte[]> operation = …;

// The contract does not prevent the operand from being modified. A careful 
// caller therefore must pass a copy of the data that may not be modified. But
// if the implementation declares, with an extension, that it does not modify
// the operand, the defensive copying can be avoided. A single expression can
// deal basically with both cases, choosing the appropriate variant:

if (Extensions.of(operation).notPresent(OperationExtension.SAFE, () -> operation.accept(data))) {
    operation.accept(data.clone());
});
```


### And there is more… ###

Have a look, feel free to discover your ways to use that, and let us know!


## Prerequisites ##

For building this project is needed:

* JDK 8 or newer.
* Maven 3.3 or newer.

For using the built library is needed:

* JRE 8 or newer.


## Acknowledgments ##

A special thanks belongs to [Atos](http://atos.net/). The development of this library would be much slower without their support which provided a great opportunity to verify the library practically and improve it according to the experience.


## Licensing ##

The project is licensed under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0). For previous versions of this repository the original or current license can be chosen, i.e., the current license applies as an option for all previously published content.

Contributions to the project are welcome and accepted if they can be incorporated without the need of changing the license or license conditions and terms.


[![Yetamine logo](http://petr.dolezal.matfyz.cz/files/Yetamine_small.svg "Our logo")](http://petr.dolezal.matfyz.cz/files/Yetamine_large.svg)
