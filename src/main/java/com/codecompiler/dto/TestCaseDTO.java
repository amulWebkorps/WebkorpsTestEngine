package com.codecompiler.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class TestCaseDTO {

	
	private  String constraints;
	private  String input;
	private  String output;
	private Set<String> questionId;
	private String complilationMessage;
	private ArrayList<Boolean> testCasesSuccess;
	private String fileName;
	public String getConstraints() {
		return constraints;
	}
	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public Set<String> getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Set<String> questionId) {
		this.questionId = questionId;
	}
	public String getComplilationMessage() {
		return complilationMessage;
	}
	public void setComplilationMessage(String complilationMessage) {
		this.complilationMessage = complilationMessage;
	}
	public ArrayList<Boolean> getTestCasesSuccess() {
		return testCasesSuccess;
	}
	public void setTestCasesSuccess(ArrayList<Boolean> testCasesSuccess) {
		this.testCasesSuccess = testCasesSuccess;
	}
	

}
