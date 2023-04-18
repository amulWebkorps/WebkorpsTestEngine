package com.codecompiler.dto;

import java.util.ArrayList;

public class CodeResponseDTO {

	private String complilationMessage;
	private ArrayList<Boolean> testCasesSuccess;
	private String successMessage;

	private Double studentPercentage;
	public String getComplilationMessage() {
		return complilationMessage;
	}

	public void setComplilationMessage(String complilationMessage) {
		this.complilationMessage = complilationMessage;
	}

	public void setTestCasesSuccess(ArrayList<Boolean> testCasesSuccess) {
		this.testCasesSuccess = testCasesSuccess;
	}

	public ArrayList<Boolean> getTestCasesSuccess() {
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

	public CodeResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Double getStudentPercentage() {
		return studentPercentage;
	}

	public void setStudentPercentage(Double studentPercentage) {
		this.studentPercentage = studentPercentage;
	}
}
