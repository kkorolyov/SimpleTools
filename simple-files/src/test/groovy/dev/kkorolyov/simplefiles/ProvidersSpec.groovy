package dev.kkorolyov.simplefiles

import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Predicate

class ProvidersSpec extends Specification {
	@Shared
	Predicate<Service> predicate = { it.accepts() }

	Providers<Service> providers

	def "finds no provider if no providers"() {
		providers = Providers.fromInstances(Service, [] as Set)

		expect:
		!providers.find(predicate).isPresent()
	}
	def "finds no provider if no matches"() {
		providers = Providers.fromInstances(Service, services(false, 5))

		expect:
		!providers.find(predicate).isPresent()
	}
	def "finds provider if matches"() {
		Service matching = service(true)
		providers = Providers.fromInstances(Service, services(false, 4) << matching)

		expect:
		providers.find(predicate).get() == matching
	}

	def "find excepts if multiple matches"() {
		providers = Providers.fromInstances(Service, services(true, 2))

		when:
		providers.find(predicate)

		then:
		thrown IllegalStateException
	}

	def "findAll returns empty if no matching providers"() {
		providers = Providers.fromInstances(Service, services(false, 4))

		expect:
		providers.findAll(predicate) == [] as Set
	}
	def "finds all matching providers"() {
		Set<Service> matching = services(true, 12)
		providers = Providers.fromInstances(Service, services(false, 20) + matching)

		expect:
		providers.findAll(predicate) == matching
	}

	def "gets provider if matches"() {
		Service matching = service(true)
		providers = Providers.fromInstances(Service, services(false, 3) << matching)

		expect:
		providers.get(predicate) == matching
	}
	def "get excepts if no matches"() {
		providers = Providers.fromInstances(Service, services(false, 3))

		when:
		providers.get(predicate)

		then:
		thrown NoSuchElementException
	}

	def "returns same provider on subsequent matching calls"() {
		expect:
		(0..10).collect { Providers.fromClasses(Service, [MockService], true).get() }.toSet().size() == 1
	}
	def "returns different providers on subsequent unmatching calls"() {
		expect:
		Providers.fromClasses(Service, [MockService], true).get() != Providers.fromClasses(Service, [MockService], false).get()
	}

	private Service service(boolean accepts) {
		return new MockService(accepts)
	}
	private Set<Service> services(boolean accepts, int num) {
		return (1..num).collect { service(accepts) }
	}

	interface Service {
		boolean accepts()
	}

	static class MockService implements Service {
		private final boolean accepts;

		// FIXME Change to primitive when Providers supports primitives
		MockService(Boolean accepts) {
			this.accepts = accepts
		}

		@Override
		boolean accepts() {
			return accepts
		}
	}
}
