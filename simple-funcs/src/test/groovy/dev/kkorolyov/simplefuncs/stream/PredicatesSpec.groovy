package dev.kkorolyov.simplefuncs.stream

import spock.lang.Specification

class PredicatesSpec extends Specification {
	def "test empty char sequences"() {
		expect:
		Predicates.isEmpty(value)
		!Predicates.nonEmpty(value)

		where:
		value << [null, ""]

	}
	def "test non-null non-empty char sequences"() {
		expect:
		Predicates.nonEmpty(value)
		!Predicates.isEmpty(value)

		where:
		value << [" ", "Yup"]
	}
}
