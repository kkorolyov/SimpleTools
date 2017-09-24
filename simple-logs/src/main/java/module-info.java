/**
 * Lightweight Java logging library with minimal configuration.
 */
module dev.kkorolyov.simplelogs {
	requires static dev.kkorolyov.simpleprops;

	exports dev.kkorolyov.simplelogs;
	exports dev.kkorolyov.simplelogs.append;
	exports dev.kkorolyov.simplelogs.format;
}
