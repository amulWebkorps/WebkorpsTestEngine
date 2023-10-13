package com.codecompiler.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.codecompiler.dto.TestCaseDTO;

@Document(collection="QuestionAndTestCases")
@EntityScan
public class Question implements Serializable{

	@Id
	private String questionId;	
	private String question;
	private  String contestLevel;	
	private String questionStatus;
	private List<TestCaseDTO> sampleTestCase;
	private List<TestCases> testcases;
	private String createdDate;
	private String cSampleCode;
	private String questionType;
	private String javaSampleCode;
	private String cPlusPluseSampleCode;
	private String pythonSampleCode;
	private String sampleCode;
	
	public String getcSampleCode() {
		return cSampleCode;
	}

	public void setcSampleCode(String cSampleCode) {
		this.cSampleCode = cSampleCode;
	}

	public String getSampleCode() {
		return sampleCode;
	}

	public void setSampleCode(String sampleCode) {
		this.sampleCode = sampleCode;
	}

	public String getJavaSampleCode() {
		return javaSampleCode;
	}

	public void setJavaSampleCode(String javaSampleCode) {
		this.javaSampleCode = javaSampleCode;
	}

	public String getcPlusPluseSampleCode() {
		return cPlusPluseSampleCode;
	}

	public void setcPlusPluseSampleCode(String cPlusPluseSampleCode) {
		this.cPlusPluseSampleCode = cPlusPluseSampleCode;
	}

	public String getPythonSampleCode() {
		return pythonSampleCode;
	}

	public void setPythonSampleCode(String pythonSampleCode) {
		this.pythonSampleCode = pythonSampleCode;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getCSampleCode() {
		return cSampleCode;
	}

	public void setCSampleCode(String sampleCode) {
		this.cSampleCode = sampleCode;
	}

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
				+ testcases + ", createdDate=" + createdDate + ", getSampleCode()="
				+ ", getQuestionStatus()=" + getQuestionStatus() + ", getQuestionId()="
				+ getQuestionId() + ", getQuestion()=" + getQuestion() + ", getContestLevel()=" + getContestLevel()
				+ ", getSampleTestCase()=" + getSampleTestCase() + ", getTestcases()=" + getTestcases()
				+ ", getCreatedDate()=" + getCreatedDate() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	
}
