package com.codecompiler.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.MyCell;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;

public interface ExcelConvertorService {

	public boolean checkExcelFormat(MultipartFile file);
	
	public List<Student> convertExcelToListOfStudent(Map<Integer, List<MyCell>> data);
	
	public List<Question> convertExcelToListOfQuestions(Map<Integer, List<MyCell>> data);
}
