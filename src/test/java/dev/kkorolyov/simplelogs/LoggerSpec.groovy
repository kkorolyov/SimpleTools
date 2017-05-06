package dev.kkorolyov.simplelogs

import dev.kkorolyov.simplelogs.append.Appender
import dev.kkorolyov.simplelogs.format.Formatter
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Field
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
		1 * formatter.format(_, _, l, message) >> message
//		1 * appender.append(l, message)	 // Final methods not intercepted

		where:
		l << (-level..level)
	}
	def "ignores messages above logger level"() {
		when:
		logger.log(l, message)

		then:
		0 * formatter.format(_, _, _, _)
		0 * appender.append(_, _)

		where:
		l << ((level + 1)..(level + 100))
	}
	def "ignores messages above appender threshold"() {
		when:
		appender.setThreshold(threshold)
		logger.log(level, message)

		then:
		0 * formatter.format(_, _, _, _)
		0 * appender.append(_, _)

		where:
		threshold << ((level - 100)..(level - 1))
	}

	@Ignore // Final methods not intercepted
	def "uses parent appenders"() {
		Appender parentAppender = Mock(constructorArgs: [level])
		Appender childAppender = Mock(constructorArgs: [level])

		Logger parent = Logger.getLogger("l", level, formatter, parentAppender)
		Logger child = Logger.getLogger("l.l", level, formatter, childAppender)

		when:
		1 * formatter.format(_, _, level, message) >> message

		child.log(level, message)

		then:
		1 * parentAppender.append(level, message)
		1 * childAppender.append(level, message)
	}

	def "resolves object args"() {
		Object arg = 149

		when:
		logger.log(level, "$message {}", arg)

		then:
		1 * formatter.format(_, _, level, "$message $arg")
	}
	def "resolves supplier args"() {
		Supplier<String> supplier = { "ClosureVal" }

		when:
		logger.log(level, "$message {}", supplier)

		then:
		1 * formatter.format(_, _, level, "$message ${supplier.get()}")
	}
	def "resolves null args"() {
		when:
		logger.log(level, "$message {} {}", "notnull", null)

		then:
		1 * formatter.format(_, _, level, "$message notnull null")
	}

	@Ignore // Final methods not intercepted
	def "appender appends message with level within threshold"() {
		when:
		formatter.format(_, _, _, message) >> message

		logger.log(l, message)

		then:
		1 * appender.append(l, message)

		where:
		l << (-level..level)
	}
	@Ignore // Final methods not intercepted
	def "appender ignores message with level above threshold"() {
		Appender spyAppender = Spy(constructorArgs: [threshold])

		when:
		logger.addAppender(spyAppender)
		logger.log(level, message)

		then:
		1 * formatter.format(_, _, level, message) >> message
		1 * spyAppender.append(level, message)
		0 * spyAppender.append(_)

		where:
		threshold << ((level - 100)..(level - 1))
	}

	def "setAppenders with no arg removes appenders"() {
		when:
		logger.setAppenders()

		then:
		Field appendersField = Logger.getDeclaredField("appenders")
		appendersField.setAccessible(true)
		appendersField.get(logger).isEmpty()
	}
}
