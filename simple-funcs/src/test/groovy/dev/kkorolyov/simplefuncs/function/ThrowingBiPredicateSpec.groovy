package dev.kkorolyov.simplefuncs.function

import spock.lang.Specification

class ThrowingBiPredicateSpec extends Specification {
	ThrowingBiPredicate<?, ?, Exception> throwingBiPredicate = new ThrowingBiPredicate<Object, Object, Exception>() {
		@Override
		boolean testThrowing(Object o, Object o2) throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingBiPredicate.test(new Object(), new Object())

		then:
		thrown RuntimeException
	}
}
