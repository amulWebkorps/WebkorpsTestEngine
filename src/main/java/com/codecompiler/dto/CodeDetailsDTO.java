package com.codecompiler.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeDetailsDTO {

	private String studentId;
	private String language;
	private int flag;
	private String contestId;
//	private List<QuestionAndCodeDTO> questionsAndCode;
  private List<QuestionDetailDTO> questionsAndCode;
	private Boolean timeOut;

	public CodeDetailsDTO(String studentId, String language, int flag, String contestId, List<QuestionDetailDTO> questionsAndCode, Boolean timeOut) {
		this.studentId = studentId;
		this.language = language;
		this.flag = flag;
		this.contestId = contestId;
		this.questionsAndCode = questionsAndCode;
		this.timeOut = timeOut;
	}
}
