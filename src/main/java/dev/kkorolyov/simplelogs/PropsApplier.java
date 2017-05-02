package dev.kkorolyov.simplelogs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dev.kkorolyov.simpleprops.Properties;

class PropsApplier {
	static void apply(Path logProps) throws IOException {
		Map<String, OutputStream> knownStreams = generateKnownStreams();
		Properties props = new Properties(logProps);
		
		for (String key : props.keys()) {
			Logger logger = Logger.getLogger(key);
			logger.setWriters((PrintWriter[]) null);
			
			String[] args = props.getArray(key);
			if (args.length > 0)
				logger.setLevel(Level.valueOf(args[0]));
			
			for (int i = 1; i < args.length; i++) {
				OutputStream stream = knownStreams.get(args[i].toUpperCase(Locale.ENGLISH));	// Check if stream
				
				if (stream != null)
					logger.addWriter(new PrintWriter(stream));
				else {
					Path 	file = Paths.get(args[i]),
								parent = file.getParent();
					if (parent != null)
						Files.createDirectories(parent);
					
					logger.addWriter(new PrintWriter(file.toFile()));
				}
			}
		}
	}
	private static Map<String, OutputStream> generateKnownStreams() {
		Map<String, OutputStream> knownStreams = new HashMap<>();
		knownStreams.put("OUT", System.out);
		knownStreams.put("ERR", System.err);
		
		return knownStreams;
	}
}
