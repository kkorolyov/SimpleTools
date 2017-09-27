[![Download](https://api.bintray.com/packages/kkorolyov/java/simple-logs/images/download.svg) ](https://bintray.com/kkorolyov/java/simple-logs/_latestVersion)

# SimpleLogs
(Yet another) logging library for logging system messages to various outputs with minimal effort.

## Examples
```java
Path logFile = Paths.get("log.log");
Logger logger = Logger.getLogger(Level.INFO, Formatters.simple(), Appenders.file(logFile, Level.INFO));

logger.info("Some INFO message");
logger.exception(new Exception("Some SEVERE exception");
```

## Installation
* Download the [latest release](https://github.com/kkorolyov/SimpleLogs/releases/latest).
* Add either the source or bundled .jar file to your project's classpath.

## Overview
SimpleLogs is composed of 3 distinct working units: `Loggers`, `Formatters`, and `Appenders`.

### Logger
This is the main, externally-facing unit responsible for accepting work in the form of log messages.

Loggers are organized in a hierarchy according to a `.`-delimited name.
* The empty string `""` corresponds to the absolute root logger.
* A logger is a child of another logger `"PARENT"` if its name follows the format `"PARENT.blah"`.
* Child loggers propagate messages to all parents' appenders.

### Formatter
This makes logged messages look good.

### Appender
This applies formatted messages to some stream, file, or other output source.

## Basic Usage
Retrieve a logger by calling one of the static `Logger.getLogger(...)` methods.
```java
// For the lazy - default level of INFO, simple formatter, and appender to System.err
Logger logger = Logger.getLogger();	// Uses the full name of the calling class as its name
Logger logger = Logger.getLogger("custom.logger");

// For the picky - all the fun stuff is custom-set
Logger logger = Logger.getLogger(Level.DEBUG, Formatters.simple(), Appenders.out());	// Uses the full name of the calling class as its name
Logger logger = Logger.getLogger("logger.never", Level.SEVERE, new Formatter() {...}, new Appender(Level.WARNING) {...});
```
Log a message by specifying the message `level`, a `message string`, and optional `args`.
A message is logged only if its level is `<=` the logging logger's level.
Args are resolved to their string representations only if and when the message is logged, and replace `"{}"` markers in the message string.
```java
logger.log(Level.INFO, "My object's toString={}", myObj);	// Resolves to "My object's toString=THIS_IS_MY_OBJ"

// Shortcuts provided for all standard levels
logger.severe("Some bad stuff happened");
logger.warning("This is a warning");
logger.info("Min int={}, max int={}", Integer.MIN_VALUE, Integer.MAX_VALUE);
logger.debug("Resolved a crazy-long computation to: {}", (Supplier) () -> "A" + "B");	// Suppliers are args too!
```
Log the stack trace of an exception.
```java
logger.exception(243, new Exception("Some exception"));		// Log at a custom level
logger.exception(new Exception("Some SEVERE exception");	// Defaults to SEVERE level
```
If [SimpleProps](https://github.com/kkorolyov/SimpleProps) is on the classpath, `Logger.applyProps(Path propsPath)` can be invoked to configure loggers via a properties file.
Each property in this file is defined as:

`LOGGER=LEVEL, WRITERS...`
* `LOGGER` - name of a logger
* `LEVEL` - the logger's logging level
* `WRITERS` - list of comma-delimited files or streams the logger logs to
	* OUT - `System.out` stream
	* ERR - `System.err` stream

Further documentation found in the [Javadoc](https://kkorolyov.github.io/SimpleLogs).

## License
[BSD-new license](LICENSE).  
