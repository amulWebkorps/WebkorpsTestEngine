package com.codecompiler.controller;

import com.codecompiler.response.ResponseHandler;
import com.codecompiler.service.EmailService;
import com.codecompiler.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
		log.info("sendMail: started, sendEmailDetails size = {}", sendEmailDetails.size());
		emailService.sendMailToStudents(sendEmailDetails);
		log.info("Mail sent successfully");
		return ResponseHandler.generateResponse("success", HttpStatus.OK, "Mail sent successfully");
	}

	@GetMapping("sentMailForParticipator")
	public ResponseEntity<Object> getAllSentMailsForParticipator() {
		log.info("sentMailForParticipator: started");
		List<String> sentMailStudentList = studentService.findEmailByStatus(true);
		log.info("sentMailForParticipator: Ended sentMailStudentList Size :: {}", sentMailStudentList.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, sentMailStudentList);
	}

	@GetMapping("sentMailForParticipatorForMCQ")
	public ResponseEntity<Object> getAllSentMailsForParticipatorForMCQ() {
		log.info("sentMailForParticipatorForMCQ: started");
		List<String> sentMailStudentList = studentService.findEmailByfinalMailSent();
		log.info("sentMailForParticipatorForMCQ: Ended sentMailStudentList Size :: {}", sentMailStudentList.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, sentMailStudentList);
	}
}
