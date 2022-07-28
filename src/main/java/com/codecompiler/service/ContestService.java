package com.codecompiler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dao.ContestRepository;
import com.codecompiler.dao.QuestionRepository;
import com.codecompiler.entity.Contest;

@Service
public class ContestService {

	@Autowired
	ContestRepository contestRepository;
	
	@Autowired
	QuestionRepository questionRepository;
	
	public Contest saveContest(Contest contest) {
		Contest con = contestRepository.save(contest);				
		return con;
	}
	
	public List<Contest> getAllContest() {
		List<Contest> contestList = contestRepository.findAll();	
		return contestList;
	}
	
	public Contest getContestBasedOnContestIdAndLevel(String cId, String clevel) {
		Contest contest = contestRepository.findByContestIdAndContestLevel(cId, clevel);	
		return contest;
		
	}
	
	public Contest findByContestId(String cId) {
		Contest contest = contestRepository.findByContestId(cId);			
		return contest;
	}

	public void deleteContest(String contestId) {
		contestRepository.deleteById(contestId);			
			}
	
}
