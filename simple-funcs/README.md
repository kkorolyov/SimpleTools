[![Download][latest-img]][latest]

# SimpleFuncs
Provides utilities for functional programming paradigms such as first-class functions, streams, and (preferably oneline) immutable operations.

## Components
### Throwing Functional Interfaces
Extensions of existing Java 8 functional interfaces with added support for checked exceptions.
```java
ThrowingRunnable<Exception> throwingRunnable = () -> throw new Exception();
throwingRunnable.run();	// Re-throws checked exception as a RuntimeException
```

### Converters
Library for generating functions converting instances between types.
```java
Converter<Object, String> stringifier = String::valueOf;
Collection<String> converted = stringifier.convert(Arrays.asList(1, 5, 7));
```
#### Generator Functions
- `Converter#selective(Predicate, Converter)` generates a converter which converts input matching a given test
- `Converter#reducing(Converter<?, Optional>...)` generates a converter which uses the first-accepting selective converter

### Collectors
Additional (potentially) useful collectors not found in the standard library.
```java
Collectors.joiningDefaultEmpty("delimeter", "prefix", "suffix");
Collectors.keyedOn(KeyableThing::getKey);
```

### Iterables
Utilities for generating and working with `Iterable`s directly.
```java
Iterable<T> part1 = ...;
Iterable<T> part2 = ...;
...
Iterable<T> partN = ...;

Iterable<T> full = Iterables.concat(part1, part2, ..., partN)	// Iterates over elements of all parts in order
```
```java
Iterable<String> initial = Arrays.asList("A", "B");
Iterable<String> full = Iterables.concat(initial, "C", "D");	// Iterates over all in 'initial', then 'C' and 'D'
```

### Predicates
Additional test methods for filtering stream elements.
```java
Predicates.isNullOrEmpty("SomeString");
```

[latest]: https://bintray.com/kkorolyov/java/simple-funcs/_latestVersion
[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-funcs/images/download.svg
