package com.codecompiler.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dao.QuestionRepository;
import com.codecompiler.dao.StudentRepository;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.helper.Helper;

@Service
public class QuestionService {
	 @Autowired
	    private QuestionRepository questionRepository;
	 
	  public void save(MultipartFile file) {

	        try {
	            List<Question> students = Helper.convertExcelToListOfOestions(file.getInputStream());
	            this.questionRepository.saveAll(students);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    }
}
