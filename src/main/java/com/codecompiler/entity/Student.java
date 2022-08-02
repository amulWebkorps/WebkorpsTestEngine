package com.codecompiler.entity;

import java.util.List;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "StudentDetails")
@EntityScan
public class Student {
	@Id
	String id;
	String name;
	String email;
	int mobileNumber;
	String password;
	String contestLevel;
	private String contestId;
	private List<String> questionId;
	private List<TestCasesRecord> testCasesRecord;

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(int mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", email=" + email + ", mobileNumber=" + mobileNumber
				+ ", password=" + password + ", contestLevel=" + contestLevel + ", contestId=" + contestId
				+ ", questionId=" + questionId + ", testCasesRecord=" + testCasesRecord + "]";
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

}
