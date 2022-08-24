package com.codecompiler.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.Student;
import com.mongodb.BasicDBObject;

public interface StudentRepository extends MongoRepository<Student, Integer> {
	Student findByEmailAndPassword(String email, String password);

	Student findByEmail(String email);

	public Student findById(String studentId);

	ArrayList<Student> findByContestId(String contestId);
	
	List<Student> findEmailByStatus(Boolean True);
	
	public Student deleteByEmail(String emailId);
	
	
}
