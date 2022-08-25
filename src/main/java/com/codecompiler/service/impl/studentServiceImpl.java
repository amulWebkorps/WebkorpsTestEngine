package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.codecompilercontroller.ExcelPOIHelper;
import com.codecompiler.dao.StudentRepository;
import com.codecompiler.entity.MyCell;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.TestCasesRecord;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.StudentService1;

@Service
public class studentServiceImpl implements StudentService1{

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired private MongoTemplate mongoTemplate;
	
	@Resource(name = "excelPOIHelper")
	private ExcelPOIHelper excelPOIHelper;
	
	@Autowired
	private ExcelConvertorService excelConvertorService;
	
	public Student findById(String studentId) {
		return studentRepository.findById(studentId);		
	}
	
	public Student findByEmailAndPassword(String email, String password) {
		return studentRepository.findByEmailAndPassword(email, password);

	}
	
	public Student findByEmail(String studentEmail) {
		return studentRepository.findByEmail(studentEmail);
	}
	
	public ArrayList<Student> findByContestId(String contestId){
		return studentRepository.findByContestId(contestId);
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
		return studentRepository.deleteByEmail(emailId);
	}
	
	public List<String> getByEmail(String filterByString){		
		Query query=new Query();
		List<Student> studentTemp = new ArrayList<>();
		query.addCriteria(Criteria.where("email").regex("^"+filterByString));
		studentTemp = mongoTemplate.find(query, Student.class);
        return studentTemp.stream().map(Student::getEmail).collect(Collectors.toList());
	}
	
	public Student updateStudentDetails(String studentId, String contestId, List<String> questionIds,
			ArrayList<String> testCasesSuccess, String complilationMessage) {
		TestCasesRecord testCasesRecord = new TestCasesRecord();
		List<TestCasesRecord> testCasesRecord1 = new ArrayList<>(); // need to remove in future
		testCasesRecord.setQuestionId(questionIds);
		testCasesRecord.setComplilationMessage(complilationMessage);
		testCasesRecord.setTestCasesSuccess(testCasesSuccess); // create new collection for testcasesrecord and save that pass id in get method
		Student existingRecord = studentRepository.findById(studentId);
		existingRecord.setContestId(contestId);
		if (existingRecord.getQuestionId() != null) {
			existingRecord.getQuestionId().addAll(questionIds);
		} else {
			existingRecord.setQuestionId(questionIds);
		}
		if (existingRecord.getTestCasesRecord() != null) {
			existingRecord.getTestCasesRecord().add(testCasesRecord);
		} else {
			existingRecord.setTestCasesRecord(testCasesRecord1); // need to remove in future
			existingRecord.getTestCasesRecord().add(testCasesRecord);
		}		
		return studentRepository.save(existingRecord);
	}
	
	public Student submitContest(String emailId) {
		Student student = studentRepository.findByEmail(emailId);
		student.setPassword(null);
		return studentRepository.save(student);
	}

}
