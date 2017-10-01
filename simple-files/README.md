[![Download][latest-img]][latest]

# SimpleFiles
A Java library for concisely dealing with system I/O.

## Examples
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

## Installation
* Download the [latest release][latest].
* Add either the source or bundled .jar file to your project's classpath.

## Usage
API is documented more thoroughly in the [Javadoc](https://kkorolyov.github.io/SimpleTools).
Basic usage follows the examples noted [above](#examples).

## License
BSD-new license.  
More detail found [here](LICENSE).

[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-files/images/download.svg
[latest]: https://bintray.com/kkorolyov/java/simple-files/_latestVersion
