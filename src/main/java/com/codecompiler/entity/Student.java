package com.codecompiler.entity;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "StudentDetails")
@EntityScan
public class Student extends User{
	@Id
	private String id;
	private String name;
	private String mobileNumber;
	private String contestLevel;
	private String contestId;
	private List<String> questionId;
	private List<TestCasesRecord> testCasesRecord;
	private boolean status;
	public Student() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getContestLevel() {
		return contestLevel;
	}

	public void setContestLevel(String contestLevel) {
		this.contestLevel = contestLevel;
	}


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

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getContestId() {
		return contestId;
	}

	public void setContestId(String contestId) {
		this.contestId = contestId;
	}

	public List<String> getQuestionId() {
		return questionId;
	}

	public void setQuestionId(List<String> questionIds) {
		this.questionId = questionIds;
	}

	public List<TestCasesRecord> getTestCasesRecord() {
		return testCasesRecord;
	}

	public void setTestCasesRecord(List<TestCasesRecord> testCasesRecord) {
		this.testCasesRecord = testCasesRecord;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", email=" + super.getEmail() + ", mobileNumber=" + mobileNumber
				+ ", password=" + super.getPassword() + ", contestLevel=" + contestLevel + ", contestId=" + contestId
				+ ", questionId=" + questionId + ", testCasesRecord=" + testCasesRecord + ", status=" + status + "]";
	}

}
