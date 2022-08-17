package com.codecompiler.controller;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.codecompiler.entity.Student;
import com.codecompiler.helper.Helper;
import com.codecompiler.service.EmailService;
import com.codecompiler.service.StudentService;

//@Controller
public class StudentController {

	@Autowired
	private StudentService studentService;

	@Autowired
	private EmailService emailService;

	Logger logger = LogManager.getLogger(StudentController.class);

	@RequestMapping(value = "/student/upload", headers = "content-type=multipart/*", method = RequestMethod.POST)
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
		if (Helper.checkExcelFormat(file)) {
			// true
			try {
				this.studentService.save(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ResponseEntity.ok(Map.of("message", "File is uploaded and data is saved to db"));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file ");
	}

	@RequestMapping("/fileUploaded")
	public String upload(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		return "uploadParticipatorAndEmail";
	}

	@RequestMapping("/getAllstudent")
	public String getAllStudents(Model model) {

		model.addAttribute("students", studentService.getAllStudents());
		return "uploadParticipatorAndEmail";
	}

	@RequestMapping("/dologin")
	public ResponseEntity<?> doLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam("contestId") String contestId) {

		Student studentExists = studentService.findByEmailAndPassword(email, password);
		if (studentExists == null) {
			logger.error("email and password does not match");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and password does not match");
		} else {
			studentExists.setContestId(contestId);
			return ResponseEntity.ok(studentExists);
		}
	}

	@RequestMapping("loginpage")
	public ModelAndView doLogin(@RequestParam("id") String id, @RequestParam("contestId") String contestId) {
		Student studentRecord = studentService.findById(id);
		studentRecord.setContestId(contestId);
		ModelAndView mv = new ModelAndView("startContest", "studentRecord", studentRecord);
		return mv;
	}

	/*
	 * @PostMapping("/sendMail") public ResponseEntity<Object> sendMail(@RequestBody
	 * Student student) { try { this.emailService.sendMail(student.getContestId(),
	 * student.getName(), student.getEmail(),"Webkorps Code Assesment Credentials",
	 * student.getPassword()); } catch (Exception e) { e.printStackTrace(); } return
	 * generateResponse("mail sent to" + student.getEmail(), HttpStatus.OK,
	 * student);
	 * 
	 * }
	 * 
	 * public ResponseEntity<Object> generateResponse(String msg, HttpStatus st,
	 * Object response) { Map<String, Object> mp = new HashedMap();
	 * mp.put("message", msg); mp.put("status", st); mp.put("data", response);
	 * return new ResponseEntity<Object>(mp, st); }
	 */
}
