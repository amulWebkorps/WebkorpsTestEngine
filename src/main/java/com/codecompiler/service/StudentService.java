package com.codecompiler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Student;

public interface StudentService {

	public Student findById(String studentId);
	
	public Student findByEmailAndPassword(String email, String password);
	
	public Student findByEmail(String studentEmail);
	
	public ArrayList<Student> findByContestId(String contestId);
	
	public Student saveStudent(Student studentDetails);
	
	public List<String> findEmailByStatus(Boolean True);
	
	public List<String> saveFileForBulkParticipator(MultipartFile file);
	
	public Student deleteByEmail(String emailId);
		
	public Student updateStudentDetails(String studentId, String contestId, List<String> questionIds,
			ArrayList<Boolean> testCasesSuccess, String complilationMessage);
	
	public Student finalSubmitContest(String emailId);
	
	public List<String> findAll();
	
	Map<String, Object> getParticipatorDetail( String studentId);
}
