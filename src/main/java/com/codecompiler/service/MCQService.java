package com.codecompiler.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.MCQ;
import com.codecompiler.entity.Question;

public interface MCQService {
	
	public List<MCQ> getAllMCQs(Map<String, List<String>> questionIdList);
	public List<MCQ> saveFileForBulkMCQQuestion(MultipartFile file, String contestId) throws IOException; 
	public List<String> saveMCQContest(Contest contest, List<MCQ> allTrueQuestions);
}
