package com.codecompiler.dto;

import java.util.List;

public class McqSubmitDto {
	private String contestId;
	private List<String> mcqQuetionsId;
	private List<String> correctAnswers;
	private String studentId;
	public String getContestId() {
		return contestId;
	}
	public void setContestId(String contestId) {
		this.contestId = contestId;
	}
	public List<String> getMcqQuetionsId() {
		return mcqQuetionsId;
	}
	public void setMcqQuetionsId(List<String> mcqQuetionsId) {
		this.mcqQuetionsId = mcqQuetionsId;
	}
	public List<String> getCorrectAnswers() {
		return correctAnswers;
	}
	public void setCorrectAnswers(List<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	
	
}
