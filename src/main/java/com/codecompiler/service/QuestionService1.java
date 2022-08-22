package com.codecompiler.service;

import java.util.List;

import com.codecompiler.entity.Question;

public interface QuestionService1 {

	 public List<Question> getAllQuestion(String contestId, String studentId);
	
	 public List<Question> findAllQuestion();
	 public List<Question> findByQuestionIdIn(List<String> questionId);
	 public Question saveQuestion(Question question);
}
