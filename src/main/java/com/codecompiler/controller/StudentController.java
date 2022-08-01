package com.codecompiler.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Student;
import com.codecompiler.helper.Helper;
import com.codecompiler.service.StudentService;

@Controller
public class StudentController {
	@Autowired
	private StudentService studentService;

	@Autowired
	private JavaMailSender jvms;

	// @RequestMapping("/student/upload")
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
		return "uploadParticipatorAndEmail";
	}

	@RequestMapping("/student")
	public List<Student> getAllStudents() {
		return this.studentService.getAllStudents();
	}

	@RequestMapping("/login")
	public ResponseEntity<?> doLogin(@RequestHeader String email, @RequestHeader String password) {

		Student s = studentService.findByEmailAndPassword(email, password);
		if (s == null) {
			System.out.println("email and password does not match");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and password does not match");
		} else {
			return ResponseEntity.ok(Map.of("message", s));
		}

	}

	@RequestMapping("/sendMail")
	public ResponseEntity<Object> sendMail(@RequestBody Student student) {
		try {
			SimpleMailMessage sms = new SimpleMailMessage();
			sms.setFrom("patilritika1995@gmail.com");
			sms.setTo(student.getEmail());
			sms.setSubject("application");
			sms.setText("hello dear");
			jvms.send(sms);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return generateResponse("mail sent to" + student.getEmail(), HttpStatus.OK, student);

	}

	public ResponseEntity<Object> generateResponse(String msg, HttpStatus st, Object response) {
		Map<String, Object> mp = new HashedMap();
		mp.put("message", msg);
		mp.put("status", st);
		mp.put("data", response);
		return new ResponseEntity<Object>(mp, st);
	}

}
