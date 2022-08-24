package com.codecompiler.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Student;
import com.mongodb.BasicDBObject;

public interface StudentService1 {

	public Student findById(String studentId);
	
	public Student findByEmailAndPassword(String email, String password);
	
	public Student findByEmail(String studentEmail);
	
	public ArrayList<Student> findByContestId(String contestId);
	
	public Student saveStudent(Student studentDetails);
	
	public List<String> findEmailByStatus(Boolean True);
	
	public List<String> saveFileForBulkParticipator(MultipartFile file);
	
	public Student deleteByEmail(String emailId);
	
	public List<String> getByEmail(String filterString);
}
