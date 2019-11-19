package dev.kkorolyov.simplefuncs.function

import spock.lang.Specification

import java.util.function.BiFunction
import java.util.function.BiPredicate
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

class MemoizerSpec extends Specification {
	Supplier<Object> supplier = Mock()

	Predicate<Object> predicate = Mock()
	BiPredicate<Object, Object> biPredicate = Mock()

	Function<Object, Object> function = Mock()
	BiFunction<Object, Object, Object> biFunction = Mock()

	def "memoizes supplier"() {
		Object result = Mock()
		Supplier<Object> memoized = Memoizer.memoize(supplier)

		when:
		Object memoResult = memoized.get()
		Object memoResultRepeat = memoized.get()

		then:
		memoResult == result
		memoResultRepeat == result

		1 * supplier.get() >> result
	}

	def "memoizes predicate"() {

	}
	def "memoized bi-predicate"() {

	}

	def "memoizes function"() {

	}
	def "memoizes bi-function"() {

	}
}
