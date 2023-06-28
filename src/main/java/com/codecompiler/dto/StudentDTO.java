package com.codecompiler.dto;

import java.util.List;

import com.codecompiler.entity.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StudentDTO extends User{

	private String id;
	private String name;
	private String mobileNumber;
	private String contestLevel;
	private String contestId;
	private List<String> questionId;
	private List<TestCaseDTO> testCaseRecord;
	private boolean status;
	private String participateDate;	
	private double percentage;
}
