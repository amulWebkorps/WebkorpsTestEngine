package com.codecompiler.exception;

public class SavedQuestionStatusFalseException extends RuntimeException {

	public SavedQuestionStatusFalseException() {
       super();
	}

	public SavedQuestionStatusFalseException(String msg) {
		super(msg);
	}
}
