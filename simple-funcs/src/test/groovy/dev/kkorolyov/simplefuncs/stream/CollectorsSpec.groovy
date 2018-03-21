package dev.kkorolyov.simplefuncs.stream

import spock.lang.Shared
import spock.lang.Specification

import static dev.kkorolyov.simplefuncs.stream.Collectors.joiningDefaultEmpty
import static java.util.stream.Collectors.joining

class CollectorsSpec extends Specification {
	@Shared String delimiter = "-"
	@Shared String prefix = "pref"
	@Shared String suffix = "suff"

	def "joins non-empty elements as usual"() {
		Collection<String> elements = ["A", "B", "C", "oops"]

		expect:
		elements.stream().collect(joiningDefaultEmpty(delimiter, prefix, suffix)) == elements.stream().collect(joining(delimiter, prefix, suffix))
	}
	def "joins lack of elements to empty string"() {
		Collection<String> elements = []

		expect:
		elements.stream().collect(joiningDefaultEmpty(delimiter, prefix, suffix)) == ""
	}
}
