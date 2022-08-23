package com.codecompiler.service;

import java.util.ArrayList;

import com.codecompiler.entity.Student;

public interface StudentService1 {

	public Student findById(String studentId);
	
	public Student findByEmailAndPassword(String email, String password);
	
	public Student findByEmail(String studentEmail);
	
	ArrayList<Student> findByContestId(String contestId);
}
