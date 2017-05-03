package dev.kkorolyov.simplelogs

import spock.lang.Specification

class LoggerSpec extends Specification {
	def "getLogger() uses calling class name"() {
		Logger.getLogger().severe("HI")

		expect:
		Logger.getLogger() == Logger.getLogger()
	}
}
