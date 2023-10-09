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
		log.info("addContest: started sendEmailDetails size = " + sendEmailDetails.size());

		emailService.sendMailToStudents(sendEmailDetails);
		log.info("Mail Send successfully");
		return ResponseHandler.generateResponse("success", HttpStatus.OK, "mail send successfully");

	}

	@GetMapping("sentMailForParticipator")
	public ResponseEntity<Object> getAllSentMailsForParticipator() {
		log.info("sentMailForParticipator: started");

		List<String> sentMailStudentList = studentService.findEmailByStatus(true);
		log.info("sentMailForParticipator: Ended setMailStudentList Size :: " + sentMailStudentList.size());
		return ResponseHandler.generateResponse("succcess", HttpStatus.OK, sentMailStudentList);

	}

	@GetMapping("sentMailForParticipatorForMCQ")
	public ResponseEntity<Object> getAllSentMailsForParticipatorForMCQ() {
		log.info("sentMailForParticipator: started");

		List<String> sentMailStudentList = studentService.findEmailByfinalMailSent();
		log.info("sentMailForParticipator: Ended setMailStudentList Size :: " + sentMailStudentList.size());
		return ResponseHandler.generateResponse("succcess", HttpStatus.OK, sentMailStudentList);

	}

}
