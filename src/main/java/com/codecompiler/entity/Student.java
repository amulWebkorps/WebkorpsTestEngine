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
	private String participateDate;
	private double percentage;
	
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
	public Student(String id, String name, String mobileNumber, String contestLevel, String contestId,
			Set<String> questionId, List<TestCaseDTO> testCaseRecord, boolean status, String participateDate) {
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
	}
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", contestLevel="
				+ contestLevel + ", contestId=" + contestId + ", questionId=" + questionId + ", testCaseRecord="
				+ testCaseRecord + ", status=" + status + ", participateDate=" + participateDate + ", percentage=" + percentage
				+ "]";
	}		
}
