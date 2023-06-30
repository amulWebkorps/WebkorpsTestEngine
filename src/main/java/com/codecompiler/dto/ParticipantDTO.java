package com.codecompiler.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ParticipantDTO{
	private String id;
	private String email;
	private boolean status;	
	private double percentage;

}
