package dev.kkorolyov.simplefuncs.function

import spock.lang.Specification

class ThrowingFunctionSpec extends Specification {
	ThrowingFunction<?, ?, Exception> throwingFunction = new ThrowingFunction<Object, Object, Exception>() {
		@Override
		Object applyThrowing(Object o) throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingFunction.apply(new Object())

		then:
		thrown RuntimeException
	}
}
