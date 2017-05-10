# Change Log

## [3.0.1] - 2017-05-10
### Fixes
* Root/empty logger is now correctly added as a parent to all other loggers


## [3.0] - 2017-05-06
### Changes
* Levels are now `ints` instead of `Enums`
* Removed `LazyMessage`, replaced with lazily-resolved log message args
* Added customizable `Formatters` and `Appenders`


## [2.3] - 2017-01-20
### Changes
* Support [SimpleProps](https://github.com/kkorolyov/SimpleProps) 4.0
* Loggers now propagate all messages to parent loggers, including those which are not logged by the logger receiving them


## [2.2] - 2017-01-12
### Changes
* Loggers can now be configured via a single properties file
* Added support for logging `LazyMessages`
  * Logged message is built only if logging would actually occur
  

## [2.1] - 2016-10-24
### Changes
* Messages are now logged _and_ propagated up the `Logger` hierarchy
  * Previously, messages were simply delegated to the root `Logger` in a hierarchy
  

## [2.0] - 2016-07-24
### Changes
* Loggers are now organized in a hierarchy.
* Loggers use `PrintWriters`to log messages.
  * Old `Printers` removed.


## [1.2.2] - 2016-07-08
### Changes
* Updated some formatting


## [1.2.1] - 2016-05-27
### Changes
* Set default printer to System.err


## [1.2] - 2016-04-09
### Changes
* Logging level can be set statically for all logger instances
* Exceptions can be logged at any level


## [1.1] - 2016-04-08
### Fixes
* Fixed Logger retrieval by name alone overwriting retrieved logger's Printer and Level fields
### Changes
* Added global Logger enabling/disabling
* Removed getLogger(String name, Printer printer) function


## [1.0] - 2016-04-02
* Initial release


[3.0.1]: https://github.com/kkorolyov/SimpleLogs/releases/tag/3.0.1
[3.0]: https://github.com/kkorolyov/SimpleLogs/releases/tag/3.0
[2.3]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v2.3
[2.2]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v2.2
[2.1]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v2.1
[2.0]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v2.0
[1.2.2]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v1.2.2
[1.2.1]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v1.2.1
[1.2]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v1.2
[1.1]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v1.1
[1.0]: https://github.com/kkorolyov/SimpleLogs/releases/tag/v1.0
