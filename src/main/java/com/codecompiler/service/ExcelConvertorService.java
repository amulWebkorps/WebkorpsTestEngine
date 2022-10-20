package com.codecompiler.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.entity.MCQ;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;

public interface ExcelConvertorService {

	public static boolean checkExcelFormat(MultipartFile file) {
		String contentType = file.getContentType();
		if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			return true;
		} 
		return false;
	}
	
	public List<Student> convertExcelToListOfStudent(Map<Integer, List<MyCellDTO>> data);
	
	public List<Question> convertExcelToListOfQuestions(Map<Integer, List<MyCellDTO>> data);

	public List<MCQ> convertExcelToListOfMCQ(Map<Integer, List<MyCellDTO>> data);
}
