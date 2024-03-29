package com.codecompiler.entity;

import java.util.List;
import java.util.Set;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.codecompiler.dto.TestCaseDTO;

@Document(collection = "StudentDetails")
@EntityScan

public class Student extends User{
	@Id
	private String id;
	private String name;
	private String mobileNumber;
	private String contestLevel;
	private String contestId;
	private Set<String> questionId;
	private List<TestCaseDTO> testCaseRecord;
	private boolean status;
	private String finalMailSent;
	private String participateDate;
	private double percentage;
	private List<String> correctAnswers;
	private List<String> mcqQuetionsId;
	private String mcqContestId;
	private String contestType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getContestLevel() {
		return contestLevel;
	}
	public void setContestLevel(String contestLevel) {
		this.contestLevel = contestLevel;
	}
	public String getContestId() {
		return contestId;
	}
	public void setContestId(String contestId) {
		this.contestId = contestId;
	}
	public Set<String> getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Set<String> questionId) {
		this.questionId = questionId;
	}
	public List<TestCaseDTO> getTestCaseRecord() {
		return testCaseRecord;
	}
	public void setTestCaseRecord(List<TestCaseDTO> testCaseRecord) {
		this.testCaseRecord = testCaseRecord;
	}
	public boolean isStatus() {
		return status;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
		
	public String getParticipateDate() {
		return participateDate;
	}
	public Student() {
		super();		
	}
	public void setParticipateDate(String participateDate) {
		this.participateDate = participateDate;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage2) {
		this.percentage = percentage2;
	}
	public String getContestType() {
		return contestType;
	}
	public void setContestType(String contestType) {
		this.contestType = contestType;
	}
	public Student(String id, String name, String mobileNumber, String contestLevel, String contestId,
			Set<String> questionId, List<TestCaseDTO> testCaseRecord, boolean status, String participateDate,String finalMailSent) {
		super();
		this.id = id;
		this.name = name;
		this.mobileNumber = mobileNumber;
		this.contestLevel = contestLevel;
		this.contestId = contestId;
		this.questionId = questionId;
		this.testCaseRecord = testCaseRecord;
		this.status = status;
		this.participateDate = participateDate;
		this.finalMailSent=finalMailSent;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", contestLevel="
				+ contestLevel + ", contestId=" + contestId + ", questionId=" + questionId + ", testCaseRecord="
				+ testCaseRecord + ", status=" + status + ", finalMailSent=" + finalMailSent + ", participateDate="
				+ participateDate + ", percentage=" + percentage + ", correctAnswers=" + correctAnswers
				+ ", mcqQuetionsId=" + mcqQuetionsId + ", mcqContestId=" + mcqContestId + "]";
	}
	public List<String> getCorrectAnswers() {
		return correctAnswers;
	}
	public void setCorrectAnswers(List<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}
	public List<String> getMcqQuetionsId() {
		return mcqQuetionsId;
	}
	public void setMcqQuetionsId(List<String> mcqQuetionsId) {
		this.mcqQuetionsId = mcqQuetionsId;
	}
	public String getMcqContestId() {
		return mcqContestId;
	}
	public void setMcqContestId(String mcqContestId) {
		this.mcqContestId = mcqContestId;
	}
	public String getFinalMailSent() {
		return finalMailSent;
	}
	public void setFinalMailSent(String finalMailSent) {
		this.finalMailSent = finalMailSent;
	}
		
	
	
}
