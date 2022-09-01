package com.codecompiler.codecompilercontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Language;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.entity.ResponseToFE;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.LanguageService;
import com.codecompiler.service.QuestionService1;

@Controller
@CrossOrigin(origins = "*")
public class CodeCompilerController {

	private static final Logger logger = LogManager.getLogger(CodeCompilerController.class);

	@Autowired
	private CodeProcessingService codeProcessingService;

	@Autowired
	private QuestionService1 questionService;

	@Autowired
	private LanguageService languageService;

	@Autowired
	private ContestService contestService;

	@PostMapping("startContestPage")
	public ResponseEntity<Object> contestPage(@RequestParam(value = "contestId", required = false) String contestId,
			@RequestParam(value = "studentId", required = false) String studentId,
			@RequestParam(value = "language", required = false) String selectlanguage) {
		logger.info("contestPage: started contestId = " + contestId + ", studentId = "+studentId+", language = "+selectlanguage);
		Language language = languageService.findByLanguage(selectlanguage);
		Contest contestTime = contestService.findByContestId(contestId);
		List<Question> contestQuestionsList = questionService.getAllQuestion(contestId, studentId);
		logger.info("contestPage: ended");
		return generateResponse(contestQuestionsList, language, contestId, contestTime.getContestTime(), studentId, 0,
				false, true, HttpStatus.OK);
	}

	@PostMapping("runAndCompilerCode")
	public ResponseEntity<ResponseToFE> getCompiler(@RequestBody Map<String, Object> data) throws Exception {
		logger.info("getCompiler: started");
		ResponseToFE responsef = codeProcessingService.compileCode(data);
		logger.info("getCompiler: ended");
		return ResponseEntity.ok(responsef);
	}

	private ResponseEntity<Object> generateResponse(List<Question> contestQuestionsList, Language language,
			String contestId, String contestTime, String studentId, Integer nextQuestion, boolean previous,
			boolean next, HttpStatus status) {
		Map<String, Object> mp = new HashedMap();
		mp.put("QuestionList", contestQuestionsList);
		mp.put("languageCode", language);
		mp.put("contestId", contestId);
		mp.put("studentId", studentId);
		mp.put("contestTime", contestTime);
		mp.put("nextQuestion", nextQuestion);
		mp.put("previous", previous);
		mp.put("next", next);
		return new ResponseEntity<Object>(mp, status);
	}

	@PostMapping("admin/createContest")
	public ResponseEntity<Object> addContest(@RequestBody Contest contest) {
		logger.info("addContest: started contestName = "+contest.getContestName());
		List<Contest> contestIdAndName = new ArrayList<>();
		try {
			contestService.saveContest(contest);
			logger.info("Contest added successfully");
		} catch (Exception ex) {
			logger.error("Exception occured in addContest :: "+ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Contest not added");
		}
		return new ResponseEntity<Object>(contestIdAndName, HttpStatus.OK);
	}

	@DeleteMapping("admin/deleteContest")
	public ResponseEntity<Object> deleteContest(@RequestParam String contestId) {
		logger.info("deleteContest: started contestId = "+contestId);
		try {
			contestService.deleteContest(contestId);
			logger.info("deleteContest: deleted successfully");
		} catch (Exception e) {
			logger.error("Exception occured in deleteContest :: "+e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Contest Id");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Contest deleted successfully");
	}

	@GetMapping("admin/getContestDetail")
	public ResponseEntity<Object> getContestDetail(@RequestParam String contestId) {
		logger.info("getContestDetail: started contestId = "+contestId);
		Map<String, Object> contestDetail = new HashedMap<String, Object>();
		ArrayList<String> qListStatusTrue = new ArrayList<>();
		try {
			Contest contestRecord = contestService.findByContestId(contestId);
			contestDetail.put("contest", contestRecord);
			for (QuestionStatus questionStatus : contestRecord.getQuestionStatus()) {
				if (questionStatus.getStatus()) {
					qListStatusTrue.add(questionStatus.getQuestionId());
				}
			}
			List<Question> questionDetailList = questionService.findByQuestionIdIn(qListStatusTrue);
			List<Question> totalQuestionWithStatusTrue = questionService.findAllQuestion();
			List<Question> questionDetailListFormat = new ArrayList<>();
			for (Question question : questionDetailList) {
				Question formateQuestion = new Question();
				formateQuestion.setQuestionId(question.getQuestionId());
				formateQuestion.setQuestion(question.getQuestion());
				questionDetailListFormat.add(formateQuestion);

				totalQuestionWithStatusTrue.removeIf(x -> x.getQuestionId().equalsIgnoreCase(question.getQuestionId()));
			}			
			contestDetail.put("contestQuestionDetail", questionDetailListFormat);			

			List<Question> totalQuestionWithStatusTrueFormat = new ArrayList<>();
			for (Question question : totalQuestionWithStatusTrue) {
				Question formateQuestion = new Question();
				formateQuestion.setQuestionId(question.getQuestionId());
				formateQuestion.setQuestion(question.getQuestion());
				totalQuestionWithStatusTrueFormat.add(formateQuestion);
			}
			contestDetail.put("totalAvailableQuestion", totalQuestionWithStatusTrueFormat);
		} catch (Exception ex) {
			logger.error("Exception occured in getContestDetail :: "+ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
		logger.info("getContestDetail: ended");
		return new ResponseEntity<Object>(contestDetail, HttpStatus.OK);
	}

	@GetMapping("admin/filterQuestion")
	public ResponseEntity<Object> filterQuestion(@RequestParam String filterByString) {
		logger.info("filterQuestion: started filterByString = "+filterByString);
		List<Question> totalQuestionByFilter = new ArrayList<>();
		try {
			if (filterByString.equals("Level 1") || filterByString.equals("Level 2"))
				totalQuestionByFilter = questionService.findByContestLevel(filterByString);
			else
				totalQuestionByFilter = questionService.findAllQuestion();
		} catch (Exception e) {
			logger.error("Exception occured in getContestDetail :: "+e.getMessage());
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		logger.info("filterQuestion: ended");
		return new ResponseEntity<Object>(totalQuestionByFilter, HttpStatus.OK);
	}

}
