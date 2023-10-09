package com.codecompiler.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ParticipantDTO{
	private String studentId;
	private String studentEmail;
	private boolean status;	
	private double studentPercentage;

}
