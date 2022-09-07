package com.codecompiler.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.Contest;



public interface ContestRepository extends MongoRepository<Contest,String>{

	public Contest findByContestIdAndContestLevel(String contestid, String contestLevel);
	
	public Contest findByContestId(String id);
	
	public List<Contest> findAll();
	
}
