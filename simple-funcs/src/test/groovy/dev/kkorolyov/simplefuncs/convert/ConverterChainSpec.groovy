package dev.kkorolyov.simplefuncs.convert

import spock.lang.Shared
import spock.lang.Specification

class ConverterChainSpec extends Specification {
	@Shared Object input = "input"

	Converter<Object, Object> d = Mock()
	Converter<Object, Object> d1 = Mock()
	Converter<Object, Object> d2 = Mock()

	ConverterChain<Object, Object> chain = ConverterChain.from(input.class)
			.add(d)
			.add(d1)
			.add(d2)

	def "invokes delegates in order"() {
		when:
		chain.convert(input)

		then:
		1 * d.convert(input) >> input
		then:
		1 * d1.convert(input) >> input
		then:
		1 * d2.convert(input) >> input
	}
}
