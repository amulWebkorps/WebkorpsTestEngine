package com.codecompiler.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codecompiler.entity.Student;
import com.codecompiler.service.EmailService;
import com.codecompiler.service.StudentService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
@Slf4j
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private StudentService studentService;
	
	@PostMapping("sendMail")
	public ResponseEntity<Object> sendMail(@RequestBody Map<String, List<String>> sendEmailDetails) {
		log.info("addContest: started sendEmailDetails size = "+sendEmailDetails.size());
		try {
			for (String studentEmail : sendEmailDetails.get("studentEmails")) {
				Student studentDetails = studentService.findByEmail(studentEmail);
				this.emailService.sendMail(sendEmailDetails.get("contestId").get(0), studentDetails.getName(), studentDetails.getEmail(),"Webkorps Code Assesment Credentials", studentDetails.getPassword());
				studentDetails.setStatus(true);
				studentService.saveStudent(studentDetails);
			}
		} catch (Exception e) {
			log.error("Exception occured in sendMail :: "+e.getMessage());
			return generateResponse("mail not sent", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		log.info("Mail Send successfully");
		return generateResponse("mail sent", HttpStatus.OK);
	}

	private ResponseEntity<Object> generateResponse(String msg, HttpStatus status) {
		Map<String, Object> mp = new HashedMap<>();
		mp.put("message", msg);
		mp.put("status", status);
		return new ResponseEntity<Object>(mp, status);
	}
	
	@GetMapping("sentMailForParticipator")
	public ResponseEntity<Object> sentMailForParticipator() {
		log.info("sentMailForParticipator: started");
		List<String> sentMailStudentList = new ArrayList<>();
		try {
			sentMailStudentList = studentService.findEmailByStatus(true);
			log.info("sentMailForParticipator: Ended setMailStudentList Size :: "+ sentMailStudentList.size());
			return new ResponseEntity<Object>(sentMailStudentList, HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Exception occured in sendMail :: "+ex.getMessage());
			return new ResponseEntity<Object>("no email sent", HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
	}
	
}
