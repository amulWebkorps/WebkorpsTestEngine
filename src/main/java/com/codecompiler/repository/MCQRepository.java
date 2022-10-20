package com.codecompiler.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.codecompiler.entity.MCQ;

@Repository
public interface MCQRepository extends MongoRepository<MCQ, Integer> {

	List<MCQ> findByMcqIdIn(List<String> mcqInContest);

	List<MCQ> findByMcqStatus(boolean status);

}
