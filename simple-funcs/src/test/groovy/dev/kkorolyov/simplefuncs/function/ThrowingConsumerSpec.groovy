package dev.kkorolyov.simplefuncs.function

import spock.lang.Specification

class ThrowingConsumerSpec extends Specification {
	ThrowingConsumer<?, Exception> throwingConsumer = new ThrowingConsumer<Object, Exception>() {
		@Override
		void acceptThrowing(Object o) throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingConsumer.accept(new Object())

		then:
		thrown RuntimeException
	}
}
