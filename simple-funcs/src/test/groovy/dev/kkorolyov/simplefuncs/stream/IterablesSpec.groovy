package dev.kkorolyov.simplefuncs.stream

import spock.lang.Specification

class IterablesSpec extends Specification {
	def "matches if same ordered equals"() {
		when:
		List<String> iterable = ["A", "B", "C"]
		List<String> other = new ArrayList<>(iterable)

		then:
		Iterables.matches(iterable, other)
	}
	def "not matches if different ordered equals"() {
		when:
		List<String> iterable = ["A", "B", "C"]
		List<String> other = ["A", "C", "B"]

		then:
		!Iterables.matches(iterable, other)
	}

	def "iterates concats in order"() {
		Iterable<String> part = ["A", "B"]
		Iterable<String> part1 = ["Z", "nope"]

		when:
		List<String> result = []
		Iterables.concat(part, part1).each { result.add(it) }

		then:
		result == (part + part1)
	}

	def "iterates initial then appended"() {
		Iterable<String> initial = ["A", "B"]
		String append = "C"
		String append1 = "oops"

		when:
		List<String> result = []
		Iterables.append(initial, append, append1).each { result.add(it) }

		then:
		result == (initial + append + append1)
	}
}
