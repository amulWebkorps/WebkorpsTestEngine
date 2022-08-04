package com.codecompiler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dao.StudentRepository;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.TestCasesRecord;
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
		List<Student> studentList = studentRepository.findAll();
		return studentList;
	}

	public Student findByEmailAndPassword(String email, String password) {
		return studentRepository.findByEmailAndPassword(email, password);

	}

	public Student saveStudentDetails(Student std) {
		Student con = studentRepository.save(std);
		return con;
	}

	public Student updateStudentDetails(String studentId, String contestId, List<String> questionIds,
			ArrayList<String> testCasesSuccess, String complilationMessage) {
		TestCasesRecord testCasesRecord = new TestCasesRecord();
		testCasesRecord.setQuestionId(questionIds);
		testCasesRecord.setComplilationMessage(complilationMessage);
		testCasesRecord.setTestCasesSuccess(testCasesSuccess);
		Student existingRecord = studentRepository.findById(studentId);
		existingRecord.setContestId(contestId);
		if (existingRecord.getQuestionId() != null) {
			existingRecord.getQuestionId().addAll(existingRecord.getQuestionId());
		}
		existingRecord.setQuestionId(questionIds);
		existingRecord.getTestCasesRecord().add(testCasesRecord);
		return studentRepository.save(existingRecord);
	}

	public Student saveStudent(Student std) {
		Student s = studentRepository.save(std);
		return s;
	}

	public Student findById(String studentId) {
		return studentRepository.findById(studentId);
	}

	public ArrayList<Student> findByContestId(String contestId) {
		ArrayList<Student> students = studentRepository.findByContestId(contestId);
		return students;
	}

}
