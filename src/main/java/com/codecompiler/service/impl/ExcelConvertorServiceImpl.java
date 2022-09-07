package com.codecompiler.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.TestCases;
import com.codecompiler.repository.StudentRepository;
import com.codecompiler.service.ExcelConvertorService;

@Service
public class ExcelConvertorServiceImpl implements ExcelConvertorService {

	@Autowired
	private StudentRepository studentRepository;

	public List<Student> convertExcelToListOfStudent(Map<Integer, List<MyCellDTO>> data) {
		List<Student> studentList = new ArrayList<>();
		try {
			for (int i = 1; i < data.size(); i++) {
				List<MyCellDTO> row = data.get(i);
				Student student = new Student();
				String studentId = UUID.randomUUID().toString();
				String characters = "ABCDEFGHLMNOPQRSTUVWXYZabcdghijklmnopqrstuvwxyz0123456789@#$*";
				String pwd = RandomStringUtils.random(7, characters);
				Student exsistingStudent = studentRepository.findByEmail(row.get(1).getContent());
				if (exsistingStudent == null) {
					student.setId(studentId);
					student.setName(row.get(0).getContent());
					student.setEmail(row.get(1).getContent());
					student.setMobileNumber(row.get(2).getContent());
					student.setContestLevel(row.get(3).getContent());
					student.setPassword(pwd);
					student.setRole("ROLE_STUDENT");
					studentList.add(student);
				} else {
					exsistingStudent.setStatus(false);
					studentRepository.save(exsistingStudent);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return studentList;
	}

	public List<Question> convertExcelToListOfQuestions(Map<Integer, List<MyCellDTO>> data) {
		List<Question> questionList = new ArrayList<>();
		try {
			List<MyCellDTO> headerRow = data.get(0);
			for (int i = 1; i < data.size(); i++) {
				Question question = new Question();
				TestCaseDTO sampleTestCases = new TestCaseDTO();
				List<TestCaseDTO> ListSampleTestCase = new ArrayList<>();
				List<TestCases> listTestCases = new ArrayList<>();
				TestCases testCases = new TestCases();
				String tempQid = UUID.randomUUID().toString();
				List<MyCellDTO> row = data.get(i);
				question.setContestLevel(row.get(0).getContent());
				question.setQuestion(row.get(1).getContent());
				sampleTestCases.setConstraints(row.get(2).getContent());
				sampleTestCases.setInput(row.get(3).getContent());
				sampleTestCases.setOutput(row.get(4).getContent());
				for (int k = 5; k < headerRow.size(); k++) {
					int flag = 0;
					if (k % 2 != 0) {
						testCases.setInput(row.get(k).getContent());
						flag = 1;
					} else {
						testCases.setOutput(row.get(k).getContent());
						flag = 2;
					}
					if (flag == 2) {
						listTestCases.add(testCases);
						testCases = new TestCases();
					}
				}
				ListSampleTestCase.add(sampleTestCases);
				question.setQuestionId(tempQid);
				question.setQuestionStatus("true");
				question.setTestcases(listTestCases);
				question.setSampleTestCase(ListSampleTestCase);
				questionList.add(question);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return questionList;
	}
}
