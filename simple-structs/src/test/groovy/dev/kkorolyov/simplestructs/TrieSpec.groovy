package dev.kkorolyov.simplestructs

import spock.lang.Specification

class TrieSpec extends Specification {
	Trie<Character> trie = new Trie<>();

	def "does not contain non-existent value"() {
		expect:
		!trie.contains('nope' as Character[])
	}
	def "does not contain non-terminal value"() {
		Character[] value = 'value'
		Character[] partial = 'val'

		when:
		trie.add(value)

		then:
		!trie.contains(partial)
		!trie.get(partial).terminal
	}
	def "contains terminal value"() {
		Character[] value = 'value'

		when:
		trie.add(value)

		then:
		trie.contains(value)
		trie.get(value).terminal
	}

	def "does not contain non-existent node"() {
		expect:
		trie.get('nope' as Character[]) == null
	}
	def "contains nodes along value paths"() {
		Character[][] values = [
				'value',
				'valet',
				'valets',
				'boop'
		]

		when:
		values.each(trie.&add)

		then:
		values.each { value ->
			Character[] builder = []
			value.every { c ->
				builder += c

				Trie.Node node = trie.get(builder)
				node != null && node.value == builder
			}
			trie.contains(value)
		}
	}
}
