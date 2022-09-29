package com.codecompiler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Student;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.EmailService;
import com.codecompiler.service.StudentService;
import com.codecompiler.service.impl.EmailServiceImpl;

@SpringBootTest
public class EmailServiceTest {

	@Value("${application.base-url}")
	private String baseUrl;

	@Autowired
	private StudentService studentService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ContestService contestService;

	public static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);

	static Student student = new Student();
	Student savedStudent;

	static Contest contest = new Contest();
	Contest afterSaveContest;

	@BeforeAll
	static void intailizeObject() {
		String studentId = UUID.randomUUID().toString();
		String characters = "ABCDEFGHLMNOPQRSTUVWXYZabcdghijklmnopqrstuvwxyz0123456789@#$*";
		String pwd = RandomStringUtils.random(7, characters);
		student.setId(studentId);
		student.setName("Shovit Rai");
		student.setEmail("Shivangi@webkorps.com");
		student.setMobileNumber("9287857856");
		student.setContestLevel("Level 2");
		student.setPassword(pwd);
		student.setRole("ROLE_STUDENT");
		
		contest.setContestName("beforeAll");
		contest.setContestDescription("Testing for deployment");
		contest.setContestLevel("Level 1");
		contest.setContestTime("60");
		contest.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));	
	}

	@Test
	public void sendMailSuccessTest() {
		savedStudent = studentService.saveStudent(student);
		afterSaveContest = contestService.saveContest(contest);
		emailService.sendMail(afterSaveContest.getContestId(), savedStudent.getName(), savedStudent.getEmail(), "For test cases", savedStudent.getPassword());
	}
}
