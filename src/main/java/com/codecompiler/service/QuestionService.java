package com.codecompiler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Question;
import com.codecompiler.entity.TestCases;

public interface QuestionService {
	
	 public List<Question> findAllQuestion();
	 
	 public Question saveQuestion(Question question);
	 
	 public List<Question> saveFileForBulkQuestion(MultipartFile file, String contestId) throws IOException;
	 
	 public Question findByQuestionId(String questionId);
	 
	 public List<Question> findByContestLevel(String filterByString);

	 public List<TestCases> getTestCase(String questionId);

	public List<Question> getAllQuestions(Map<String, List<String>> questionIdList);

	public void saveQuestionOrContest(ArrayList<String> contestAndQuestionId);

	public List<Question> filterQuestion(String filterByString);
	 
}
