# Change Log

## 1.3.1 - 2019-11-28
### Changes
* `Providers<T>` now extends `Iterable<T>`
### Fixes
* `Providers` now loads the same instance for multiple provided services if such an instance exists

## 1.3 - 2019-11-25
### Additions
* `Providers#find`, `Providers#get` no-arg convenience methods

## 1.2 - 2019-05-12
### Additions
* `Providers#fromDescriptor` method to load service providers from module descriptors

## 1.1.1 - 2018-05-24
### Changes
* Added additional `Providers` methods
	* `findAll(Predicate<T>): Collection<T>`
	* `stream(): Stream<T>`

## 1.1 - 2018-05-24
### Changes
* Added `Providers`
