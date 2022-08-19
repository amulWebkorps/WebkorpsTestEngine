package com.codecompiler.service;

import com.codecompiler.entity.Student;

public interface StudentService1 {

	public Student findById(String studentId);
	
	public Student findByEmailAndPassword(String email, String password);
	
	public Student findByEmail(String studentEmail);
}
