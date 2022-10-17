package com.codecompiler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.dto.QuestionStatusDTO;
import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.TestCases;
import com.codecompiler.exception.RecordMisMatchedException;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.QuestionService;

@SpringBootTest

public class QuestionServiceTest {

	@Autowired
	public QuestionService questionService;
	@Autowired
	private ContestService contestService;

	static Question question = new Question();
	Question savedQuestion;
	static Contest contest = new Contest();
	Contest afterSaveContest;

	@BeforeAll
	static void intailizeObject() {
		ArrayList<TestCaseDTO> sampleTestCase = new ArrayList<>();
		TestCaseDTO sampleTestCaseDTO = new TestCaseDTO();
		List<TestCases> testcases = new ArrayList<>();
		question.setQuestionId("");
		question.setQuestion("save question by initialize object method 26-09-22 ?");
		question.setContestLevel("Level 1");
		question.setQuestionStatus("true");
		question.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		sampleTestCaseDTO.setConstraints("Correct Syntax");
		sampleTestCaseDTO.setInput("save");
		sampleTestCaseDTO.setOutput("saved");
		sampleTestCase.add(sampleTestCaseDTO);
		question.setSampleTestCase(sampleTestCase);
		TestCases testCase = new TestCases();
		testCase.setId(0);
		testCase.setInput("save1");
		testCase.setOutput("saved1");
		testcases.add(testCase);
		TestCases testCase1 = new TestCases();
		testCase1.setId(1);
		testCase1.setInput("save2");
		testCase1.setOutput("saved2");
		testcases.add(testCase1);
		question.setTestcases(testcases);

		contest.setContestName("beforeAll");
		contest.setContestDescription("Testing for deployment");
		contest.setContestLevel("Level 1");
		contest.setContestTime("60");
		contest.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
	}

	@AfterEach
	void deletObject() {
		questionService.deleteQuestionForTestCase(savedQuestion);		
	}

