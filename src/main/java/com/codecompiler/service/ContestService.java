package com.codecompiler.service;

import java.util.List;

import com.codecompiler.entity.Contest;

public interface ContestService {

	public Contest findByContestId(String contestId);
	
	public List<Contest> findAllContest();
	
	public Contest saveContest(Contest contest); 
	
	public void deleteContest(String contestId);
}
