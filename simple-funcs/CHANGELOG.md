# Change Log

## 1.4 - 2019-04-15
### Additions
* `Iterables#matches(Iterable<T>, Iterable<T>)` single-iteration matching function
* `Iterables#append(T, T...)` convenience overload

## 1.3 - 2018-06-18
### Changes
* Added more Collectors
	* `keyedOn`

## 1.2 - 2018-04-01
### Changes
* Added more ThrowingFunctions
	* `ThrowingConsumer`
	* `ThrowingPredicate`
	* `ThrowingBiPredicate`
### Fixes
* Unconstrained `Iterables#append` generics a bit

## 1.1 - 2018-03-21
### Changes
* Moved ThrowingFunctions to `simplefuncs.function` package
* Added initial
	* `Collectors`
	* `Iterables`
	* `Predicates`
