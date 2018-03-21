package dev.kkorolyov.simplefuncs.stream

import spock.lang.Specification

class IterablesSpec extends Specification {
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
