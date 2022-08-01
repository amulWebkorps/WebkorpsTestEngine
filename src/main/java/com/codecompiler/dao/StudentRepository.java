package com.codecompiler.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.Student;

public interface StudentRepository extends  MongoRepository<Student,Integer> {
Student findByEmailAndPassword(String email,String password); 
Student findByEmail(String email);
Student findById(int studentId);
}
