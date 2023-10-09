package com.codecompiler.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.JwtResponseDTO;
import com.codecompiler.dto.ParticipantDTO;
import com.codecompiler.dto.StudentFinalResponse;
import com.codecompiler.entity.Student;
import com.codecompiler.reponse.ResponseHandler;
import com.codecompiler.service.ContestService;
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
	private AuthenticationManager authenticationManager;

	@Autowired
	private ContestService contestService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("public/doSignInForParticipator")
	public ResponseEntity<Object> doSignIn(@RequestBody Student student, @RequestParam("contestId") String contestId) {
		log.info("doSignIn:: Started : " + contestId);

		Authentication authObj = this.authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(student.getEmail().toLowerCase(), student.getPassword()));
		Student studentExists = this.studentService.findByEmailAndPassword(student.getEmail().toLowerCase(),
				student.getPassword());
		String contestType = contestService.findContestTypeByContestId(contestId);
		JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
		studentExists.setContestId(contestId);
		studentExists.setContestType(contestType);
		jwtResponseDTO.setToken(this.jwtUtil.generateToken(authObj.getName()));
		jwtResponseDTO.setStudent(studentExists);
		log.info("doSignIn:: Particepant authenticate successfully :" + jwtResponseDTO);
		log.info("doCome:: Particepant authenticate successfully :" + jwtResponseDTO);
		return ResponseHandler.generateResponse("success", HttpStatus.OK, jwtResponseDTO);

	}

	@GetMapping("admin/getParticipatorDetail")
	public ResponseEntity<Object> getParticipatorDetail(@RequestParam String studentId) throws IOException {
		log.info("getParticipatorDetail started studentId :: " + studentId);
		Map<String, Object> participatorDetail = this.studentService.getParticipatorDetail(studentId);
		log.info("getParticipatorDetail:: participatorDetail : " + participatorDetail.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, participatorDetail);

	}

	@GetMapping("admin/contest/result")
	public ResponseEntity<Object> evaluateStudentResult(@RequestParam String contestId) {
		log.info("viewParticipators:: started with contestId: " + contestId);
//		List<StudentDTO> studentDetails = this.studentService.findByContestId(contestId); this is just commented, Functionality is still in code as per requirement you can use
		List<StudentFinalResponse> studentTestDetails = this.studentService.evaluateStudentTestResult(contestId);
		log.info("viewParticipators:: studentDetails fetch successfully: " + studentTestDetails.toString());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, studentTestDetails);

	}

	@PostMapping(value = "admin/studentUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file) {
		log.info("upload:: Started ");
		List<String> allStudents = this.studentService.saveFileForBulkParticipator(file);
		log.info("upload:: File bulk students are saved succesfully : " + allStudents.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, allStudents);

	}

	@GetMapping("admin/getAllParticipator")
	public ResponseEntity<Object> getAllParticipator() {
		log.info("getAllParticipator:: started");
		List<String> allParticipator = this.studentService.findAll();
		log.info("getAllParticipator:: participators found successfully" + allParticipator.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, allParticipator);

	}

	@DeleteMapping("admin/deleteStudent")
	private ResponseEntity<Object> deleteStudent(@RequestParam String emailId) {
		log.info("deleteStudent:: has started with an email id: " + emailId);
		this.studentService.deleteByEmail(emailId);
		log.info("Student Deleted Successfully");
		return ResponseHandler.generateResponse("success", HttpStatus.OK, "Student Deleted Successfully");

	}

	@GetMapping("admin/filterParticipants")
	public ResponseEntity<Object> filterParticipants(@RequestParam String filterByString) {
		log.info("filterParticipants: started filterByString = " + filterByString);
		List<String> totalParticipantsByFilter = studentService.filterParticipants(filterByString);
		log.info("filterParticipants:: totalparticipantsByFilter size : " + totalParticipantsByFilter.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, totalParticipantsByFilter);

	}

	@GetMapping("admin/participatorOfMCQContest")
	public ResponseEntity<Object> viewMCQParticipators(@RequestParam String contestId) {
		log.info("viewMCQParticipators:: started with contestId: " + contestId);
		List<ParticipantDTO> studentDetails = this.studentService.findByContestIdForMCQ(contestId);
		log.info("viewMCQParticipators:: studentDetails fetch successfully: " + studentDetails.toString());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, studentDetails);

	}

	@GetMapping("/admin/participatorOfContest")
	public ResponseEntity<?> participatorOfContest(@RequestParam("contestId") String contestId) {
		log.info("participatorOfContest:: started with contestId: " + contestId);
		List<ParticipantDTO> studentDetails = this.studentService.findByContestIdForProgramming(contestId);
		log.info("participatorOfContest:: studentDetails fetch successfully: " + studentDetails.toString());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, studentDetails);

	}

}
