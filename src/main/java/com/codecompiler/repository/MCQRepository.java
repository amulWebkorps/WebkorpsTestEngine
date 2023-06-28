package com.codecompiler.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.codecompiler.entity.MCQ;

@Repository
public interface MCQRepository extends MongoRepository<MCQ,Integer> {
	
	public List<MCQ> findByMcqIdIn(List<String> mcqId);
	public List<MCQ> findByMcqStatus(boolean mcqId);
	public MCQ findByMcqId(String mcqQuestionId);
	@Query(value="{'mcqStatus' : :#{#status}}")
	public List<MCQ> findAllMCQ(boolean status);
}
