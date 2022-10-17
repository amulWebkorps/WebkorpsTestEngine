package com.codecompiler.exception;

public class RecordMisMatchedException extends RuntimeException {

	public RecordMisMatchedException() {
       super();
	}

	public RecordMisMatchedException(String msg) {
		super(msg);
	}
}
