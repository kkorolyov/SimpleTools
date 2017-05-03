package dev.kkorolyov.simplelogs

import spock.lang.Specification

class LoggerSpec extends Specification {
	def "getLogger() uses calling class name"() {
		Logger.getLogger().severe("HI")

		expect:
		Logger.getLogger() == Logger.getLogger()
	}

	def test() {
		zero()
		one()
		two()

		expect:
		1 == 1
	}
	def zero() {
		def elements = Thread.currentThread().getStackTrace()

		println("zero")
		(0..2).each {
			println("${elements[it].className}#${elements[it].methodName}")
		}
		println()
	}
	def one() {
		println("one")
		return zero()
	}
	def two() {
		println("two")
		return one()
	}
}
