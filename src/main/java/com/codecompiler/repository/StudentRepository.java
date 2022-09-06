package com.codecompiler.repository;

import java.util.List;

import com.codecompiler.entity.Student;

public interface StudentRepository extends UserRepository<Student> {
	Student findByEmailAndPassword(String email, String password);

	Student findByEmail(String email);

	public Student findById(String studentId);

	List<Student> findByContestId(String contestId);
	
	List<Student> findEmailByStatus(Boolean True);
	
	public Student deleteByEmail(String emailId);
	
	
}
