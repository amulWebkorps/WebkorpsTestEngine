package com.codecompiler.codecompilercontroller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.HrDetails;
import com.codecompiler.entity.Student;
import com.codecompiler.service.AdminService;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.StudentService1;

@Controller
public class UserController {

	@Autowired
	private StudentService1 studentService;

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private ContestService contestService;

	Logger logger = LogManager.getLogger(UserController.class);

	@GetMapping("doSignInForParticipator")
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

	@GetMapping("doSignInForAdmin")
	public ResponseEntity<Object> doLogin(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		
		HrDetails adminExistis = adminService.findByEmailAndPassword(email, password);
		if (adminExistis == null) {
			logger.error("email and password does not match");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and password does not match");
		} else {
			List<Contest> allContest = contestService.findAllContest();
			return generateResponseForAdmin(adminExistis, allContest, HttpStatus.OK);
		}
	}

	@PostMapping("adminRegistration")
	private ResponseEntity<Object> addHrDetails(@RequestBody HrDetails hrDetails) {
		try {
			HrDetails adminExists = adminService.findByEmail(hrDetails.getEmail());
			if (adminExists == null) {
				hrDetails.sethId(UUID.randomUUID().toString());
				adminService.saveHrDetails(hrDetails);
				logger.error("Admin details saved successfully");
			} else {
				logger.error("Email Already Registered");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email Already Registered");
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body("Admin registered successfully");
	}
	
	public ResponseEntity<Object> generateResponseForAdmin(HrDetails adminDetails, List<Contest> presentContest, HttpStatus status) {
		Map<String, Object> mp = new HashedMap();
		mp.put("adminDetails", adminDetails);
		mp.put("presentContest", presentContest);
		return new ResponseEntity<Object>(mp, status);
	}
}
