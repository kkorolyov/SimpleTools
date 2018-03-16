[![Download][latest-img]][latest]

# SimpleProps
A simple Java API for writing and reading application properties to and from files.

## Examples
#### Reading a property:
```java
Properties props = new Properties();
props.load(Paths.get("Config.ini"));

String value = props.get("Key");
```
#### Writing and saving a property:
```java
Properties props = new Properties();

props.put("Key", "Value");
props.save(Paths.get("Config.ini"));
```
### Initializing from pre-existing properties:
```java
Properties fromFile = new Properties(Paths.get("Config.ini"));
Properties fromProperties = new Properties(fromFile);
```

## Installation
* Download the [latest release](https://github.com/kkorolyov/SimpleProps/releases/latest).
* Add either the source or bundled .jar file to your project's classpath.

## Usage
API is documented more thoroughly in the [Javadoc](https://kkorolyov.github.io/SimpleProps).
Basic usage follows the examples noted [above](#examples).

## License
BSD-new license.  
More detail found [here](LICENSE).

[latest]: https://bintray.com/kkorolyov/java/simple-props/_latestVersion
[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-props/images/download.svg
