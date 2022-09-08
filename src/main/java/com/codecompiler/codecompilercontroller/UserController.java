package com.codecompiler.codecompilercontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.HrDetails;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.service.AdminService;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService1;
import com.codecompiler.service.StudentService1;
import com.codecompiler.util.JwtUtil;

@Controller
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	private StudentService1 studentService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ContestService contestService;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private ExcelConvertorService excelConvertorService;

	@Autowired
	private QuestionService1 questionService;

	public static final Logger logger = LogManager.getLogger(UserController.class);

	@GetMapping("public/doSignInForParticipator")
	public ResponseEntity<Object> doSignIn(@RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("contestId") String contestId) {
		logger.info("Sign In for participator: started");
		Authentication authObj;
		Student studentExists = null;
		try {
			authObj = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

			studentExists = studentService.findByEmailAndPassword(email, password);
			studentExists.setContestId(contestId);
		} catch (BadCredentialsException e) {
			logger.error("Unauthorized: Bad Credentials "+e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email and password does not match");
		}
		String token = jwtUtil.generateToken(authObj.getName());
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("token", token);
		hm.put("student", studentExists);
		logger.info("Sign In for participator: ended");
		return new ResponseEntity<Object>(hm, HttpStatus.OK);
	}

	@GetMapping("public/admin/signIn")
	public ResponseEntity<Object> doLogin(@RequestParam("email") String email,
			@RequestParam("password") String password) {
		logger.info("Sign In for Admin: started");
		Authentication authObj;
		try {
			authObj = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email.toLowerCase(), password));
		} catch (BadCredentialsException e) {
			logger.error("Unauthorized: Bad Credentials "+e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email and password does not match");
		}
		String token = jwtUtil.generateToken(authObj.getName());
		List<Contest> allContest = contestService.findAllContest();
		logger.info("Sign In for Admin: ended");
		return generateResponseForAdmin(allContest, token, HttpStatus.OK);
	}

	@PostMapping("public/adminRegistration")
	private ResponseEntity<Object> addHrDetails(@RequestBody HrDetails hrDetails) {
		try {
			logger.info("Sign up for Admin: started");
			HrDetails adminExists = adminService.findByEmail(hrDetails.getEmail().toLowerCase());
			if (adminExists == null) {
				hrDetails.sethId(UUID.randomUUID().toString());
				hrDetails.setEmail(hrDetails.getEmail().toLowerCase());
				hrDetails.setRole("ROLE_ADMIN");
				adminService.saveHrDetails(hrDetails);
				logger.info("Admin details saved successfully");
			} else {
				logger.error("Email Already Registered");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email Already Registered");
			}
		} catch (Exception e) {
			logger.error("Bad Request: "+e.getMessage());
		}
		logger.info("Sign up for Admin: ended");
		return ResponseEntity.status(HttpStatus.OK).body("Admin registered successfully");
	}

	public ResponseEntity<Object> generateResponseForAdmin(List<Contest> presentContest, String token,
			HttpStatus status) {
		Map<String, Object> mp = new HashedMap<>();
		mp.put("presentContest", presentContest);
		mp.put("token", token);
		return new ResponseEntity<Object>(mp, status);
	}

	@GetMapping("admin/participatorOfContest")
	public ResponseEntity<Object> viewParticipators(@RequestParam String contestId) {
		List<Student> studentTemp = new ArrayList<>();
		List<Student> studentTempFormat = new ArrayList<>();
		logger.info("get participator of contest: started");
		try {
			studentTemp = studentService.findByContestId(contestId);
			for(Student student : studentTemp){
				Student studentFormat = new Student();
				studentFormat.setId(student.getId());
				studentFormat.setEmail(student.getEmail());
				studentTempFormat.add(studentFormat);
			}	
		} catch (Exception e) {
			logger.error("Object is null "+e.getMessage());
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		logger.info("get participator of contest: ended");
		return new ResponseEntity<Object>(studentTempFormat, HttpStatus.OK);
	}

	@GetMapping("admin/getParticipatorDetail")
	public ResponseEntity<Object> getparticipatordetail(@RequestParam String studentId) {
		logger.info("getParticipatorDetail: started");
		Map<String, Object> mp = new HashedMap<>();
		try {
			Student student = studentService.findById(studentId);
			if (student != null && student.getQuestionId() != null) {
				List<Question> questionDetail = new ArrayList<>();
				for (String questionId : student.getQuestionId()) {
					Question question = questionService.findByQuestionId(questionId);
					Question questionTemp = new Question();
					questionTemp.setQuestionId(question.getQuestionId());
					questionTemp.setQuestion(question.getQuestion());
					questionTemp.setSampleTestCase(question.getSampleTestCase());
					questionDetail.add(questionTemp);
				}
				mp.put("studentDetail", student);
				mp.put("questionSubmitedByStudent", questionDetail);
			} else {
				logger.error("Bad request: This student did not submit a single Question");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This student did not submit a single Question");
			}
		} catch (Exception e) {
			logger.error("Internal Server Error "+e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Student Id");
		}
		logger.info("getParticipatorDetail: ended");
		return new ResponseEntity<Object>(mp, HttpStatus.OK);
	}

	@PostMapping(value = "admin/studentUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file) {
		logger.info("bulkStudentUpload: started");
		if (excelConvertorService.checkExcelFormat(file)) {
			try {
				List<String> allStudents = studentService.saveFileForBulkParticipator(file);
				logger.info("bulkStudentUpload: Saved");
				return new ResponseEntity<Object>(allStudents, HttpStatus.OK);
			} catch (Exception e) {
				logger.error("Internal Server Error "+e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Excel not uploaded");
			}
		} else {
			logger.error("UnSupported Media Type: Please check excel file format");
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Please check excel file format");
		}
	}

	@DeleteMapping("admin/deleteStudent")
	private ResponseEntity<Object> deleteStudent(@RequestParam String emailId) {
		logger.info("deleteStudent: started");
		try {
			studentService.deleteByEmail(emailId);
		} catch (Exception e) {
			logger.error("Internal Server Error "+e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Email Id");
		}
		logger.info("deleteStudent: ended");
		return ResponseEntity.status(HttpStatus.OK).body("Student deleted successfully");
	}
	
	@DeleteMapping("admin/finalSubmitContest")
	public  ResponseEntity<Object> submitContest(@RequestParam String emailId) {
		logger.info("finalSubmitContest: started");
		try {
			 studentService.finalSubmitContest(emailId);
		} catch (Exception e) {
			logger.error("Internal Server Error "+e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check EmailId");
		}
		logger.info("finalSubmitContest: ended");
		return ResponseEntity.status(HttpStatus.OK).body("Test submitted successfully");
	}

	@GetMapping("admin/getAllParticipator")
	public ResponseEntity<Object> getAllParticipator() {
		logger.info("getAllParticipator: started");
		List<String> allParticipator =  studentService.findAll();
		try {
			if (!allParticipator.isEmpty()) {
				return new ResponseEntity<Object>(allParticipator, HttpStatus.OK);
			} else
				return new ResponseEntity<Object>("No Participator is in active state", HttpStatus.CONFLICT);
		} catch (Exception ex) {
			logger.error("No Participator is in active state "+ex.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body("No Participator is in active state");
		}
	}
}
