# SimpleLogs
A simple API for logging various application messages to various outputs with minimal effort.

## Examples
#### Creating a logger for a class:
```java
Logger log = Logger.getLogger(MyClass.class.getName());
```
```java
Logger log = Logger.getLogger(MyClass.class.getName(), new FilePrinter("error.log"), Level.SEVERE);
```
#### Logging a basic message
```java
log.severe("Some SEVERE message");
log.warning("Some WARNING message");
log.info("Some INFO message");
log.debug("Some DEBUG message");
```
## Installation
* Download the [latest release](https://github.com/kkorolyov/SimpleLogs/releases/latest).
* Add either the source or bundled .jar file to your project's classpath.

## Usage
Basic usage follows the path:
* Get a logger using any one of the static `Logger.getLogger()` methods.
* Add calls such as `log.info("INFO message")` or `log.log("Message", Level.DEBUG)` throughout code.
* Invoke the static methods `Logger.setGlobalLevel(Level level)` and `Logger.setGlobalEnabled(boolean enabled)` to control logging during runtime.

## License
BSD-new license.  
More detail found [here](LICENSE).
