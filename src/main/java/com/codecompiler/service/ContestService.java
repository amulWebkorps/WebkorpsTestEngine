package com.codecompiler.service;

import java.util.List;
import java.util.Map;

import com.codecompiler.entity.Contest;

public interface ContestService {

	public Contest findByContestId(String contestId);
	
	public List<Contest> findAllContest();
	
	public Contest saveContest(Contest contest); 
	
	public void deleteContest(String contestId);
	
	Map<String,Object> getContestDetail(String contestId);
	
	Map<String,Object> contestPage(String contestId,String studentId, String selectlanguage);
}
