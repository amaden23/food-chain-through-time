package exception;

/**
 * GameLoadException is a custom exception for load related errors.
 */
public class GameLoadException extends Exception{
	
	/**
	 * Custom exception.
	 * Handles errors occurring during game resource loading.
	 * @param message Message string.
	 */
	public GameLoadException(String message) {
		super(message);
	}
	
}
