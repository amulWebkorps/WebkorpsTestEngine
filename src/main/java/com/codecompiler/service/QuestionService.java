package com.codecompiler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dao.QuestionRepository;
import com.codecompiler.entity.Question;
import com.codecompiler.helper.Helper;

@Service
public class QuestionService {
	 @Autowired
	    private QuestionRepository questionRepository;
	 
//	  public void save(MultipartFile file) {
//
//	        try {
//	            List<Question> students = Helper.convertExcelToListOfQuestions(file.getInputStream());
//	            this.questionRepository.saveAll(students);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//
//	    }

	public List<Question> getAllQuestion() {
		List<Question> questions =questionRepository.findAll();
        return questions;
	
	}
	public List<Question>  findQuestionByContestLevel(String contestLevel){
	List<Question> question = questionRepository.findByContestLevel(contestLevel);      
	return question;
}
}
