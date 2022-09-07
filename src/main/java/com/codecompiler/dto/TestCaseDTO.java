package com.codecompiler.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class TestCaseDTO {

	
	private  String constraints;
	private  String input;
	private  String output;
	private List<String> questionId;
	private String complilationMessage;
	private ArrayList<Boolean> testCasesSuccess;

}
