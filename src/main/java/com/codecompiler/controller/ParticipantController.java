package com.codecompiler.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.reponse.ResponseHandler;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.StudentService;
import com.codecompiler.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(originPatterns = "*")
@Slf4j
public class ParticipantController {

	@Autowired
	private StudentService studentService;
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private ExcelConvertorService excelConvertorService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@GetMapping("public/doSignInForParticipator")
	public ResponseEntity<Object> doSignIn(@RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("contestId") String contestId) {
		
		Authentication authObj;
		Student studentExists = null;
		try {
			authObj = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			studentExists = studentService.findByEmailAndPassword(email, password);
			studentExists.setContestId(contestId);
		} catch (BadCredentialsException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email and password does not match");
		}
		String token = jwtUtil.generateToken(authObj.getName());
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("token", token);
		hm.put("student", studentExists);
		return new ResponseEntity<Object>(hm, HttpStatus.OK);
	}
	
	@GetMapping("getParticipatorDetail")
	public ResponseEntity<Object> getParticipatorDetail(@RequestParam String studentId) {
		log.info("getParticipatorDetail started studentId :: "+studentId);
		try {
			Map<String, Object> mp = this.studentService.getParticipatorDetail(studentId);
			return ResponseHandler.generateResponse("success", HttpStatus.OK, mp);
		} 
		catch (Exception e) {
			log.error("Exception occured in getParticipatorDetail :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@GetMapping("admin/participatorOfContest")
	public ResponseEntity<Object> viewParticipators(@RequestParam String contestId) {
		List<Student> studentTemp = new ArrayList<>();
		List<Student> studentTempFormat = new ArrayList<>();			
		try {
			studentTemp = studentService.findByContestId(contestId);
			for(Student student : studentTemp){
				Student studentFormat = new Student();
				studentFormat.setId(student.getId());
				studentFormat.setEmail(student.getEmail());
				studentTempFormat.add(studentFormat);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(studentTempFormat, HttpStatus.OK);
	}
	

	@PostMapping(value = "admin/studentUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file) {
		if (excelConvertorService.checkExcelFormat(file)) {
			try {
				List<String> allStudents = studentService.saveFileForBulkParticipator(file);
				return new ResponseEntity<Object>(allStudents, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Excel not uploaded");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Please check excel file format");
		}
	}
	
	@GetMapping("admin/getAllParticipator")
	public ResponseEntity<Object> getAllParticipator() {
		List<String> allParticipator =  studentService.findAll();
		try {
			if (!allParticipator.isEmpty()) {
				return new ResponseEntity<Object>(allParticipator, HttpStatus.OK);
			} else
				return new ResponseEntity<Object>("No Participator is in active state", HttpStatus.CONFLICT);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).body("No Participator is in active state");
		}

	}
	
	@DeleteMapping("finalSubmitContest")
	public  ResponseEntity<Object> submitContest(@RequestParam String emailId) {
		try {
			 studentService.finalSubmitContest(emailId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check EmailId");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Test submitted successfully");
	}
	
	@DeleteMapping("admin/deleteStudent")
	private ResponseEntity<Object> deleteStudent(@RequestParam String emailId) {
		try {
			studentService.deleteByEmail(emailId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Email Id");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Student deleted successfully");

	}
	

}
