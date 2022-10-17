package com.codecompiler.dto;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ContestDTO {

	private String contestId;
	private  String contestName;
	private  String contestDescription;
	private  String contestLevel;
	private ArrayList<QuestionStatusDTO> questionStatus = new ArrayList<>();
	private String contestTime;
	private String date;
}
