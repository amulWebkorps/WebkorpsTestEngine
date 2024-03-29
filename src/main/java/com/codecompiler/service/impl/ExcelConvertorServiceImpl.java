package com.codecompiler.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.Admin;
import com.codecompiler.entity.MCQ;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.TestCases;
import com.codecompiler.repository.AdminRepository;
import com.codecompiler.repository.QuestionRepository;
import com.codecompiler.repository.StudentRepository;
import com.codecompiler.service.ExcelConvertorService;

@Service
public class ExcelConvertorServiceImpl implements ExcelConvertorService {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private QuestionRepository questionRepository;

	public List<Student> convertExcelToListOfStudent(Map<Integer, List<MyCellDTO>> data) {
		List<Student> studentList = new ArrayList<>();
		Set<String> uniqueStudent = new HashSet<String>();
		try {
			for (int i = 1; i < data.size(); i++) {
				List<MyCellDTO> row = data.get(i);
				Student student = new Student();
				String studentId = UUID.randomUUID().toString();
				String characters = "ABCDEFGHLMNOPQRSTUVWXYZabcdghijklmnopqrstuvwxyz0123456789@#$*";
				String pwd = RandomStringUtils.random(7, characters);
				if (!row.get(1).getContent().isEmpty()) {
					Student exsistingStudent = studentRepository.findByEmail(row.get(1).getContent().toLowerCase());
					Admin exsistingAdmin = adminRepository.findByEmail(row.get(1).getContent().toLowerCase());

					if (exsistingStudent == null && exsistingAdmin == null) {
						if (uniqueStudent.contains(row.get(1).getContent().toLowerCase()))
							continue;
						student.setId(studentId);
						student.setName(row.get(0).getContent());
						student.setEmail(row.get(1).getContent().toLowerCase());
						student.setMobileNumber(row.get(2).getContent());
						student.setContestLevel(row.get(3).getContent());
						
						student.setPassword(pwd);
						student.setRole("ROLE_STUDENT");
						uniqueStudent.add(student.getEmail());
						studentList.add(student);
					} else if (exsistingAdmin == null) {
						exsistingStudent.setStatus(false);
						exsistingStudent.setPassword(pwd);
						exsistingStudent.setQuestionId(null);
						exsistingStudent.setPercentage(0);
						exsistingStudent.setTestCaseRecord(null);
						exsistingStudent.setContestId(null);
						exsistingStudent.setParticipateDate(null);
						studentRepository.save(exsistingStudent);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return studentList;
	}

	public List<Question> convertExcelToListOfQuestions(Map<Integer, List<MyCellDTO>> data) {
		List<Question> questionList = new ArrayList<>();
		List<Question> listOfQuestions = questionRepository.findAll();

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
					if (row.get(k).getContent() != "") {
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
				}
				ListSampleTestCase.add(sampleTestCases);
				question.setQuestionId(tempQid);
				question.setQuestionStatus("true");
				question.setTestcases(listTestCases);
				question.setSampleTestCase(ListSampleTestCase);
				question.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

				Question result = listOfQuestions.stream()
						.filter(obj -> obj.getQuestion().equals(question.getQuestion())).findFirst().orElse(null);

				if (result != null)
					continue;

				questionList.add(question);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return questionList;
	}
	
	public List<MCQ> convertExcelToListOfMCQ(Map<Integer, List<MyCellDTO>> data) {
		List<MCQ> mcqQuestionList = new ArrayList<>();
		try {
			for (int i = 1; i < data.size(); i++) {
				MCQ mcqQuestion = new MCQ();
				String tempMCQid = UUID.randomUUID().toString();
				List<MyCellDTO> row = data.get(i);
				mcqQuestion.setMcqQuestion(row.get(0).getContent());
				mcqQuestion.setOption1(row.get(1).getContent());
				mcqQuestion.setOption2(row.get(2).getContent());
				mcqQuestion.setOption3(row.get(3).getContent());
				mcqQuestion.setOption4(row.get(4).getContent());
				String[] splitData = row.get(5).getContent().split(",");
				List<String> listData=new ArrayList<String>();
				for(int m = 0; m< splitData.length ;m++) {
					listData.add(splitData[m]);
				}
				mcqQuestion.setCorrectOption(listData);
				mcqQuestion.setMcqId(tempMCQid);
				mcqQuestion.setMcqStatus(true);
			    mcqQuestionList.add(mcqQuestion);
			 
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mcqQuestionList;
	}

}
