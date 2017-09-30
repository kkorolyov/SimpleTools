package dev.kkorolyov.simplefiles

import dev.kkorolyov.simplefiles.stream.InStrategy
import dev.kkorolyov.simplefiles.stream.OutStrategy

import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Consumer

class FilesTest extends Specification {
	@Shared String path = "something.something"
	@Shared String badStrategyMessage = "stuff"
	@Shared byte[] bytes = new byte[64]

	InputStream inStream = Mock()
	OutputStream outStream = Mock()
	Consumer<InputStream> inConsumer = Mock()
	Consumer<OutputStream> outConsumer = Mock()

	def setupSpec() {
		new Random().nextBytes(bytes)
	}
	def cleanup() {
		Files.delete(path)
	}

	def "in() does not fail fast if failFast false"() {
		expect:
		Files.in(path, false, inStrategies(inStream)) == inStream
	}
	def "in() fails fast if failFast true"() {
		when:
		Files.in(path, true, inStrategies(inStream))

		then:
		AccessException e = thrown()
		e.getMessage() == badStrategyMessage
	}

	def "in() returns first non-null strategy return value"() {
		expect:
		Files.in(path, inStrategies(inStream)) == inStream
	}
	def "in() fails if null input stream returned"() {
		when:
		Files.in(path, inStrategies(null))

		then:
		thrown AccessException
	}

	def "in() consumer invoked if stream available"() {
		when:
		boolean result = Files.in(path, inConsumer, inStrategies(inStream))

		then:
		result
		1 * inConsumer.accept(inStream)
	}
	def "in() consumer not invoked if stream unavailable"() {
		when:
		boolean result = Files.in(path, inConsumer, inStrategies(null))

		then:
		!result
		0 * inConsumer.accept(_)
	}

	def "out() does not fail fast if failFast false"() {
		expect:
		Files.out(path, false, outStrategies(outStream)) == outStream
	}
	def "out() fails fast if failFast true"() {
		when:
		Files.out(path, true, outStrategies(outStream))

		then:
		AccessException e = thrown()
		e.getMessage() == badStrategyMessage
	}

	def "out() returns first non-null strategy return value"() {
		expect:
		Files.out(path, outStrategies(outStream)) == outStream
	}
	def "out() fails if null input stream returned"() {
		when:
		Files.out(path, outStrategies(null))

		then:
		thrown AccessException
	}

	def "out() consumer invoked if stream available"() {
		when:
		boolean result = Files.out(path, outConsumer, outStrategies(outStream))

		then:
		result
		1 * outConsumer.accept(outStream)
	}
	def "out() consumer not invoked if stream unavailable"() {
		when:
		boolean result = Files.out(path, outConsumer, outStrategies(null))

		then:
		!result
		0 * outConsumer.accept(_)
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

	/** @return 2 InStrategies, where the 1st throws an {@code AccessException}, and 2nd returns {@code stream} */
	private InStrategy[] inStrategies(InputStream stream) {
		return [
				Mock(InStrategy) {
					apply(path) >> { throw new AccessException(badStrategyMessage) }
				},
				Mock(InStrategy) {
					apply(path) >> stream
				}
		]
	}
	/** @return 2 OutStrategies, where the 1st throws an {@code AccessException}, and 2nd returns {@code stream} */
	private OutStrategy[] outStrategies(OutputStream stream) {
		return [
				Mock(OutStrategy) {
					apply(path) >> { throw new AccessException(badStrategyMessage) }
				},
				Mock(OutStrategy) {
					apply(path) >> stream
				}
		]
	}
}
