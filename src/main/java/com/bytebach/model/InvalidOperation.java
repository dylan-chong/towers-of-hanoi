package com.bytebach.model;

/**
 * An invalid operation exception is used to signal the user attempted something
 * which is not permitted.
 * 
 * @author djp
 * 
 */
public class InvalidOperation extends RuntimeException {
	public InvalidOperation(String msg) {
		super(msg);
	}
}
