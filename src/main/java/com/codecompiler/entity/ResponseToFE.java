package com.codecompiler.entity;

import java.util.ArrayList;

public class ResponseToFE {
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

@Override
public String toString() {
	return "ResponseToFE [complilationMessage=" + complilationMessage + ", testCasesSuccess=" + testCasesSuccess + "]";
}

public ResponseToFE() {
	super();
	// TODO Auto-generated constructor stub
}

}
