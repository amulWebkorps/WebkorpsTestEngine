package com.codecompiler.entity;

import java.util.ArrayList;
import java.util.List;

public class TestCasesRecord {

	private List<String> questionId;
	private String complilationMessage;
	private ArrayList<String> testCasesSuccess;

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

	public List<String> getQuestionId() {
		return questionId;
	}

	public void setQuestionId(List<String> questionIds) {
		this.questionId = questionIds;
	}

	@Override
	public String toString() {
		return "TestCasesRecord [questionId=" + questionId + ", complilationMessage=" + complilationMessage
				+ ", testCasesSuccess=" + testCasesSuccess + "]";
	}
	
}
