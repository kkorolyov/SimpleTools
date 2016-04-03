package dev.kkorolyov.simplelogs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Prints a message to a file.
 */
public class FilePrinter implements Printer {
	private File file;
	private BufferedWriter writer;
	
	/**
	 * Creates a new file printer printing to a file of the specified name.
	 * @param filename name of file to print to
	 */
	public FilePrinter(String filename) {
		this(new File(filename));
	}
	/**
	 * Creates a new file printer printing to the specified file.
	 * @param file file to print to
	 */
	public FilePrinter(File file) {
		this.file = file;

		clearFile(this.file);
	}
	
	private static void clearFile(File file) {
		try (FileWriter writer = new FileWriter(file)){
			// Do nothing
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void print(String message) {
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			
			writer.write(message);
			writer.newLine();
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
