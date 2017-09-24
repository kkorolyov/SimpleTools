package dev.kkorolyov.simplelogs;

import dev.kkorolyov.simplelogs.append.Appender;
import dev.kkorolyov.simplelogs.append.Appenders;
import dev.kkorolyov.simplelogs.format.Formatter;
import dev.kkorolyov.simplelogs.format.Formatters;
import dev.kkorolyov.simpleprops.Properties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class PropsApplier {
	static void apply(Path logProps) throws IOException {
		Properties props = new Properties(logProps);
		
		for (String key : props.keys()) {
			String[] args = props.getArray(key);

			String name = key;
			int level = resolveLevel(args);
			Formatter formatter = resolveFormatter(args);
			Appender[] appenders = resolveAppenders(args, level);

			Logger.getLogger(name, level, formatter, appenders);
		}
	}
	private static int resolveLevel(String[] args) {
		return Level.fromString(args[0]);
	}
	private static Formatter resolveFormatter(String[] args) {
		return Formatters.simple();	// TODO Actual work
	}
	private static Appender[] resolveAppenders(String[] args, int loggerLevel) throws IOException {
		List<Appender> results = new ArrayList<>();

		for (int i = 1; i < args.length; i++) {
			switch (args[i].toUpperCase()) {
				case "ERR":
					results.add(Appenders.err(loggerLevel));	// TODO Parse custom thresholds
					break;
				case "OUT":
					results.add(Appenders.out(loggerLevel));
					break;
				default:
					Path file = Paths.get(args[i]);
					Path parent = file.getParent();
					if (parent != null) Files.createDirectories(parent);

					results.add(Appenders.file(file, loggerLevel));
			}
		}
		return results.toArray(new Appender[results.size()]);
	}
}
