[![Download][latest-img]][latest]

# SimpleFuncs
Extensions for functional programming in Java.

## Components
### Throwing Functional Interfaces
Extensions of existing Java 8 functional interfaces with added support for checked exceptions.
- `ThrowingRunnable`
- `ThrowingSupplier`
- `ThrowingFunction`
- `ThrowingBiConsumer`
- `ThrowingBiFunction`
```java
ThrowingRunnable<Exception> throwingRunnable = () -> throw new Exception();
throwingRunnable.run()	// Re-throws checked exception as a RuntimeException
```

[latest]: https://bintray.com/kkorolyov/java/simple-funcs/_latestVersion
[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-funcs/images/download.svg
