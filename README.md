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
* The root (topmost) logger in a hierarchy is in charge of logging all messages for its child loggers.
	* The root logger's `PrintWriters` define the output of the hierarchy's logged messages.
	* The root logger's `Level` defines the finest granularity of the hierarchy's logged messages.
		* Lower-level loggers may have coarser `Levels` than higher-level loggers, and will only propagate logged messages meeting their `Level`.
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
	* `"L1.L2"` propagates the message to its parent `"L1"`, the message level exceeds the level of `"L1"` and is ignored.
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

## License
BSD-new license.  
More detail found [here](LICENSE).
