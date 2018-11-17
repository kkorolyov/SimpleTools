# Change Log

## 1.0 - 2018-11-17
### Changes
* Re-branded as SimpleStructs
* `Graph<T>` is an `Iterable<Node<T>>`
* Made `Graph` methods more explicit
	* `add()` node with edges to other nodes
	* `addUndirected()` node with edges to and from other nodes
	* `remove()` nodes from the graph
	* `sever()` edges from node to other nodes
	* `severUndirected()` edges from node to and from other nodes
* Converted algorithmic operations to `Procedure`s supplied through factories
### Additions
* Shortest path procedures
### Removals
* `Graph#sortTopological()`
