package dev.kkorolyov.simplefuncs.function

import spock.lang.Specification

import java.util.concurrent.ThreadLocalRandom
import java.util.function.BiFunction
import java.util.function.BiPredicate
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

import static dev.kkorolyov.simplefuncs.function.Memoizer.memoize

class MemoizerSpec extends Specification {
	Object arg = Mock()
	Object arg1 = Mock()

	Supplier<Object> supplier = Mock()
	Supplier<Object> supplierMemo = memoize(supplier)

	Predicate<Object> predicate = Mock()
	Predicate<Object> predicateMemo = memoize(predicate)

	BiPredicate<Object, Object> biPredicate = Mock()
	BiPredicate<Object, Object> biPredicateMemo = memoize(biPredicate)

	Function<Object, Object> function = Mock()
	Function<Object, Object> functionMemo = memoize(function)

	BiFunction<Object, Object, Object> biFunction = Mock()
	BiFunction<Object, Object, Object> biFunctionMemo = memoize(biFunction)

	def "memoizes supplier"() {
		def result = Mock(Object)

		when:
		def memoResults = (0..10).collect { supplierMemo.get() }

		then:
		memoResults.every { it == result }

		1 * supplier.get() >> result
	}

	def "memoizes predicate"() {
		def ( result, result1 ) = (0..1).collect { ThreadLocalRandom.current().nextBoolean() }

		when:
		def memoResults = (0..10).collect { predicateMemo.test(arg) }
		def memoResults1 = (0..10).collect { predicateMemo.test(arg1) }

		then:
		memoResults.every { it == result }
		memoResults1.every { it == result1 }

		1 * predicate.test(arg) >> result
		1 * predicate.test(arg1) >> result1
	}
	def "memoizes bi-predicate"() {
		def ( result, result1, result2, result3 ) = (0..3).collect { ThreadLocalRandom.current().nextBoolean() }

		when:
		def memoResults = (0..10).collect { biPredicateMemo.test(arg, arg) }
		def memoResults1 = (0..10).collect { biPredicateMemo.test(arg, arg1) }
		def memoResults2 = (0..10).collect { biPredicateMemo.test(arg1, arg) }
		def memoResults3 = (0..10).collect { biPredicateMemo.test(arg1, arg1) }

		then:
		memoResults.every { it == result }
		memoResults1.every { it == result1 }
		memoResults2.every { it == result2 }
		memoResults3.every { it == result3 }

		1 * biPredicate.test(arg, arg) >> result
		1 * biPredicate.test(arg, arg1) >> result1
		1 * biPredicate.test(arg1, arg) >> result2
		1 * biPredicate.test(arg1, arg1) >> result3
	}

	def "memoizes function"() {
		def ( result, result1 ) = (0..1).collect { Mock(Object) }

		when:
		def memoResults = (0..10).collect { functionMemo.apply(arg) }
		def memoResults1 = (0..10).collect { functionMemo.apply(arg1) }

		then:
		memoResults.every { it == result }
		memoResults1.every { it == result1 }

		1 * function.apply(arg) >> result
		1 * function.apply(arg1) >> result1
	}
	def "memoizes bi-function"() {
		def ( result, result1, result2, result3 ) = (0..3).collect { Mock(Object) }

		when:
		def memoResults = (0..10).collect { biFunctionMemo.apply(arg, arg) }
		def memoResults1 = (0..10).collect { biFunctionMemo.apply(arg, arg1) }
		def memoResults2 = (0..10).collect { biFunctionMemo.apply(arg1, arg) }
		def memoResults3 = (0..10).collect { biFunctionMemo.apply(arg1, arg1) }

		then:
		memoResults.every { it == result }
		memoResults1.every { it == result1 }
		memoResults2.every { it == result2 }
		memoResults3.every { it == result3 }

		1 * biFunction.apply(arg, arg) >> result
		1 * biFunction.apply(arg, arg1) >> result1
		1 * biFunction.apply(arg1, arg) >> result2
		1 * biFunction.apply(arg1, arg1) >> result3
	}
}
