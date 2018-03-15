package dev.kkorolyov.simplefuncs.throwing

import spock.lang.Specification

class ThrowingSupplierSpec extends Specification {
	ThrowingSupplier<?, Exception> throwingSupplier = new ThrowingSupplier<Object, Exception>() {
		@Override
		Object getThrowing() throws Exception {
			throw new Exception()
		}
	}

	def "wraps as runtime exception"() {
		when:
		throwingSupplier.get()

		then:
		thrown RuntimeException
	}
}
