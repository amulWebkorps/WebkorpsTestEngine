package com.codecompiler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Student;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.StudentService;

@SpringBootTest

public class ContestServiceTest {
	
	@Autowired
	private ContestService contestService;

	@Autowired
	private StudentService studentService;
	
	static Contest contest = new Contest();
    Contest afterSaveContest;
	
    static Student student = new Student();
	Student savedStudent;
    
	@BeforeAll
	static void intailizeObject() {
		contest.setContestName("beforeAll");
		contest.setContestDescription("Testing for deployment");
		contest.setContestLevel("Level 1");
		contest.setContestTime("60");
		contest.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));	
		
		student.setName("Sarthak Kothari");
		student.setEmail("Sarthak@mail.com");
		student.setMobileNumber("9754635882");
		student.setContestLevel("Level 2");
		student.setStatus(false);
		String characters = "ABCDEFGHLMNOPQRSTUVWXYZabcdghijklmnopqrstuvwxyz0123456789@#$*";
		String pwd = RandomStringUtils.random(7, characters);
		student.setPassword(pwd);
		student.setParticipateDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

	}
	
	@AfterEach
	void deletObject() {
		contestService.deleteContest(afterSaveContest.getContestId());
	}
	
	@Test
	public void saveContestSuccessTest() {
		afterSaveContest = contestService.saveContest(contest);
		Assertions.assertNotNull(afterSaveContest);
		Assertions.assertTrue(afterSaveContest.getContestId() != null);
	}
	
	@Test
	public void saveContestFailureTest() {
		afterSaveContest = contestService.saveContest(contest);		
		Assertions.assertFalse(afterSaveContest.getContestTime().isEmpty());
		Assertions.assertThrows(IllegalArgumentException.class, ()-> contestService.saveContest(null));
	}
	
	@Test
	public void getContestDetailSuccessTest() {
		afterSaveContest = contestService.saveContest(contest);
		Map map = contestService.getContestDetail(afterSaveContest.getContestId(), afterSaveContest.getContestType());
		Assertions.assertNotNull(map);
		Assertions.assertTrue(map.get("contest") != null);
		Assertions.assertTrue(map.get("totalAvailableQuestion") != null);
	}
	
	@Test
	public void getContestDetailFailureTest() {
		afterSaveContest = contestService.saveContest(contest);	
		Assertions.assertThrows(NullPointerException.class, ()->contestService.getContestDetail(null, null));		
		Assertions.assertThrows(IllegalArgumentException.class, ()->contestService.getContestDetail("", ""));		
		Assertions.assertThrows(IllegalArgumentException.class, ()->contestService.getContestDetail(" ", " "));		
	}
	
	@Test
	public void findByContestIdSuccessTest() {
		afterSaveContest = contestService.saveContest(contest);
		Contest getContest = contestService.findByContestId(afterSaveContest.getContestId());
		Assertions.assertNotNull(getContest);
		Assertions.assertEquals(afterSaveContest.getContestId(), getContest.getContestId());	
	}
	
	@Test
	public void findByContestIdFailureTest() {
		afterSaveContest = contestService.saveContest(contest);
		Assertions.assertThrows(NullPointerException.class, ()-> contestService.findByContestId(null));
		Assertions.assertThrows(IllegalArgumentException.class, ()-> contestService.findByContestId(""));
		Assertions.assertThrows(IllegalArgumentException.class, ()-> contestService.findByContestId(" "));
		Assertions.assertThrows(RecordNotFoundException.class, ()-> contestService.findByContestId(afterSaveContest.getContestId()+"-4tfed4"));
	}
	
	@Test
	public void deleteContestSuccessTest() {
		afterSaveContest = contestService.saveContest(contest);
		Contest getContest = contestService.findByContestId(afterSaveContest.getContestId());		
		contestService.deleteContest(afterSaveContest.getContestId());
			Assertions.assertNotNull(getContest);
	}
	
	@Test
	public void deleteContestFailureTest() {	
		afterSaveContest = contestService.saveContest(contest);
		Contest beforeDelete = contestService.findByContestId(afterSaveContest.getContestId());
		Assertions.assertNotNull(beforeDelete);
		contestService.deleteContest(afterSaveContest.getContestId());	
		Assertions.assertThrows(RecordNotFoundException.class, ()->contestService.findByContestId(afterSaveContest.getContestId()));
		Assertions.assertThrows(IllegalArgumentException.class, ()-> contestService.deleteContest(""));
		Assertions.assertThrows(IllegalArgumentException.class, ()-> contestService.deleteContest(" "));
		Assertions.assertThrows(NullPointerException.class, ()-> contestService.deleteContest(null)); 
    }
	
	@Test
	public void findAllContestSuccessTest() {
		afterSaveContest = contestService.saveContest(contest);	
		List<Contest> allContest = contestService.findAllContest();
		Assertions.assertNotNull(allContest);
		Assertions.assertTrue(allContest.size() > 0);
	}
	
	
//	@Test
//	public void findAllContestFailureTest() {
//		afterSaveContest = contestService.saveContest(contest);	
//		List<Contest> allContest = contestService.findAllContest();		
//	}
	
//	@Test
//	public void contestPageSuccessTest() {
//		afterSaveContest = contestService.saveContest(contest);	
//		savedStudent = studentService.saveStudent(student);
//		Map<String, Object> mp = contestService.contestPage(afterSaveContest.getContestId(), savedStudent.getId(), "Java");
//		studentService.deleteByEmail(savedStudent.getEmail());
//	}
	
	
	
	
	
	
	
	

	
}
