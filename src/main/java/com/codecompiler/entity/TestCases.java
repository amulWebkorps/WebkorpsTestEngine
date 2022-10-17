package com.codecompiler.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;


public class TestCases implements Serializable{

	@Id 
	private int id;
	private  String input;	
	private String output;
	
    public TestCases() { }

	@Override
	public String toString() {
		return "TestCases [id=" + id + ", input=" + input + ", output=" + output + "]";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	
	

}
