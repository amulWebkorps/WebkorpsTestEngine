package com.codecompiler.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.codecompiler.entity.Contest;


@Repository
public interface ContestRepository extends  MongoRepository<Contest,String>{

	public Contest findByContestIdAndContestLevel(String contestid, String contestLevel);
	
	public Contest findByContestId(String id);
	
}
