package com.codecompiler.controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.JwtResponseDTO;
import com.codecompiler.dto.StudentDTO;
import com.codecompiler.entity.Student;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.UnSupportedFormatException;
import com.codecompiler.reponse.ResponseHandler;
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
	private JwtUtil jwtUtil;

	@PostMapping("public/doSignInForParticipator")
	public ResponseEntity<Object> doSignIn(@RequestBody Student student ,@RequestParam("contestId") String contestId) {
		log.info("doSignIn:: Started : "+contestId);
		try {
			Authentication authObj = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(student.getEmail().toLowerCase(), student.getPassword()));
			Student studentExists  = this.studentService.findByEmailAndPassword(student.getEmail().toLowerCase(), student.getPassword());
			JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
			studentExists.setContestId(contestId);
			jwtResponseDTO.setToken(this.jwtUtil.generateToken(authObj.getName()));
			jwtResponseDTO.setStudent(studentExists);
			log.info("doSignIn:: Particepant authenticate successfully :"+jwtResponseDTO);
			return ResponseHandler.generateResponse("success", HttpStatus.OK, jwtResponseDTO);
		} catch (BadCredentialsException e) {
			log.info("Exception occurs in doSignIn: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.UNAUTHORIZED,"email and password does not match");
		}
	}

	@GetMapping("admin/getParticipatorDetail")
	public ResponseEntity<Object> getParticipatorDetail(@RequestParam String studentId) {
		log.info("getParticipatorDetail started studentId :: "+studentId);
		try {
			Map<String, Object> participatorDetail = this.studentService.getParticipatorDetail(studentId);
			log.info("getParticipatorDetail:: participatorDetail : "+participatorDetail.size());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, participatorDetail);
		} 
		catch (Exception e) {
			log.error("Exception occured in getParticipatorDetail :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("admin/participatorOfContest")
	public ResponseEntity<Object> viewParticipators(@RequestParam String contestId) {			
		log.info("viewParticipators:: started with contestId: " + contestId);
		try {
			List<StudentDTO> studentDetails = this.studentService.findByContestId(contestId);
			log.info("viewParticipators:: studentDetials fetch successfully: "+studentDetails.toString());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, studentDetails);
		} catch (Exception e) {
			log.error("viewParticipators:: Exception occured in viewParticipators :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}


	@PostMapping(value = "admin/studentUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file) {
		log.info("upload:: Started ");
		try {
			List<String> allStudents = this.studentService.saveFileForBulkParticipator(file);
			log.info("upload:: File bulk students are saved succesfully : "+allStudents.size());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, allStudents);
		}catch (UnSupportedFormatException e) {
			log.error("Exception occured in upload :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
		}
		catch (Exception e) {
			log.error("Exception occured in upload :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("admin/getAllParticipator")
	public ResponseEntity<Object> getAllParticipator() {
		log.info("getAllParticipator:: started");
		try {
			List<String> allParticipator =  this.studentService.findAll();
			log.info("getAllParticipator:: participators found successfully"+allParticipator.size());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, allParticipator);
		}
		catch (RecordNotFoundException ex) {
			log.error("Exception occured in getAllParticipator :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.NOT_FOUND, ex.getMessage());
		}
		catch (Exception ex) {
			//log.error("Exception occured in getAllParticipator :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}

	}

	@DeleteMapping("admin/deleteStudent")
	private ResponseEntity<Object> deleteStudent(@RequestParam String emailId) {
		log.info("deleteStudent:: has started with an email id: " + emailId);
		try {
			this.studentService.deleteByEmail(emailId);
			log.info("Student Deleted Successfully");
			return ResponseHandler.generateResponse("error", HttpStatus.OK, "Student Deleted Successfully");
		}
		catch (Exception ex) {
			log.error("Exception occured in deleteStudent :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}

	}

}
