package com.codecompiler.dto;

import java.util.List;

import com.codecompiler.entity.TestCases;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionDTO {

	private String questionId;	
	private String question;
	private  String contestLevel;	
	private String questionStatus;
	private List<TestCaseDTO> sampleTestCase;
	private List<TestCases> testcases;
	private String createdDate;
}
