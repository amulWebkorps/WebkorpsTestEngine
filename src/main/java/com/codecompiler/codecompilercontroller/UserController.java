package com.codecompiler.codecompilercontroller;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.codecompiler.entity.HrDetails;
import com.codecompiler.entity.Student;
import com.codecompiler.service.AdminService;
import com.codecompiler.service.StudentService1;

@Controller
public class UserController {

	@Autowired
	private StudentService1 studentService;

	@Autowired
	private AdminService adminService;

	Logger logger = LogManager.getLogger(UserController.class);

	@PostMapping("doSignInForParticipator")
	public ResponseEntity<Object> doSignIn(@RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("contestId") String contestId) {

		Student studentExists = studentService.findByEmailAndPassword(email, password);
		if (studentExists == null) {
			logger.error("email and password does not match");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and password does not match");
		} else {
			studentExists.setContestId(contestId);
			return new ResponseEntity<Object>(studentExists, HttpStatus.OK);
		}
	}

	@PostMapping("doSignInForAdmin")
	public ResponseEntity<Object> doLogin(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		
		HrDetails adminExistis = adminService.findByEmailAndPassword(email, password);
		if (adminExistis == null) {
			logger.error("email and password does not match");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and password does not match");
		} else {
			return new ResponseEntity<Object>(adminExistis, HttpStatus.OK);
		}
	}

	@PostMapping("adminRegistration")
	private ResponseEntity<Object> addHrDetails(@RequestBody HrDetails hrDetails) {
		HrDetails adminDetail = new HrDetails();
		try {
			HrDetails adminExists = adminService.findByEmail(hrDetails.getEmail());
			if (adminExists == null) {
				adminDetail.sethId(UUID.randomUUID().toString());
				adminDetail.setEmail(hrDetails.getEmail());
				adminDetail.sethName(hrDetails.gethName());
				adminDetail.sethNumber(hrDetails.gethNumber());
				adminDetail.setPassword(hrDetails.getPassword());
				adminService.saveHrDetails(adminDetail);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email Already Registered");
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Object>(adminDetail, HttpStatus.OK);
	}
}
