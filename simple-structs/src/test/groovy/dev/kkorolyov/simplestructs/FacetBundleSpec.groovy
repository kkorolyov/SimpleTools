package dev.kkorolyov.simplestructs

import spock.lang.Specification

import static dev.kkorolyov.simplespecs.SpecUtilities.randInt
import static dev.kkorolyov.simplespecs.SpecUtilities.randString
import static java.util.stream.Collectors.toSet

class FacetBundleSpec extends Specification {
	int index = randInt(2000)
	Object element = Mock()

	FacetBundle<String, Object> bundle = new FacetBundle<>()

	def "contains entry if added"() {
		when:
		bundle.put(index, element)

		then:
		bundle.contains(index)
	}
	def "contains nothing at unset index"() {
		when:
		bundle.put(0, element)
		bundle.put(2, element)

		then:
		!bundle.contains(1)
	}
	def "contains nothing at out-of-bounds index"() {
		when:
		bundle.put(3, element)

		then:
		!bundle.contains(30)
	}

	def "gets entry at index"() {
		when:
		bundle.put(index, element)

		then:
		element == bundle.get(index).element
	}
	def "gets null at unset index"() {
		when:
		bundle.put(0, element)
		bundle.put(2, element)

		then:
		bundle.get(1) == null
	}
	def "gets null at out-of-bounds index"() {
		when:
		bundle.put(3, element)

		then:
		bundle.get(30) == null
	}

	def "gets facet intersection"() {
		String facet = randString()
		int[] faceted = (0..4)
		int[] other = (5..50)

		when:
		faceted.each {
			bundle.put(it, it)
					.addFacets(facet)
		}
		other.each {
			bundle.put(it, it)
					.addFacets(randString())
		}

		then:
		bundle.get([facet]).collect(toSet()) == faceted as Set
	}

	def "removes element at index"() {
		when:
		bundle.put(index, element)

		then:
		bundle.remove(index)
		!bundle.contains(index)
	}
	def "removes nothing at unset index"() {
		when:
		bundle.put(0, element)
		bundle.put(2, element)

		then:
		!bundle.remove(1)
	}
	def "removes nothing at out-of-bounds index"() {
		when:
		bundle.put(3, element)

		then:
		!bundle.remove(30)
	}
}
