package com.rental.validator;

public enum ErrorCode {
	ERR02("Error Input item doesn't exists in the inventory.Please create item"),
	ERR06("Invalid Input");
	
	private String value;
	
	ErrorCode(String value){
		this.value = value;
	}	
	
	public String getValue(){
		return value;
	}
}
