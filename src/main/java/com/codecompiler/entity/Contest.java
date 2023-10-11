package com.codecompiler.entity;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.Generated;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.codecompiler.dto.MCQStatusDTO;
import com.codecompiler.dto.QuestionStatusDTO;

@Document(collection="ContestName")
@EntityScan
public class Contest implements Serializable {
	@Id
	@Generated(value = "uuid")	
	private String contestId;
	private  String contestName;
	private  String contestDescription;
	private  String contestLevel;
	private ArrayList<QuestionStatusDTO> questionStatus = new ArrayList<>();
	private ArrayList<MCQStatusDTO> mcqStatus = new ArrayList<>();
	private String contestTime;
	private String date;
	private String contestType;
	private ArrayList<MCQ> totalAvailableQuestions=new ArrayList<MCQ>();
	
	public Contest() {
		super();		
	}

	public Contest(String contestId, String contestName, String contestLevel, String contestDescription, String date, String contestTime, String contestType) {
		this.contestId = contestId;
		this.contestName = contestName;
		this.contestLevel = contestLevel;
		this.contestDescription = contestDescription;
		this.date = date;
		this.contestTime = contestTime;
		this.contestType = contestType;
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
	public ArrayList<QuestionStatusDTO> getQuestionStatus() {
		return questionStatus;
	}
	public void setQuestionStatus(ArrayList<QuestionStatusDTO> questionStatus) {
		this.questionStatus = questionStatus;
	}
	public ArrayList<MCQStatusDTO> getMcqStatus() {
		return mcqStatus;
	}
	public void setMcqStatus(ArrayList<MCQStatusDTO> mcqStatus) {
		this.mcqStatus = mcqStatus;
	}
	public String getContestTime() {
		return contestTime;
	}
	public void setContestTime(String contestTime) {
		this.contestTime = contestTime;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getContestType() {
		return contestType;
	}
	public void setContestType(String contestType) {
		this.contestType = contestType;
	}
	@Override
	public String toString() {
		return "Contest [contestId=" + contestId + ", contestName=" + contestName + ", contestDescription="
				+ contestDescription + ", contestLevel=" + contestLevel + ", questionStatus=" + questionStatus
				+ ", contestTime=" + contestTime + ", date=" + date + ", contestType=" + contestType + "]";
	}
	public ArrayList<MCQ> getTotalAvailableQuestions() {
		return totalAvailableQuestions;
	}
	public void setTotalAvailableQuestions(ArrayList<MCQ> totalAvailableQuestions) {
		this.totalAvailableQuestions = totalAvailableQuestions;
	}
	
	
	
}
