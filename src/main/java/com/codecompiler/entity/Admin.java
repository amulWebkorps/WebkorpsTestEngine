package com.codecompiler.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="admin")
@EntityScan
public class Admin extends User{
	@Id
	private String hId;
	private String hName;
	private String hNumber;

	public String gethId() {
		return hId;
	}
	public void sethId(String hId) {
		this.hId = hId;
	}
	public String gethName() {
		return hName;
	}
	public void sethName(String hName) {
		this.hName = hName;
	}
	public String gethNumber() {
		return hNumber;
	}
	public void sethNumber(String hNumber) {
		this.hNumber = hNumber;
	}


}
