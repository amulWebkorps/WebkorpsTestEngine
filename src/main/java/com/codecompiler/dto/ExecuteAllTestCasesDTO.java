package com.codecompiler.dto;

import lombok.Data;

@Data
public class ExecuteAllTestCasesDTO {

	private String studentId;
	private String language;
	private String questionId;
	private String code;
	private Integer flag;
}
