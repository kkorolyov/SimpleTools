# SimpleProps
A simple API for writing and reading application properties to and from files.

## Examples
#### Reading a property:
```java
Properties props = Properties.getInstance("Config.ini");
String value1 = props.getValue("Key1");
```
#### Writing a property:
```java
String  newKey = "Key2",
        newValue = "Value2";
        
Properties props = Properties.getInstance("Config.ini");

props.addProperty(newKey, newValue);
props.saveToFile();
```

## Installation
* Download the [latest release](https://github.com/kkorolyov/SimpleProps/releases/latest).
* Add either the source or bundled .jar file to your project's classpath.

## Usage
All publicly-used methods are documented in the source code.  
Basic API usage follows the examples noted [above](#examples).

## License
BSD-new license.  
More detail found [here](license).
