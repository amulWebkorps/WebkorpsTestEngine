package com.codecompiler.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.codecompilercontroller.ExcelPOIHelper;
import com.codecompiler.dao.StudentRepository;
import com.codecompiler.entity.MyCell;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.helper.Helper;
import com.codecompiler.service.StudentService1;

@Service
public class studentServiceImpl implements StudentService1{

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired private MongoTemplate mongoTemplate;
	
	@Resource(name = "excelPOIHelper")
	private ExcelPOIHelper excelPOIHelper;
	
	public Student findById(String studentId) {
		return studentRepository.findById(studentId);		
	}
	
	public Student findByEmailAndPassword(String email, String password) {
		return studentRepository.findByEmailAndPassword(email, password);

	}
	
	public Student findByEmail(String studentEmail) {
		return studentRepository.findByEmail(studentEmail);
	}
	
	public ArrayList<Student> findByContestId(String contestId){
		return studentRepository.findByContestId(contestId);
	}
	
	public Student saveStudent(Student studentDetails) {		
		return studentRepository.save(studentDetails);				
	}
	
	public List<String> findEmailByStatus(Boolean True) {
		List<Student> sentMail = studentRepository.findEmailByStatus(True);
		return 	sentMail.stream().map(Student::getEmail).collect(Collectors.toList());
	}

	@Override
	public List<String> saveFileForBulkParticipator(MultipartFile file) {
		List<Student> uploadParticipator = new ArrayList<>();
		try {
			Map<Integer, List<MyCell>> data = excelPOIHelper.readExcel(file.getInputStream(), file.getOriginalFilename());
			uploadParticipator = Helper.convertExcelToListOfStudent(data);
			studentRepository.saveAll(uploadParticipator);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uploadParticipator.stream().map(Student::getEmail).collect(Collectors.toList());
	}
	
	public Student deleteByEmail(String emailId) {		
		return studentRepository.deleteByEmail(emailId);
	}
	
	public List<Student> getByEmail(String filterByString){		
		Query query=new Query();
		List<Student> studentTemp = new ArrayList<>();
		query.addCriteria(Criteria.where("email").regex(filterByString));
		studentTemp = mongoTemplate.find(query, Student.class);
        return studentTemp;
	}

}
