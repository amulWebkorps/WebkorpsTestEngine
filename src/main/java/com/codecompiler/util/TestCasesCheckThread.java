package com.codecompiler.util;

import java.util.concurrent.Callable;

import com.codecompiler.dto.TestCaseDTO;

public class TestCasesCheckThread implements Callable<Integer>{

	private TestCaseDTO testCaseDto;
	
	public TestCasesCheckThread(TestCaseDTO test) {
		this.testCaseDto=test;
	}
	
	@Override
	public Integer call() throws Exception {
		return (int) testCaseDto.getTestCasesSuccess().stream().filter(filter->filter).count();
	}

}
