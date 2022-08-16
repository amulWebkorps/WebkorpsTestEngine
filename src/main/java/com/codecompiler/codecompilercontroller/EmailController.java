package com.codecompiler.codecompilercontroller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codecompiler.entity.Student;
import com.codecompiler.service.EmailService;
import com.codecompiler.service.StudentService1;

@Controller
@RequestMapping("/admin")
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private StudentService1 studentService1;

	Logger logger = LogManager.getLogger(EmailController.class);
	
	@PostMapping("sendMail")
	public ResponseEntity<Object> sendMail(@RequestParam("StudentIds") List<String> StudentIds, @RequestParam("contestId") String contestId) {
		try {
			for (String studentId : StudentIds) {
				Student studentDetails = studentService1.findById(studentId);
				this.emailService.sendMail(contestId, studentDetails.getName(), studentDetails.getEmail(),"Webkorps Code Assesment Credentials", studentDetails.getPassword());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return generateResponse("mail sent", HttpStatus.OK);
	}

	public ResponseEntity<Object> generateResponse(String msg, HttpStatus status) {
		Map<String, Object> mp = new HashedMap();
		mp.put("message", msg);
		mp.put("status", status);
		return new ResponseEntity<Object>(mp, status);
	}
}
