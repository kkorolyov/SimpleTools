package dev.kkorolyov.simplestructs.procedure

import dev.kkorolyov.simplestructs.Graph

import spock.lang.Specification

class TopologicalSortSpec extends Specification {
	static class DfsSpec extends TopologicalSortSpec {
		Graph<Integer> graph = new Graph<>()
				.add(0, 1, 11)
				.add(2, 3)
				.add(1, 2)
				.add(11, 2)

		def "sorts topologically"() {
			expect:
			[
					[0, 1, 11, 2, 3],
					[0, 11, 1, 2, 3]
			].any { TopologicalSort.dfs(graph).execute() == it }
		}
		def "excepts if topologically-sorting cyclic graph"() {
			when:
			TopologicalSort.dfs(
					new Graph<>()
							.add(0, 0)
			).execute()

			then:
			thrown IllegalStateException
		}
	}
}
