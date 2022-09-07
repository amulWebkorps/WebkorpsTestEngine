package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.MyCell;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.UnSupportedFormatException;
import com.codecompiler.exception.UserNotFoundException;
import com.codecompiler.helper.ExcelPOIHelper;
import com.codecompiler.repository.StudentRepository;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService{

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private QuestionService questionService;

	@Resource(name = "excelPOIHelper")
	private ExcelPOIHelper excelPOIHelper;
	
	@Autowired
	private ExcelConvertorService excelConvertorService;
	
	public Student findById(String studentId) {
		Student student = studentRepository.findById(studentId);	
		if(student==null) {
			throw new UserNotFoundException("Student with id :: "+studentId+" does not found");
		}
		return 	student;
	}
	
	public Student findByEmailAndPassword(String email, String password) {
		return studentRepository.findByEmailAndPassword(email, password);

	}
	
	public Student findByEmail(String studentEmail) {
		return studentRepository.findByEmail(studentEmail); 
	}
	
	public List<Student> findByContestId(String contestId){
		List<Student> students = studentRepository.findByContestId(contestId);
		if(students==null) {
			throw new RecordNotFoundException("No Student Found in Contest with id ::"+contestId);
		}
		return students;
	}
	
	public Student saveStudent(Student studentDetails) {
		return studentRepository.save(studentDetails);				
	}
	
	public List<String> findEmailByStatus(Boolean True) {
		List<Student> sentMail = studentRepository.findEmailByStatus(True);
		return 	sentMail.stream().map(Student::getEmail).collect(Collectors.toList());
	}

	@Override
	public List<String> saveFileForBulkParticipator(MultipartFile file) {
		if (!ExcelConvertorService.checkExcelFormat(file)) {
			throw new UnSupportedFormatException("Please check excel file format");
		}
		List<Student> uploadParticipator = new ArrayList<>();
		try {
			Map<Integer, List<MyCell>> data = excelPOIHelper.readExcel(file.getInputStream(), file.getOriginalFilename());
			uploadParticipator = excelConvertorService.convertExcelToListOfStudent(data);
			studentRepository.saveAll(uploadParticipator);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uploadParticipator.stream().map(Student::getEmail).collect(Collectors.toList());
	}
	
	public Student deleteByEmail(String emailId) {		
		Student student = studentRepository.findByEmail(emailId);
		if(student==null) {
			throw new UserNotFoundException("User with email :: "+emailId+" not found");
		}
		return studentRepository.deleteByEmail(emailId);
	}
	
	public Student updateStudentDetails(String studentId, String contestId, List<String> questionIds,
			ArrayList<Boolean> testCasesSuccess, String complilationMessage) {
		TestCaseDTO testCaseRecord = new TestCaseDTO();
		List<TestCaseDTO> testCasesRecord1 = new ArrayList<>(); // need to remove in future
		testCaseRecord.setQuestionId(questionIds);
		testCaseRecord.setComplilationMessage(complilationMessage);
		testCaseRecord.setTestCasesSuccess(testCasesSuccess); // create new collection for testcasesrecord and save that pass id in get method
		Student existingRecord = studentRepository.findById(studentId);
		existingRecord.setContestId(contestId);
		if (existingRecord.getQuestionId() != null) {
			existingRecord.getQuestionId().addAll(questionIds);
		} else {
			existingRecord.setQuestionId(questionIds);
		}
		if (existingRecord.getTestCaseRecord() != null) {
			existingRecord.getTestCaseRecord().add(testCaseRecord);
		} else {
			existingRecord.setTestCaseRecord(testCasesRecord1); // need to remove in future
			existingRecord.getTestCaseRecord().add(testCaseRecord);
		}		
		return studentRepository.save(existingRecord);
	}
	
	public Student finalSubmitContest(String emailId) {
		Student student = this.studentRepository.findByEmail(emailId);
		student.setPassword(null); 
		return studentRepository.save(student);
	}

	@Override
	public List<String> findAll() {
		List<Student> presentStudent = studentRepository.findEmailByStatus(false);
		List<String> emailList = presentStudent.stream().map(Student::getEmail).collect(Collectors.toList());
		if(emailList.isEmpty()) {
			throw new RecordNotFoundException("No Participator is in active state");
		}
		return emailList;
	}

	@Override
	public Map<String, Object> getParticipatorDetail(String studentId) {
			Student student = this.findById(studentId);
			if(student.getQuestionId()==null) {
				throw new RecordNotFoundException("Participant did not submit a single Question");
			}
			Map<String, Object> mp = new HashedMap<>();
				List<Question> questionDetail = new ArrayList<>();
				for (String questionId : student.getQuestionId()) {
					Question question = questionService.findByQuestionId(questionId);
					Question questionTemp = new Question();
					questionTemp.setQuestionId(question.getQuestionId());
					questionTemp.setQuestion(question.getQuestion());
					questionTemp.setSampleTestCase(question.getSampleTestCase());
					questionDetail.add(questionTemp);
				mp.put("studentDetail", student);
				mp.put("questionSubmitedByStudent", questionDetail);
	}
				return mp;
	}

}
