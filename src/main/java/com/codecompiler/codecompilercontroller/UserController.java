package com.codecompiler.codecompilercontroller;

import java.util.ArrayList;
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
import com.mongodb.BasicDBObject;

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
	private ExcelConvertorService excelConvertorService;

	@Autowired
	private QuestionService1 questionService;	

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
	
	@GetMapping("viewParticipatorOfContest")
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
	
	
	@PostMapping(value = "/studentUpload", headers = "content-type=multipart/*")
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
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please check excel file format");
		}
	}
	
	@DeleteMapping("deletestudent")
	private ResponseEntity<Object> deleteStudent(@RequestParam String emailId) {	
		try {
			 studentService.deleteByEmail(emailId);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Email Id");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Student deleted successfully");

	}
	
	@GetMapping("filterparticipator")
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
}









