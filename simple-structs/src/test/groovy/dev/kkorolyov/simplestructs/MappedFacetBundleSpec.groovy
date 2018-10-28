package dev.kkorolyov.simplestructs

import spock.lang.Specification

import static dev.kkorolyov.simplespecs.SpecUtilities.randInt
import static dev.kkorolyov.simplespecs.SpecUtilities.randString
import static java.util.stream.Collectors.toSet

class MappedFacetBundleSpec extends Specification {
	String key = randString()
	Object element = Mock()

	FacetBundle.Mapped<String, Integer, Object> bundle = new FacetBundle.Mapped<String, Integer, Object>()

	def "contains entry if added"() {
		when:
		bundle.put(key, element)

		then:
		bundle.contains(key)
	}
	def "does not contain entry if not added"() {
		expect:
		!bundle.contains(key)
	}

	def "gets entry at key"() {
		when:
		bundle.put(key, element)

		then:
		element == bundle.get(key).element
	}
	def "gets null at unset key"() {
		expect:
		bundle.get(key) == null
	}

	def "gets facet intersection"() {
		int facet = randInt()
		String[] faceted = (0..4).collect { randString() }
		String[] other = (5..50).collect { randString() }

		when:
		faceted.each {
			bundle.put(it, it)
					.addFacets(facet)
		}
		other.each {
			bundle.put(it, it)
					.addFacets(randInt())
		}

		then:
		bundle.get([facet]).collect(toSet()) == faceted as Set
	}

	def "removes element at key"() {
		when:
		bundle.put(key, element)

		then:
		bundle.remove(key)
		!bundle.contains(key)
	}
	def "removes nothing at unset key"() {
		expect:
		!bundle.remove(key)
	}
}
