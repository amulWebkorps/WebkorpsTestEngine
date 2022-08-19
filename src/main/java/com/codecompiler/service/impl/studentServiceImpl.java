package com.codecompiler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dao.StudentRepository;
import com.codecompiler.entity.Student;
import com.codecompiler.service.StudentService1;

@Service
public class studentServiceImpl implements StudentService1{

	@Autowired
	private StudentRepository studentRepository;
	
	public Student findById(String studentId) {
		return studentRepository.findById(studentId);
	}
	
	public Student findByEmailAndPassword(String email, String password) {
		return studentRepository.findByEmailAndPassword(email, password);

	}
	
	public Student findByEmail(String studentEmail) {
		return studentRepository.findByEmail(studentEmail);
	}
}
