package com.codecompiler.entity;

import java.util.ArrayList;

public class ResponseToFE {

	private String complilationMessage;
	private ArrayList<String> testCasesSuccess;
	private String successMessage;
	public String getComplilationMessage() {
		return complilationMessage;
	}

	public void setComplilationMessage(String complilationMessage) {
		this.complilationMessage = complilationMessage;
	}

	public void setTestCasesSuccess(ArrayList<String> testCasesSuccess) {
		this.testCasesSuccess = testCasesSuccess;
	}

	public ArrayList<String> getTestCasesSuccess() {
		return testCasesSuccess;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	@Override
	public String toString() {
		return "ResponseToFE [complilationMessage=" + complilationMessage + ", testCasesSuccess=" + testCasesSuccess
				+ ", successMessage=" + successMessage + "]";
	}

	public ResponseToFE() {
		super();
		// TODO Auto-generated constructor stub
	}

}
