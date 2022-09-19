package com.codecompiler.entity;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;

//import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import com.codecompiler.dto.TestCaseDTO;

@Document(collection="QuestionAndTestCases")
@EntityScan
public class Question {

	@org.springframework.data.annotation.Id
	private String questionId;	
	private String question;
	private  String contestLevel;	
	private String questionStatus;
	private List<TestCaseDTO> sampleTestCase;
	private List<TestCases> testcases;
	private String createdDate;

	public Question() {
		super();
		
	}

	public String getQuestionStatus() {
		return questionStatus;
	}

	public void setQuestionStatus(String questionStatus) {
		this.questionStatus = questionStatus;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getContestLevel() {
		return contestLevel;
	}

	public void setContestLevel(String contestLevel) {
		this.contestLevel = contestLevel;
	}

	public List<TestCaseDTO> getSampleTestCase() {
		return sampleTestCase;
	}

	public void setSampleTestCase(List<TestCaseDTO> sampleTestCase) {
		this.sampleTestCase = sampleTestCase;
	}

	public List<TestCases> getTestcases() {
		return testcases;
	}

	public void setTestcases(List<TestCases> testcases) {
		this.testcases = testcases;
	}
	
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "Question [questionId=" + questionId + ", question=" + question + ", contestLevel=" + contestLevel
				+ ", questionStatus=" + questionStatus + ", sampleTestCase=" + sampleTestCase + ", testcases="
				+ testcases + ", createdDate=" + createdDate + "]";
	}
}
