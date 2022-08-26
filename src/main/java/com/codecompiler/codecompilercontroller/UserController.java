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
import com.codecompiler.entity.JwtResponse;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.service.AdminService;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService1;
import com.codecompiler.service.StudentService1;
import com.codecompiler.service.impl.UserDetailServiceImpl;
import com.codecompiler.util.JwtUtil;

@Controller
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	private StudentService1 studentService;

	@Autowired
	private AdminService adminService;
	
@Autowired
private UserDetailServiceImpl userDetailServiceImpl;
	
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

	Logger logger = LogManager.getLogger(UserController.class);

	@GetMapping("/public/signin")
	public ResponseEntity<Object> doSignIn(@RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("contestId") String contestId) {

		Authentication authObj ;
		Student studentExists = null;
		try{
    authObj =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
	System.out.println("UserController.doLogin()");
	System.out.println("email = "+authObj.getName());
	 studentExists = studentService.findByEmailAndPassword(email, password);
	studentExists.setContestId(contestId);
}catch (BadCredentialsException e){
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email and password does not match");
}
    String token = jwtUtil.generateToken(authObj.getName());
    System.out.println(token);
    HashMap<String, Object> hm = new HashMap<>();
    hm.put("token", token);
    hm.put("student", studentExists);
return new ResponseEntity<Object>(hm, HttpStatus.OK);
	}

	@GetMapping("public/admin/signin")
	public ResponseEntity<Object> doLogin(@RequestParam("email") String email,
			@RequestParam("password") String password) {
<<<<<<< HEAD
		Authentication authObj ;
		try{
    authObj =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
	System.out.println("UserController.doLogin()");
	System.out.println("email = "+authObj.getName());
}catch (BadCredentialsException e){
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("email and password does not match");
}
    String token = jwtUtil.generateToken(authObj.getName());
    System.out.println(token);
    List<Contest> allContest = contestService.findAllContest();
    return generateResponseForAdmin( allContest,token, HttpStatus.OK);
=======
		
		HrDetails adminExistis = adminService.findByEmailAndPassword(email.toLowerCase(), password);
		if (adminExistis == null) {
			logger.error("email and password does not match");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and password does not match");
		} else {
			List<Contest> allContest = contestService.findAllContest();
			return generateResponseForAdmin("Admin registration successfully", allContest, HttpStatus.OK);
		}
>>>>>>> 810a50582b27e785fbe6f37be79e7d27dad4f826
	}

	@PostMapping("/public/adminRegistration")
	private ResponseEntity<Object> addHrDetails(@RequestBody HrDetails hrDetails) {
		try {
			HrDetails adminExists = adminService.findByEmail(hrDetails.getEmail().toLowerCase());
			if (adminExists == null) {
				hrDetails.sethId(UUID.randomUUID().toString());
<<<<<<< HEAD
				hrDetails.setRole("ROLE_ADMIN");
=======
				hrDetails.setEmail(hrDetails.getEmail().toLowerCase());
>>>>>>> 810a50582b27e785fbe6f37be79e7d27dad4f826
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
	
<<<<<<< HEAD
	public ResponseEntity<Object> generateResponseForAdmin( List<Contest> presentContest,String token, HttpStatus status) {
		Map<String, Object> mp = new HashedMap<>();
=======
	public ResponseEntity<Object> generateResponseForAdmin(String successMessage, List<Contest> presentContest, HttpStatus status) {
		Map<String, Object> mp = new HashedMap<>();
		mp.put("successMessage", successMessage);
>>>>>>> 810a50582b27e785fbe6f37be79e7d27dad4f826
		mp.put("presentContest", presentContest);
		mp.put("token", token);
		return new ResponseEntity<Object>(mp, status);
	}
	
	@GetMapping("/admin/participatorOfContest")
	public ResponseEntity<Object> viewParticipators(@RequestParam String contestId) {
		List<Student> studentTemp = new ArrayList<>();
		try {		
			studentTemp = studentService.findByContestId(contestId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(studentTemp, HttpStatus.OK);
	}
	
	@GetMapping("getparticipatordetail")
	public ResponseEntity<Object> getparticipatordetail(@RequestParam String studentId) {
		Map<String, Object> mp = new HashedMap<>();
		try {
		Student student = studentService.findById(studentId);
		if(student != null) {
		List<Question> questionDetail = new ArrayList<>();
		for(String questionId : student.getQuestionId()) {
			Question question = questionService.findByQuestionId(questionId);
			Question questionTemp = new Question();
			questionTemp.setQuestionId(question.getQuestionId());
			questionTemp.setQuestion(question.getQuestion());
			questionTemp.setSampleTestCase(question.getSampleTestCase());
			questionDetail.add(questionTemp);
		}
		mp.put("studentDetail", student);
		mp.put("questionSubmitedByStudent", questionDetail);
		}else {
			return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This student not present in DataBase");	
		}		
		}catch(Exception e) {
			e.printStackTrace();
			return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Student Id");
        }
		return new ResponseEntity<Object>(mp, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/admin/studentUpload", headers = "content-type=multipart/*")
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
	
	@DeleteMapping("/admin/deletestudent")
	private ResponseEntity<Object> deleteStudent(@RequestParam String emailId) {	
		try {
			 studentService.deleteByEmail(emailId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Email Id");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Student deleted successfully");

	}
	
	@GetMapping("/admin/filterparticipator")
	private ResponseEntity<Object> filterParticipator(@RequestParam String filterByString) {			
		List<String> studentTemp = new ArrayList<>();
		try {						
			studentTemp = studentService.getByEmail(filterByString);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Email Id");
		}
		return new ResponseEntity<Object>(studentTemp, HttpStatus.OK);

	}
	
	@DeleteMapping("finalsubmitcontest")
	public  ResponseEntity<Object> finalSubmitContest(@RequestParam String emailId) {
		try {
			 studentService.finalSubmitContest(emailId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check EmailId");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Test submitted successfully");
	}
	
	@GetMapping("getAllParticipator")
	public ResponseEntity<Object> getAllParticipator() {
		List<Student> allParticipator = new ArrayList<>();
		try {
			if (!allParticipator.isEmpty()) {
				allParticipator = studentService.findAll();
				return new ResponseEntity<Object>(allParticipator, HttpStatus.OK);
			} else
				return new ResponseEntity<Object>("No Participator is in active state", HttpStatus.CONFLICT);
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.CONFLICT).body("No Participator is in active state");
		}
		
	}
}









