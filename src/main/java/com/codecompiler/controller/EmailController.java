package com.codecompiler.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codecompiler.reponse.ResponseHandler;
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
			emailService.sendMailToStudents(sendEmailDetails);
			log.info("Mail Send successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, "mail send successfully");
		} catch (Exception e) {
			log.error("Exception occured in sendMail :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
		}
	}
	
	@GetMapping("sentMailForParticipator")
	public ResponseEntity<Object> getAllSentMailsForParticipator() {
		log.info("sentMailForParticipator: started");
		try {
			List<String> sentMailStudentList = studentService.findEmailByStatus(true);
			log.info("sentMailForParticipator: Ended setMailStudentList Size :: "+ sentMailStudentList.size());
			return ResponseHandler.generateResponse("succcess", HttpStatus.OK, sentMailStudentList);
		} catch (Exception ex) {
			log.error("Exception occured in sendMail :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());
		}	
		
	}
	
}
