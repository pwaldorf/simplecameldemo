package com.pw.resumecameldemo.bean;

public class MyCustomException extends RuntimeException {

	public MyCustomException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;

}
