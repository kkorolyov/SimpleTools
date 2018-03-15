package dev.kkorolyov.simplefuncs.throwing

import spock.lang.Specification

class ThrowingBiFunctionSpec extends Specification {
	ThrowingBiFunction<?, ?, ?, Exception> throwingBiFunction = new ThrowingBiFunction<Object, Object, Object, Exception>() {
		@Override
		Object applyThrowing(Object o, Object o2) throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingBiFunction.apply(new Object(), new Object())

		then:
		thrown RuntimeException
	}
}
