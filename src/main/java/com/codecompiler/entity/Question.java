package com.codecompiler.entity;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;

//import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="QuestionAndTestCases")
@EntityScan
public class Question {

	@org.springframework.data.annotation.Id
	private String questionId;	
	private String question;
	private  String contestLevel;
	
	private List<SampleTestCase> sampleTestCase;
	private List<TestCases> testcases;

	public Question() {
		super();
		
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

	public List<SampleTestCase> getSampleTestCase() {
		return sampleTestCase;
	}

	public void setSampleTestCase(List<SampleTestCase> sampleTestCase) {
		this.sampleTestCase = sampleTestCase;
	}

	public List<TestCases> getTestcases() {
		return testcases;
	}

	public void setTestcases(List<TestCases> testcases) {
		this.testcases = testcases;
	}

	@Override
	public String toString() {
		return "Question [questionId=" + questionId + ", question=" + question + ", contestLevel=" + contestLevel
				+ ", sampleTestCase=" + sampleTestCase + ", testcases=" + testcases + "]";
	}
	
	
	
}