	@Test
	public void saveQuestionSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);
		Assertions.assertNotNull(savedQuestion);
		Assertions.assertNotNull(savedQuestion.getQuestionId());
		Assertions.assertTrue(savedQuestion.getTestcases() != null);
		Assertions.assertTrue(savedQuestion.getQuestionStatus() == "true");
	}

	@Test
	public void saveQuestionFailureTest() {		
	Assertions.assertThrows(IllegalArgumentException.class, ()-> questionService.saveQuestion(null));	
	}
	
	@Test
	public void findByQuestionIdSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);
		Question getQuestion = questionService.findByQuestionId(savedQuestion.getQuestionId());
		Assertions.assertNotNull(getQuestion);
	}
	
	@Test
	public void findByQuestionIdFailureTest() {
		savedQuestion = questionService.saveQuestion(question);
		Assertions.assertThrows(NullPointerException.class, () -> questionService.findByQuestionId(null));
		Assertions.assertThrows(IllegalArgumentException.class, () -> questionService.findByQuestionId(""));
		Assertions.assertThrows(IllegalArgumentException.class, () -> questionService.findByQuestionId(" "));
		Assertions.assertThrows(RecordNotFoundException.class,
				() -> questionService.findByQuestionId(savedQuestion.getQuestionId() + "4567"));
	}

	@Test
	public void findByContestLevelSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);
		List<Question> questionList = questionService.findByContestLevel(savedQuestion.getContestLevel());
		Assertions.assertNotNull(questionList);
		Assertions.assertTrue(questionList.size() > 0);
		Assertions.assertTrue(!questionList.isEmpty());
	}
	
	@Test
	public void findByContestLevelFailureTest() {
		savedQuestion = questionService.saveQuestion(question);
		Assertions.assertThrows(NullPointerException.class, ()->questionService.findByContestLevel(null));		
		Assertions.assertThrows(IllegalArgumentException.class, ()->questionService.findByContestLevel(""));		
		Assertions.assertThrows(IllegalArgumentException.class, ()->questionService.findByContestLevel(" "));
	}

	@Test
	public void getTestCaseSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);
		List<TestCases> testCaseList = questionService.getTestCase(savedQuestion.getQuestionId());
		Assertions.assertNotNull(testCaseList);
		Assertions.assertTrue(testCaseList.size() > 0);
		Assertions.assertTrue(!testCaseList.isEmpty());
	}
	
	@Test
	
	public void getTestCaseFailureTest() {
		savedQuestion = questionService.saveQuestion(question);
		Assertions.assertThrows(NullPointerException.class, ()->questionService.getTestCase(null));		
		Assertions.assertThrows(IllegalArgumentException.class, ()->questionService.getTestCase(""));		
		Assertions.assertThrows(IllegalArgumentException.class, ()->questionService.findByContestLevel(" "));
		}	

	@Test
	public void getAllQuestionsSuccessTest() {
		Map<String, List<String>> questionIdList = new HashMap<>();
		afterSaveContest = contestService.saveContest(contest);
		savedQuestion = questionService.saveQuestion(question);
		List<String> idList = new ArrayList<>();
		List<String> contestList = new ArrayList<>();
		idList.add(savedQuestion.getQuestionId());
		contestList.add(afterSaveContest.getContestId());
		questionIdList.put("contestId", contestList);
		questionIdList.put("questionsIds", idList);
		List<Question> responseQuestion = questionService.getAllQuestions(questionIdList);
		Assertions.assertNotNull(responseQuestion);
		Assertions.assertTrue(responseQuestion.size() > 0);
		Assertions.assertTrue(!responseQuestion.isEmpty());
		contestService.deleteContest(afterSaveContest.getContestId());	
	}
	
	@Test
	public void getAllQuestionsFailureTest() {
		
		savedQuestion = questionService.saveQuestion(question);			
		List<String> idList = new ArrayList<>();
		Map<String, List<String>> questionIdList = new HashMap<>();
		List<String> contestList = new ArrayList<>();
		idList.add(savedQuestion.getQuestionId());
		contestList.add(0, null);
		questionIdList.put("contestId", contestList);
		questionIdList.put("questionsIds", idList);
		Assertions.assertThrows(NullPointerException.class, ()->questionService.getAllQuestions(null));
		Assertions.assertThrows(NullPointerException.class, ()->questionService.getAllQuestions(questionIdList));
		contestList.add(0, "");
		Assertions.assertThrows(NullPointerException.class, ()->questionService.getAllQuestions(questionIdList));
		contestList.add(0, " ");
		Assertions.assertThrows(NullPointerException.class, ()->questionService.getAllQuestions(questionIdList));
		questionIdList.remove("questionsIds");
		Assertions.assertThrows(NullPointerException.class, ()->questionService.getAllQuestions(questionIdList));
	}

	@Test
	public void saveQuestionOrContestSuccessTest() {
		QuestionStatusDTO questionStatusDTO = new QuestionStatusDTO();
		savedQuestion = questionService.saveQuestion(question);
		ArrayList<String> contestAndQuestionIdforLevel = new ArrayList<String>();
		contestAndQuestionIdforLevel.add("questionForLevel");
		contestAndQuestionIdforLevel.add(savedQuestion.getQuestionId());
		questionService.saveQuestionOrContest(contestAndQuestionIdforLevel);
		savedQuestion = questionService.findByQuestionId(savedQuestion.getQuestionId());
		String questionStatusFalse = questionService.findByQuestionId(savedQuestion.getQuestionId())
				.getQuestionStatus();
		question.setQuestionId("");
		afterSaveContest = contestService.saveContest(contest);
		question.setContestLevel(question.getContestLevel() + "@" + afterSaveContest.getContestId());
		Question savedQuestionForContest = questionService.saveQuestion(question);
		ArrayList<String> contestAndQuestionIdForContest = new ArrayList<String>();
		contestAndQuestionIdForContest.add(afterSaveContest.getContestId());
		contestAndQuestionIdForContest.add(savedQuestionForContest.getQuestionId());
		questionService.saveQuestionOrContest(contestAndQuestionIdForContest);
		for (QuestionStatusDTO temp : contestService.findByContestId(afterSaveContest.getContestId())
				.getQuestionStatus()) {
			if (temp.getQuestionId().equals(savedQuestionForContest.getQuestionId()))
				questionStatusDTO = temp;
		}
		Assertions.assertNotNull(savedQuestionForContest);
		Assertions.assertTrue(questionStatusDTO.getStatus() == false);
		Assertions.assertNotNull(savedQuestion);
		Assertions.assertEquals(questionStatusFalse, "false");
		questionService.deleteQuestionForTestCase(savedQuestionForContest);
		contestService.deleteContest(afterSaveContest.getContestId());
	}
	
	@Test
	public void saveQuestionOrContestFailureTest() {
		savedQuestion = questionService.saveQuestion(question);
		ArrayList<String> contestAndQuestionIdforLevel = new ArrayList<String>();
		contestAndQuestionIdforLevel.add("questionForLevel");
		Assertions.assertThrows(NullPointerException.class, ()->questionService.saveQuestionOrContest(null));		
		Assertions.assertThrows(NullPointerException.class, ()->questionService.saveQuestionOrContest(contestAndQuestionIdforLevel));
	}

	@Test
	public void filterQuestionSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);	
		List<Question> questionsBasedOnLevel = questionService.filterQuestion(savedQuestion.getContestLevel());	
		Question question = null;
		for(Question que : questionsBasedOnLevel) {
			if(que.getQuestionId().equals(savedQuestion.getQuestionId())){	
				question = que;
			break; 
			}
		}
		Assertions.assertNotNull(savedQuestion);
		Assertions.assertNotNull(question);
		Assertions.assertTrue(questionsBasedOnLevel.size() > 0);
	}
	
	@Test
	public void filterQuestionFailureTest() {
		savedQuestion = questionService.saveQuestion(question);	
		Assertions.assertThrows(NullPointerException.class, ()->questionService.filterQuestion(null));
		Assertions.assertThrows(NullPointerException.class, ()->questionService.filterQuestion(""));	
		Assertions.assertThrows(NullPointerException.class, ()->questionService.filterQuestion(" "));
			}
	
	@Test
	public void findAllQuestionSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);	
		List<Question> questionsList = questionService.findAllQuestion();
		Assertions.assertNotNull(savedQuestion);
		Assertions.assertNotNull(questionsList);
		Assertions.assertTrue(questionsList.size() > 0);
	}
	
}
