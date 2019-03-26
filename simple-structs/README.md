[![Download][latest-img]][latest]

# SimpleStructs
A Java library of various data structures and algorithms.

## Graph
A `Graph` is a generically-typed collection of nodes connected to other nodes by outbound and inbound edges.

```java
Graph<String, Integer> graph = new Graph<String, Integer>()
	.add("A", "1", 1)
	.add("B", "1", 4)
	.add("A", "B");
```
results in a graph with
- node `"A"` has outbound edges to nodes (`"1"` with weight `1`), (`"B"` with no weight)
- node `"B"` has outbound edge to node (`"1"` with weight `4`) and inbound edge from node (`"A"` with no weight)
- node `"1"` has inbound edges from nodes (`"A"` with weight `1`), (`"B"` with weight `4`)

## Trie
A `Trie` provides for addition and verification of iterable elements, such as `String`s.
```java
Trie<Character> trie = new Trie<Character>
	.add(asIterable("foo"))
	.add(asIterable("foobar"))
	.add(asIterable("foofles"));

trie.contains(asIterable("fo"));	// false
trie.contains(asIterable("foob"));	// false
trie.contains(asIterable("foobar"));	// true
```

## FacetedBundle
A `FacetedBundle` provides for efficient retrieval of the intersection of elements that have a given subset of "facets" or markers applied to them,
```java
FacetedBundle<Integer, String, Object> bundle = new FacetedBundle<>();
bundle.put(0, firstObj)
	.addFacets("A", "B");
bundle.put(1, secondObj)
	.addFacets("B", "C");

Stream<Object> withAFacet = bundle.get(singleton("A"));	// [firstObject]
Stream<Object> withBFacet = bundle.get(singleton("B"));	// [firstObj, secondObj]
Stream<Object> withCFacet = bundle.get(singleton("C"));	// [secondObj]
```

## WeightedDistribution
A `WeightedDistribution` supports weighted randomized selection of elements.
```java
WeightedDistribution<String> distribution = new WeightedDistribution<String>()
	.add("A", 3)
	.add("B", 1);

String randomValue = distribution.get(); // 3/4 chance of "A", 1/4 chance of "B"
```

[latest]: https://bintray.com/kkorolyov/java/simple-structs/_latestVersion
[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-structs/images/download.svg
