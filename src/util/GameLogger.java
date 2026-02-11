package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * GameLogger class is for logging operations.
 */
public class GameLogger {
	
	private static final String LOG_FILE = "log.txt";
	
	/**
	 * Appends the specified message to the log file.
	 * @param message The text content to write to the log file.
	 */
	public static void log(String message) {
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))){
			writer.write(message);
			writer.newLine();
			
		}
		catch (IOException e) {
			System.out.println("Log'da bi hata var.");
		}
		
	}
	
	
	
}
