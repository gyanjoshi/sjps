/**
 * 
 */
package com.example.projectx.exception;

/**
 * @author Gyan Prakash Joshi
 * Jan 31, 2021
 * Represents exception object for the In valid Category( of an article )  error
 */
public class InvalidCategoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidCategoryException(String message) {
		super(message);
	}

}
