package com.codecompiler.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.Question;



public interface QuestionRepository extends MongoRepository<Question,Integer>{

   public void deleteByQuestionId(String questionId);
   
   public List<Question> findByContestLevel(String filterByString);
   
   public List<Question> findAll();
   
   public List<Question> findByQuestionIdIn(List<String> questionId);
   
   public Question findByQuestionId(String questionId);
   
   public List<Question> findByQuestionStatus(Boolean True);
   
   public List<Question> findByContestLevelAndQuestionStatus(String contestLevel, String True);
   
}

