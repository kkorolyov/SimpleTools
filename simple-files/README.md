[![Download][latest-img]][latest]

# SimpleFiles
A Java library for concisely dealing with system I/O.

## Examples
### File operations
#### Opening streams
```java
InputStream in = Files.in("path/to/file", StreamStrategies.IN_PATH);
OutputStream out = Files.out("path/to/file", StreamStrategies.OUT_PATH);
```

#### Lazily consuming streams
```java
boolean consumedIn = Files.in(in -> in.read(), "path/to/file", StreamStrategies.IN_PATH);
boolean consumedOut = Files.out(out -> out.write(new byte[]{1, 2, 3}), "path/to/file", StreamStrategies.OUT_PATH);
```

#### Obtaining readers/writers
```java
BufferedReader reader = Files.read(in);
BufferedWriter writer = Files.write(out);
```

### Service providers
#### Loading service providers
```java
Providers<MyService> providers = Providers.fromConfig(MyService.class, "someName", 42);
MyService someProvider = providers.get(p -> p.accepts(key));
```

[latest]: https://bintray.com/kkorolyov/java/simple-files/_latestVersion
[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-files/images/download.svg
