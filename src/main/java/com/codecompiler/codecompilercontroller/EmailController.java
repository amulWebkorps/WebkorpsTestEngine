package com.codecompiler.codecompilercontroller;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.codecompiler.entity.Student;
import com.codecompiler.service.EmailService;
import com.codecompiler.service.StudentService1;

@Controller
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private StudentService1 studentService1;

	Logger logger = LogManager.getLogger(EmailController.class);
	
	@PostMapping("sendMail")
	public ResponseEntity<Object> sendMail(@RequestBody Map<String, List<String>> sendEmailDetails) {
		try {
			for (String studentEmail : sendEmailDetails.get("studentEmails")) {
				Student studentDetails = studentService1.findByEmail(studentEmail);
				this.emailService.sendMail(sendEmailDetails.get("contestId").get(0), studentDetails.getName(), studentDetails.getEmail(),"Webkorps Code Assesment Credentials", studentDetails.getPassword());
				studentDetails.setStatus(true);
				studentService1.saveStudent(studentDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return generateResponse("mail not sent", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return generateResponse("mail sent", HttpStatus.OK);
	}

	public ResponseEntity<Object> generateResponse(String msg, HttpStatus status) {
		Map<String, Object> mp = new HashedMap();
		mp.put("message", msg);
		mp.put("status", status);
		return new ResponseEntity<Object>(mp, status);
	}
	
	@GetMapping("sentMailForParticipator")
	public ResponseEntity<Object> sentMailForParticipator() {
		List<String> sentMailStudentList = new ArrayList<>();
		try {
			sentMailStudentList = studentService1.findEmailByStatus(true);
			return new ResponseEntity<Object>(sentMailStudentList, HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<Object>("no email sent", HttpStatus.BAD_REQUEST);
		}		
	}
	
}
