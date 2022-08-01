package com.codecompiler.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.HrDetails;
import com.codecompiler.entity.Student;

public interface StudentRepository extends  MongoRepository<Student,Integer> {
Student findByEmailAndPassword(String email,String password); 
Student findByEmail(String email);
}