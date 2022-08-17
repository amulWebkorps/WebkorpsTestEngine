package com.codecompiler.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="CodeLanguage")
@EntityScan
public class Language {

	@Id
	private String id;
	private String language;
	private String codeBase;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCodeBase() {
		return codeBase;
	}
	public void setCodeBase(String codeBase) {
		this.codeBase = codeBase;
	}
	
	@Override
	public String toString() {
		return "Language [id=" + id + ", language=" + language + ", codeBase=" + codeBase + "]";
	}
	
}
