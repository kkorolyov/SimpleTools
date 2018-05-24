package dev.kkorolyov.simplefiles

import spock.lang.Shared
import spock.lang.Specification

import java.util.function.Predicate

class ProvidersSpec extends Specification {
	interface Service {
		boolean accepts()
	}
	@Shared Predicate<Service> predicate = { it.accepts() }

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
		thrown IllegalArgumentException
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

	private Service service(boolean accepts) {
		return new Service() {
			@Override
			boolean accepts() {
				return accepts
			}
		}
	}
	private Set<Service> services(boolean accepts, int num) {
		return (1..num).collect { service(accepts) }
	}
}
