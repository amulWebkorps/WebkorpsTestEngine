package com.codecompiler.service;

import java.util.List;
import java.util.Map;

import com.codecompiler.dto.McqSubmitDto;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MCQ;
import com.codecompiler.entity.Student;

public interface ContestService {

	public Contest findByContestId(String contestId);
	
	public List<Contest> findAllContest();
	
	public Contest saveContest(Contest contest); 
	
	public void deleteContest(String contestId);
	
	Map<String,Object> getContestDetail(String contestId, String contestType);
	
	Map<String,Object> contestPage(String contestId,String studentId, String selectlanguage);
	
	public Contest findByContestName(String contestName);
	
	public Contest findAllUploadedQuetions(String contestId);
	
	public boolean submitMcqContest(McqSubmitDto mcqSubmitDto);
}
