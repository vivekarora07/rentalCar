package com.rental.validator;

public class InvalidRequestException extends Exception{

	private static final long serialVersionUID = -6030822697975955650L;

	public InvalidRequestException(String reason) {
		super(reason);
	}
}
