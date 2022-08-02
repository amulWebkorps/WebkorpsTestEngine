package com.codecompiler.dao;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.Student;

public interface StudentRepository extends  MongoRepository<Student,Integer> {
Student findByEmailAndPassword(String email,String password); 
Student findByEmail(String email);
Student findById(int studentId);
ArrayList<Student> findByContestId(String contestId);
}
