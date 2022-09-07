package com.codecompiler.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentDTO {

	private String id;
	private String name;
	private String mobileNumber;
	private String contestLevel;
	private String contestId;
	private List<String> questionId;
	private List<TestCaseDTO> testCaseRecord;
	private boolean status;
}
