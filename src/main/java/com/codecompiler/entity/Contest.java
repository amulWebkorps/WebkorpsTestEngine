package com.codecompiler.entity;

import java.util.ArrayList;

import javax.annotation.Generated;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="ContestName")
@EntityScan
public class Contest {
	@Id
	@Generated(value = "uuid")	
	private String contestId;
	private  String contestName;
	private  String contestDescription;
	private  String contestLevel;
	private ArrayList<String> QuestionIds = new ArrayList<>();
	public Contest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getContestId() {
		return contestId;
	}
	public void setContestId(String contestId) {
		this.contestId = contestId;
	}
	public String getContestName() {
		return contestName;
	}
	public void setContestName(String contestName) {
		this.contestName = contestName;
	}
	public String getContestDescription() {
		return contestDescription;
	}
	public void setContestDescription(String contestDescription) {
		this.contestDescription = contestDescription;
	}
	public String getContestLevel() {
		return contestLevel;
	}
	public void setContestLevel(String contestLevel) {
		this.contestLevel = contestLevel;
	}
	public ArrayList<String> getQuestionIds() {
		return QuestionIds;
	}
	public void setQuestionIds(ArrayList<String> questionIds) {
		QuestionIds = questionIds;
	}
	@Override
	public String toString() {
		return "Contest [contestId=" + contestId + ", contestName=" + contestName + ", contestDescription="
				+ contestDescription + ", contestLevel=" + contestLevel + ", QuestionIds=" + QuestionIds + "]";
	}
	
	
}
