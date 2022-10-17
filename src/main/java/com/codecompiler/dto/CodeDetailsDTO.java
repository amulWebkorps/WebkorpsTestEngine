package com.codecompiler.dto;

import java.util.List;

import lombok.Data;

@Data
public class CodeDetailsDTO {

	private String studentId;
	private String language;
	private int flag;
	private String contestId;
	private List<QuestionAndCodeDTO> questionsAndCode;
	private Boolean timeOut;
}
