[![Download][latest-img]][latest]

# SimpleGraphs
A Java library of graph structures and algorithms.

## Graph
A `Graph` is a generically-typed collection of nodes connected to other nodes by outbound and inbound edges.

### Building a graph
```java
Graph<String> graph = new Graph<String>()
	.add("A", "1")
	.add("B", "1")
	.add("A", "B")
```
results in a graph with
- node `"A"` has outbound edges to nodes `"1"`, `"B"`
- node `"B"` has outbound edge to node `"1"` and inbound edge from node `"A"`
- node `"1"` has inbound edges from nodes `"A"`, `"B"`

## Operations
### Topological sort
Directed acyclic graphs may be sorted [topologically](https://en.wikipedia.org/wiki/Topological_sorting). 
```java
Graph<Integer> graph = new Graph<Integer>()
	.add(1, 3, 4)
	.add(2, 3, 4)
	.add(3, 4)
```
would result in either `[1, 2, 3, 4]` or `[2, 1, 3, 4]`

[latest]: https://bintray.com/kkorolyov/java/simple-graphs/_latestVersion
[latest-img]: https://api.bintray.com/packages/kkorolyov/java/simple-graphs/images/download.svg