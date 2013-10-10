package edu.wisc.wisccal.shareurl;

/**
 * {@link Exception} raised when a valid "guessable" share already exists
 * and an attempt to create another is made.
 * 
 * @author Nicholas Blair
 */
public class GuessableShareAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8486905820036319786L;

	/**
	 * 
	 */
	public GuessableShareAlreadyExistsException() {
	}

	/**
	 * @param message
	 */
	public GuessableShareAlreadyExistsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GuessableShareAlreadyExistsException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GuessableShareAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
