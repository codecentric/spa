package de.codecentric.spa.sql;

/**
 * Exception class which instances are raised when unsupported type is mapped.
 */
public class UnsupportedTypeException extends RuntimeException {

	private static final long serialVersionUID = -2655856137495677008L;

	public UnsupportedTypeException(String message) {
		super(message);
	}

}
