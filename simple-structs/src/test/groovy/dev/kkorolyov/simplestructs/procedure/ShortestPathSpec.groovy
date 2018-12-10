package dev.kkorolyov.simplestructs.procedure

import dev.kkorolyov.simplestructs.Graph

import spock.lang.Shared
import spock.lang.Specification

class ShortestPathSpec extends Specification {
	static class BfsSpec extends ShortestPathSpec {
		@Shared
		Graph<Integer, Void> graph = new Graph<>()
				.add(1, [2, 3])
				.add(2, [3, 5])
				.add(3, 4)
				.add(4, 5)
				.add(42)

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
		def "gets empty path on disconnected end"() {
			expect:
			ShortestPath.bfs(graph).execute(1, 42) == []
		}

		def "gets shortest path"() {
			expect:
			ShortestPath.bfs(graph).execute(1, 5) == [1, 2, 5]
		}
	}

	static class DijkstraSpec extends ShortestPathSpec {
		@Shared
		Graph<String, Integer> graph = new Graph<>()
				.add('A', 'B', 2)
				.add('A', 'C', 5)
				.add('B', 'C', 2)
				.add('Lonely')

		def "gets empty path on null start"() {
			expect:
			ShortestPath.dijkstra(graph).execute(null, 'A') == []
		}
		def "gets empty path on null end"() {
			expect:
			ShortestPath.dijkstra(graph).execute('A', null) == []
		}

		def "gets empty path on non-existent start"() {
			expect:
			ShortestPath.dijkstra(graph).execute('Z', 'C') == []
		}
		def "gets empty path on non-existent end"() {
			expect:
			ShortestPath.dijkstra(graph).execute('A', 'Z') == []
		}
		def "gets empty path on disconnected end"() {
			expect:
			ShortestPath.dijkstra(graph).execute('A', 'Lonely') == []
		}

		def "gets shortest path"() {
			expect:
			ShortestPath.dijkstra(graph).execute('A', 'C') == ['A', 'B', 'C']
		}
		def "gets unweighted shortest path"() {
			when:
			Graph<String, Integer> graph = new Graph<>()
					.add('A', 'B')
					.add('A', 'C')
					.add('B', 'C')

			then:
			ShortestPath.dijkstra(graph).execute('A', 'C') == ['A', 'C']
		}
	}
}
