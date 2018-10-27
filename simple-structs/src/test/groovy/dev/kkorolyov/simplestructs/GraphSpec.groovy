package dev.kkorolyov.simplestructs

import spock.lang.Specification

class GraphSpec extends Specification {
	Graph<Object> graph = new Graph<>()

	def "contains node if added"() {
		Object node = Mock()
		Object outbound = Mock()

		when:
		graph.add(node, outbound)

		then:
		graph.contains(node)
		graph.contains(outbound)
	}
	def "does not contain node if not added"() {
		expect:
		!graph.contains("")
	}

	def "does not contain node if all edges removed"() {
		Object node = Mock()
		Object outbound = Mock()

		when:
		graph.add(node, outbound)
		graph.remove(node, outbound)

		then:
		!graph.contains(node)
		!graph.contains(outbound)
	}
	def "contains node if not all edges removed"() {
		Object node = Mock()
		Object outbound = Mock()

		when:
		graph.add(node, outbound)
		graph.remove(node)

		then:
		graph.contains(node)
		graph.contains(outbound)
	}

	def "sorts topologically"() {
		Object node = Mock()
		Object node1 = Mock()
		Object node11 = Mock()
		Object node2 = Mock()
		Object node3 = Mock()

		when:
		graph.add(node, node1, node11)
		graph.add(node2, node3)
		graph.add(node1, node2)
		graph.add(node11, node2)
		List<Object> result = graph.sortTopological()

		then:
		result == [node, node1, node11, node2, node3] ||
				result == [node, node11, node1, node2, node3]
	}
	def "excepts if topologically-sorting cyclic graph"() {
		Object node = Mock()

		when:
		graph.add(node, node)
		graph.sortTopological()

		then:
		thrown IllegalStateException
	}
}
