package dev.kkorolyov.simplefiles

import dev.kkorolyov.simplefiles.stream.InStrategy
import dev.kkorolyov.simplefiles.stream.OutStrategy

import spock.lang.Shared
import spock.lang.Specification

class FilesTest extends Specification {
	@Shared String path = "something.something"
	@Shared String badStrategyMessage = "stuff"
	@Shared byte[] bytes = new byte[64]
	InStrategy goodInStrategy = Mock()
	InStrategy badInStrategy = Mock()
	OutStrategy goodOutStrategy = Mock()
	OutStrategy badOutStrategy = Mock()

	def setupSpec() {
		new Random().nextBytes(bytes)
	}
	def cleanup() {
		Files.delete(path)
	}

	def "in() does not fail fast if failFast false"() {
		when:
		Files.in(path, false, badInStrategy, goodInStrategy)

		then:
		1 * badInStrategy.apply(path) >> { throw new AccessException(badStrategyMessage) }
		1 * goodInStrategy.apply(path)

		thrown AccessException
	}
	def "in() fails fast if failFast true"() {
		when:
		Files.in(path, true, badInStrategy, goodInStrategy)

		then:
		1 * badInStrategy.apply(path) >> { throw new AccessException(badStrategyMessage) }
		0 * goodInStrategy.apply(_ as String)

		AccessException e = thrown()
		e.getMessage() == badStrategyMessage
	}

	def "in() returns first non-null strategy return value"() {
		InputStream mockStream = Mock(InputStream)

		when:
		InputStream result = Files.in(path, badInStrategy, goodInStrategy)

		then:
		1 * badInStrategy.apply(path)
		1 * goodInStrategy.apply(path) >> { return mockStream }

		notThrown AccessException

		result == mockStream
	}
	def "in() fails if null input stream returned"() {
		when:
		Files.in(path, goodInStrategy)

		then:
		1 * goodInStrategy.apply(path)

		thrown AccessException
	}

	def "out() does not fail fast if failFast false"() {
		when:
		Files.out(path, false, badOutStrategy, goodOutStrategy)

		then:
		1 * badOutStrategy.apply(path) >> { throw new AccessException(badStrategyMessage) }
		1 * goodOutStrategy.apply(path)

		thrown AccessException
	}
	def "out() fails fast if failFast true"() {
		when:
		Files.out(path, true, badOutStrategy, goodOutStrategy)

		then:
		1 * badOutStrategy.apply(path) >> { throw new AccessException(badStrategyMessage) }
		0 * goodOutStrategy.apply(_ as String)

		AccessException e = thrown()
		e.getMessage() == badStrategyMessage
	}

	def "out() returns first non-null strategy return value"() {
		OutputStream mockStream = Mock()

		when:
		OutputStream result = Files.out(path, badOutStrategy, goodOutStrategy)

		then:
		1 * badOutStrategy.apply(path)
		1 * goodOutStrategy.apply(path) >> { return mockStream }

		result == mockStream
	}
	def "out() fails if null input stream returned"() {
		when:
		Files.out(path, goodOutStrategy)

		then:
		1 * goodOutStrategy.apply(path)

		thrown AccessException
	}

	def "reads files"() {
		InputStream mockStream = Mock()

		when:
		Files.read(mockStream)
				.close()

		then:
		1 * mockStream.close()
	}
	def "writes files"() {
		OutputStream mockStream = Mock()

		when:
		Files.write(mockStream)
				.close()

		then:
		1 * mockStream.close()
	}

	def "reads bytes"() {
		InputStream mockStream = Mock()

		when:
		byte[] result = Files.bytes(mockStream)

		then:
		1 * mockStream.readAllBytes() >> bytes

		result == bytes
	}
	def "writes bytes"() {
		OutputStream mockStream = Mock()

		when:
		Files.bytes(mockStream, bytes)

		then:
		1 * mockStream.write(bytes)
	}
}
