# SimpleProps
A simple Java API for writing and reading application properties to and from files.

## Examples
#### Reading a property:
```java
File file = new File("Config.ini");
Properties props = new Properties(file);

String value1 = props.get("Key1");
```
#### Writing and saving a property:
```java
File file = new File("Config.ini");
Properties props = new Properties(file);

props.put("Key2", "Value2");
props.saveFile();
```
### Reverting to defaults:
```java
File file = new File("Config.ini");
Properties defaults = new Properties(); // No backing file
defaults.put("DefaultKey", "DefaultValue");

Properties props = new Properties(file, defaults);

props.put("DefaultKey", "ChangedValue");
props.get("DefaultKey");        // "ChangedValue"

props.loadDefaults();
props.get("DefaultKey");        // "DefaultValue"
```

## Installation
* Download the [latest release](https://github.com/kkorolyov/SimpleProps/releases/latest).
* Add either the source or bundled .jar file to your project's classpath.

## Usage
All publicly-used methods are documented in the source code.  
Basic API usage follows the examples noted [above](#examples).

## License
BSD-new license.  
More detail found [here](LICENSE).
