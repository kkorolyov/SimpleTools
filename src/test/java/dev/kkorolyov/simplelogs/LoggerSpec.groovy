package dev.kkorolyov.simplelogs

import dev.kkorolyov.simplelogs.append.Appender
import dev.kkorolyov.simplelogs.format.Formatter
import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Supplier

class LoggerSpec extends Specification {
	@Shared int level = Level.DEBUG
	@Shared String message = "TestMessage"
	Formatter formatter = Mock()
	Appender appender = Mock(constructorArgs: [level])

	Logger logger = Logger.getLogger("Spec", level, formatter, appender)

	def "getLogger() uses calling class name"() {
		expect:
		Invoker.logger == Logger.getLogger(Invoker.class.getName())
	}

	def "resolves calling method"() {
		when:
		Invoker.refreshLogger(level, formatter, appender)
		Invoker.log(level, message)

		then:
		1 * formatter.format(_, { it.className == Invoker.class.getName() && it.methodName == "log" }, level, message)
	}

	def "logs at specified level"() {
		when:
		logger.log(l, message)

		then:
		1 * formatter.format(_, _, l, message)
		1 * appender.append(l, _)

		where:
		l << (Byte.MIN_VALUE..Byte.MAX_VALUE)
	}

	def "appender appends message with level within threshold"() {
		when:
		formatter.format(_, _, _, message) >> message

		logger.log(l, message)

		then:
		1 * appender.append(l, message)

		where:
		l << (0..level)
	}
	def "appender ignores message with level above threshold"() {
		when:
		formatter.format(_, _, _, message) >> message

		logger.log(l, message)

		then:
		0 * appender.append(_, _)

		where:
		l << ((level + 1)..(level + Byte.MAX_VALUE))
	}

	def "resolves object toStrings"() {
		Object arg = 149

		when:
		logger.log(level, "$message {}", arg)

		then:
		1 * formatter.format(_, _, level, "$message $arg")
	}
	def "resolves suppliers"() {
		Supplier<String> supplier = { "ClosureVal" }

		when:
		logger.log(level, "$message {}", supplier)

		then:
		1 * formatter.format(_, _, level, "$message ${supplier.get()}")
	}

	def "ignores messages above current level"() {
		when:
		logger.log(logger.getLevel() + 1, message)

		then:
		0 * formatter.format(_, _, _, _)
	}
}
