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

import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.TestCases;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.SavedQuestionStatusFalseException;
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
		question.setQuestion("save question by initialize object method ?");
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
		questionService.deleteQuestionTestCase(savedQuestion);
		contestService.deleteContest(afterSaveContest.getContestId());
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
		Question nullQuestion = new Question();
		Assertions.assertThrows(NullPointerException.class, () -> questionService.saveQuestion(null));
		Assertions.assertThrows(NullPointerException.class, () -> questionService.saveQuestion(nullQuestion));
			}

	@Test
	public void findByQuestionIdSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);
		Question getQuestion = questionService.findByQuestionId(savedQuestion.getQuestionId());
		Assertions.assertNotNull(getQuestion);
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
	public void getTestCaseSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);
		List<TestCases> testCaseList = questionService.getTestCase(savedQuestion.getQuestionId());
		Assertions.assertNotNull(testCaseList);
		Assertions.assertTrue(testCaseList.size() > 0);
		Assertions.assertTrue(!testCaseList.isEmpty());
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
	}

	@Test
	public void deleteQuesFromLevelSuccessTest() {
		savedQuestion = questionService.saveQuestion(question);
		ArrayList<String> contestAndQuestionIdforLevel = new ArrayList<String>();
		contestAndQuestionIdforLevel.add("questionForLevel");
		contestAndQuestionIdforLevel.add(savedQuestion.getQuestionId());
		questionService.saveQuestionOrContest(contestAndQuestionIdforLevel);
		questionService.findByQuestionId(savedQuestion.getQuestionId());
		questionService.deleteQuestionTestCase(savedQuestion);
		question.setQuestionId("");
		afterSaveContest = contestService.saveContest(contest);
		question.setContestLevel(question.getContestLevel() + "@" + afterSaveContest.getContestId());
		savedQuestion = questionService.saveQuestion(question);
		ArrayList<String> contestAndQuestionIdForContest = new ArrayList<String>();
		contestAndQuestionIdForContest.add(afterSaveContest.getContestId());
		contestAndQuestionIdForContest.add(savedQuestion.getQuestionId());
		questionService.saveQuestionOrContest(contestAndQuestionIdForContest);
		Assertions.assertNotNull(savedQuestion);
		Assertions.assertTrue(contestAndQuestionIdforLevel != null && contestAndQuestionIdforLevel.size() > 2);
		Assertions.assertTrue(contestAndQuestionIdForContest != null && contestAndQuestionIdForContest.size() > 2);
		
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

}
