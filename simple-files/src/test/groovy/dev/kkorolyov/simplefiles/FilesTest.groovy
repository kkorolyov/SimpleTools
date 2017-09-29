package dev.kkorolyov.simplefiles

import dev.kkorolyov.simplefiles.stream.StreamStrategy

import spock.lang.Shared
import spock.lang.Specification

class FilesTest extends Specification {
	@Shared String path = "something.something"
	@Shared String badStrategyMessage = "stuff"
	@Shared byte[] bytes = new byte[64]
	StreamStrategy goodStrategy = Mock()
	StreamStrategy badStrategy = Mock()

	def setupSpec() {
		new Random().nextBytes(bytes)
	}
	def cleanup() {
		Files.delete(path)
	}

	def "stream() does not fail fast if failFast false"() {
		when:
		Files.stream(path, false, badStrategy, goodStrategy)

		then:
		1 * badStrategy.open(path) >> { throw new AccessException(badStrategyMessage) }
		1 * goodStrategy.open(path)

		thrown AccessException
	}
	def "stream() fails fast if failFast true"() {
		when:
		Files.stream(path, true, badStrategy, goodStrategy)

		then:
		1 * badStrategy.open(path) >> { throw new AccessException(badStrategyMessage) }
		0 * goodStrategy.open(_ as String)

		AccessException e = thrown()
		e.getMessage() == badStrategyMessage
	}

	def "stream() returns first non-null strategy return value"() {
		InputStream mockStream = Mock(InputStream)

		when:
		InputStream result = Files.stream(path, badStrategy, goodStrategy)

		then:
		1 * badStrategy.open(path)
		1 * goodStrategy.open(path) >> { return mockStream }

		notThrown AccessException

		result == mockStream
	}
	def "stream() fails if null input stream returned"() {
		when:
		Files.stream(path, goodStrategy)

		then:
		1 * goodStrategy.open(path)

		thrown AccessException
	}

	def "reads/writes file"() {
		String content = "some stuff to write"

		when:
		BufferedWriter out = Files.write(path)
		out.write(content)
		out.close()

		BufferedReader input = Files.read(path)
		String result = input.readLine()
		input.close()

		then:
		result == content
	}

	def "reads/writes bytes"() {
		when:
		Files.bytes(path, bytes)

		byte[] result = Files.bytes(path)

		then:
		result == bytes
	}
}
