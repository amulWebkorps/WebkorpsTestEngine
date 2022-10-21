package com.codecompiler.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MCQDTO {
	private String mcqId;	
	private String mcqQuestion;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private String correctOption;
	private String mcqStatus;
	private String createdDate;
}
