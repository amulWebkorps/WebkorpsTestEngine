package com.codecompiler.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dao.StudentRepository;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.HrDetails;
import com.codecompiler.entity.Student;
import com.codecompiler.helper.Helper;

@Service
public class StudentService {
	 @Autowired
	    private StudentRepository studentRepository;
	 
	  public void save(MultipartFile file) {

	        try {
	            List<Student> students = Helper.convertExcelToListOfStudent(file.getInputStream());
	            this.studentRepository.saveAll(students);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    }

	    public List<Student> getAllStudents() {
	        return this.studentRepository.findAll();
	    }
	    public Student findByEmailAndPassword(String email, String password)
	    {
	    Student s=	studentRepository.findByEmailAndPassword(email, password);
			return s;
	    	
	    }

}
