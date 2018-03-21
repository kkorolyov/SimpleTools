package dev.kkorolyov.simplefuncs.function

import spock.lang.Specification

class ThrowingBiConsumerSpec extends Specification {
	ThrowingBiConsumer<?, ?, Exception> throwingBiConsumer = new ThrowingBiConsumer<Object, Object, Exception>() {
		@Override
		void acceptThrowing(Object o, Object o2) throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingBiConsumer.accept(new Object(), new Object())

		then:
		thrown RuntimeException
	}
}
