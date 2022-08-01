package com.codecompiler.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="HrInfo")
@EntityScan
public class HrDetails {
	@Id
	int hId;
	String hName;
	int hNumber;
	String Email;
	public int gethId() {
		return hId;
	}
	public void sethId(int hId) {
		this.hId = hId;
	}
	public String gethName() {
		return hName;
	}
	public void sethName(String hName) {
		this.hName = hName;
	}
	public int gethNumber() {
		return hNumber;
	}
	public void sethNumber(int hNumber) {
		this.hNumber = hNumber;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}

}
