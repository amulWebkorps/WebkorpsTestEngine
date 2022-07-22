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
	private ArrayList<String> level1QuestionIds = new ArrayList<>();
	private ArrayList<String> level2QuestionIds = new ArrayList<>();
   public Contest() {
		super();
		
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
public ArrayList<String> getLevel1QuestionIds() {
	return level1QuestionIds;
}
public void setLevel1QuestionIds(ArrayList<String> level1QuestionIds) {
	this.level1QuestionIds = level1QuestionIds;
}
public ArrayList<String> getLevel2QuestionIds() {
	return level2QuestionIds;
}
public void setLevel2QuestionIds(ArrayList<String> level2QuestionIds) {
	this.level2QuestionIds = level2QuestionIds;
}
@Override
public String toString() {
	return "Contest [contestId=" + contestId + ", contestName=" + contestName + ", contestDescription="
			+ contestDescription + ", contestLevel=" + contestLevel + ", QuestionIds=" + QuestionIds
			+ ", level1QuestionIds=" + level1QuestionIds + ", level2QuestionIds=" + level2QuestionIds + "]";
}
   	
}
