package com.codecompiler.dto;

public class QuestionAndCodeDTO {

	private String questionId;
	private String code;
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "QuestionAndCodeDTO [questionId=" + questionId + ", code=" + code + "]";
	}
}
