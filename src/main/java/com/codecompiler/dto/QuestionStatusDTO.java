package com.codecompiler.dto;

public class QuestionStatusDTO {

	private String questionId;
	private boolean status;
	public QuestionStatusDTO() {
		super();
		}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public boolean getStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "QuestionStatus [questionId=" + questionId + ", status=" + status + "]";
	}
	
	
	
}
