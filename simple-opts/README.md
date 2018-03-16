[![Download][latest-img]][latest]

# SimpleOpts
A simple Java API for parsing and executing command line options.

## Examples
#### Creating an option:
```java
String  shortName = "h",
        longName = "help",
        description = "Provides help";
boolean requiresArg = false;

Option option = new Option(shortName, longName, description, requiresArg);
```
#### Creating a set of options:
```java
Option[] optionArray = <Make a bunch of options>;
Options options = new Options();

options.addAll(optionArray);
```
#### Parsing command line arguments:
```java
String[] args = <Command line arguments>;
Options validOptions = <Some options>;

ArgParser parser = new ArgParser(validOptions, args); // Parses during construction
```
#### Using parsed arguments:
```java
Option  helpOption = <Make a 'help' option>,
        addOption = <Make an 'add' option>;
ArgParser parser = <Make a parser>;

Set<Option> parsedOptions = parser.getParsedOptions();  // Returns a set of all parsed options
boolean parsedHelpOption = parser.parsedOption(helpOption); // Returns true if the specified option was parsed
String addItemArg = parser.getArg(addOption);  // Returns the argument of an option
```
## Installation
* Download the [latest release](https://github.com/kkorolyov/SimpleOpts/releases/latest).
* Add either the source or bundled .jar file to your project's classpath.

## Usage
Basic usage follows the path:
* Construct `Option` objects for all valid options.
* Bundle all created `Option` objects into an `Options` object.
* Construct an `ArgParser` object using the `Options` object and the input application `args`.
* Check parsed arguments using `ArgParser` methods such as `getParsedOptions()`, `parsedOption(Option toCheck)`, and `getArg(Option key)`.

## License
BSD-new license.  
More detail found [here](LICENSE).

[latest]: https://bintray.com/kkorolyov/java/simple-opts/_latestVersion
[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-opts/images/download.svg
