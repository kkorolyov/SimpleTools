package dev.kkorolyov.simplestructs

import spock.lang.Specification

class GraphSpec extends Specification {
	Graph<Object, Void> graph = new Graph<>()

	def "does not contain non-existent node"() {
		expect:
		!graph.contains("")
	}
	def "adds node"() {
		when:
		graph.add(0)

		then:
		graph.contains(0)
		graph.getOutbounds(0) == [] as Set
		graph.getInbounds(0) == [] as Set
	}

	def "removes node"() {
		graph.addUndirected(0, 1)

		when:
		graph.remove(0)

		then:
		!graph.contains(0)
		graph.contains(1)

		graph.getOutbounds(1) == [] as Set
		graph.getInbounds(1) == [] as Set
	}

	def "adds directed edge"() {
		when:
		graph.add(0, 1)

		then:
		graph.isConnected(0)
		graph.outDegree(0) == 1
		graph.inDegree(0) == 0
		graph.getOutbounds(0) == [1] as Set
		graph.getInbounds(0) == [] as Set

		graph.isConnected(1)
		graph.outDegree(1) == 0
		graph.inDegree(1) == 1
		graph.getOutbounds(1) == [] as Set
		graph.getInbounds(1) == [0] as Set
	}
	def "adds undirected edge"() {
		when:
		graph.addUndirected(0, 1)

		then:
		graph.isConnected(0)
		graph.outDegree(0) == 1
		graph.inDegree(0) == 1
		graph.getOutbounds(0) == [1] as Set
		graph.getInbounds(0) == [1] as Set

		graph.isConnected(1)
		graph.outDegree(1) == 1
		graph.inDegree(1) == 1
		graph.getOutbounds(1) == [0] as Set
		graph.getInbounds(1) == [0] as Set
	}

	def "severs directed edge"() {
		graph.addUndirected(0, 1)

		when:
		graph.sever(0, 1)

		then:
		graph.isConnected(0)
		graph.outDegree(0) == 0
		graph.inDegree(0) == 1
		graph.getOutbounds(0) == [] as Set
		graph.getInbounds(0) == [1] as Set

		graph.isConnected(0)
		graph.outDegree(1) == 1
		graph.inDegree(1) == 0
		graph.getOutbounds(1) == [0] as Set
		graph.getInbounds(1) == [] as Set
	}
	def "severs undirected edge"() {
		graph.addUndirected(0, 1)

		when:
		graph.severUndirected(0, 1)

		then:
		!graph.isConnected(0)
		graph.outDegree(0) == 0
		graph.inDegree(0) == 0
		graph.getOutbounds(0) == [] as Set
		graph.getInbounds(0) == [] as Set

		!graph.isConnected(1)
		graph.outDegree(1) == 0
		graph.inDegree(1) == 0
		graph.getOutbounds(1) == [] as Set
		graph.getInbounds(1) == [] as Set
	}
}
