package dev.kkorolyov.simplestructs.procedure

import dev.kkorolyov.simplestructs.Graph

import spock.lang.Specification

class ShortestPathSpec extends Specification {
	static class BfsSpec extends ShortestPathSpec {
		Graph<Integer, Void> graph = new Graph<>()
			.add(1, [2, 3])
			.add(2, [3, 5])
			.add(3, 4)
			.add(4, 5)

		def "gets empty path on null start"() {
			expect:
			ShortestPath.bfs(graph).execute(null, 1) == []
		}
		def "gets empty path on null end"() {
			expect:
			ShortestPath.bfs(graph).execute(1, null) == []
		}

		def "gets empty path on non-existent start"() {
			expect:
			ShortestPath.bfs(graph).execute(0, 1) == []
		}
		def "gets empty path on non-existent end"() {
			expect:
			ShortestPath.bfs(graph).execute(1, 0) == []
		}

		def "gets shortest path"() {
			expect:
			ShortestPath.bfs(graph).execute(1, 5) == [1, 2, 5]
		}
	}
}
