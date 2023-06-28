package com.codecompiler.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.codecompiler.dto.ParticipantResultDto;
import com.codecompiler.entity.Student;

public class CompileTask implements Callable<ParticipantResultDto> {

	private Student student;

	public CompileTask(Student student) {
		this.student = student;
	}

	@Override
	public ParticipantResultDto call() throws Exception {
		ParticipantResultDto pt=new ParticipantResultDto();
		ExecutorService executerService=Executors.newFixedThreadPool(student.getTestCaseRecord().size());
		List<Callable<Integer>> callables = new ArrayList<>();
		int testCasesSize=0;
		for(int i=0;i<student.getTestCaseRecord().size();i++) {
			TestCasesCheckThread task=new TestCasesCheckThread(student.getTestCaseRecord().get(i));
			testCasesSize+=student.getTestCaseRecord().get(i).getTestCasesSuccess().size();
			callables.add(task);
		}
		
		try {
			List<Future<Integer>> futures = executerService.invokeAll(callables);
			
			int sum=0;
	        for (Future<Integer> future : futures) {
	            sum+=future.get();
	        }
	        pt.setPercentage(generatePercentage(sum,testCasesSize));
	        pt.setName(student.getName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			executerService.shutdown();
			callables=null;
		}
		return pt;
	}
	
	public Double generatePercentage(int count,int testCasesSize) {
		double percentage = ((100 *  count) /testCasesSize);
		return percentage;
	}
}
