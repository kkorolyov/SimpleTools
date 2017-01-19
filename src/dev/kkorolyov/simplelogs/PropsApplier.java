package dev.kkorolyov.simplelogs;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dev.kkorolyov.simplelogs.Logger.Level;
import dev.kkorolyov.simpleprops.Properties;

class PropsApplier {
	private static final Map<String, OutputStream> knownStreams = new HashMap<>();
	
	static {
		knownStreams.put("OUT", System.out);
		knownStreams.put("ERR", System.err);
	}
	
	static void apply(Path logPropsFile) throws IOException {
		Properties props = new Properties(logPropsFile);
		
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
					File file = new File(args[i]);
					if (!file.isFile()) {	// Check if exists
						File parent = file.getParentFile();
						if (parent != null && !parent.exists())
							parent.mkdirs();
					}
					logger.addWriter(new PrintWriter(file));
				}
			}
		}
	}
}
