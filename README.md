# SimpleLogs
A simple Java API for logging various application messages to various outputs with minimal effort.

## Examples
```java
Logger logger = Logger.getLogger(MyClass.class.getName(), Level.INFO, new PrintWriter("error.log");
logger.info("Some INFO message");
logger.exception(new Exception("Some SEVERE exception");
```

## Installation
* Download the [latest release](https://github.com/kkorolyov/SimpleLogs/releases/latest).
* Add either the source or bundled .jar file to your project's classpath.

## Usage
### Logger Hierarchy
Loggers are organized in a hierarchy according to name.
* Each level in the hierarchy is defined as a unique set of characters (segment) delimeted by the `.` character.
* A logger is defined as a parent of another logger when its entire name consists of segments matching the initial segments of the child logger's name.
* Messages logged by child loggers propagate up the hierarchy until they reach either the last logger or a logger with a level less than the message.
* Logger hierarchies are populated dynamically when new loggers are created.
* An empty name (`""`) corresponds to the absolute root logger.

#### Example
* `Logger.getLogger("L1.L2")` is called.
	* `"L1.L2"` has no parent.
* `Logger.getLogger("L1")` is called.
	* `"L1"` has no parent, `"L1.L2"` has `"L1"` as its parent.
* `Logger.getLogger("L1.L2").setLevel(Level.DEBUG)` is called.
* `Logger.getLogger("L1").setLevel(Level.INFO)` is called.
* `Logger.getLogger("L1.L2").debug("DEBUG message")` is called.
	* `"L1.L2"` logs and propagates the message to its parent `"L1"`, the message level exceeds the level of `"L1"` and is ignored by `"L1"`.
* `Logger.getLogger("L1.L2").setLevel(Level.INFO)` is called.
* `Logger.getLogger("L1").setLevel(Level.DEBUG)` is called.
* `Logger.getLogger("L1.L2").debug("DEBUG message")` is called.
	* The message level exceeds the level of `"L1.L2"` and is not propagated to parent `"L1"`, even though `"L1"` has a level of `DEBUG`.  

### Retrieving a Logger
Retrieve a logger by calling one of the static `Logger.getLogger()` methods.
```java
	Logger logger = Logger.getLogger(String name);
	Logger logger = Logger.getLogger(String name, Level level, PrintWriter... writers);
```

### Logging a Message
A logged message consists of a `String` and a logging `Level`.
```java
logger.log("Some message", Level.INFO);	// Message is logged if the specified level is less than or equal to the logger's level
logger.severe("Some SEVERE message");	// Same as logger.log("Some message", Level.SEVERE)
logger.warning("Some WARNING message");	// Same as logger.log("Some message", Level.WARNING)
logger.info("Some INFO message");		// Same as logger.log("Some message", Level.INFO)
logger.debug("Some DEBUG message");		// Same as logger.log("Some message", Level.DEBUG)
```

### Logging an Exception
A logged exception consists of an `Exception` and a logging `Level`.
```java
logger.exception(new Exception("Some exception"), Level.WARNING);	// Exception is logged if the specified level is less than or equal to the logger's level
logger.exception(new Exception("Some SEVERE exception");			// Same as logger.exception(new Exception("Some exception"), Level.SEVERE)
```

### Enabling and Disabling a Logger
Enabled loggers log both messages and exceptions at or below their level.  
Disabled loggers log nothing.  
All new loggers are initially enabled.
```java
logger.setLevel(Level.INFO);
logger.setEnabled(true);
logger.info("Logged message");		// Message is logged
logger.setEnabled(false);
logger.info("Not logged message");	// Message is ignored
```

### Configuring Loggers via properties file
If [SimpleProps](https://github.com/kkorolyov/SimpleProps) is in the class path, `Logger.applyProps(File logProps)` can be invoked to configure loggers via a properties file.
Each property in this file is defined as:

`LOGGER=LEVEL, WRITERS...`
* `LOGGER` - name of a logger
* `LEVEL` - the logger's logging level
* `WRITERS` - list of comma-delimited files or streams the logger logs to
	* OUT - `System.out` stream
	* ERR - `System.err` stream

## License
BSD-new license.  
More detail found [here](LICENSE).
