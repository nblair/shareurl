package edu.wisc.wisccal.shareurl.sasecurity;

/**
 * {@link Exception} raised when the target delegate calendar account cannot be found.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: DelegateNotFoundException.java 1966 2010-04-20 17:44:20Z npblair $
 */
public class DelegateNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DelegateNotFoundException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DelegateNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public DelegateNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DelegateNotFoundException(Throwable cause) {
		super(cause);
	}

}
