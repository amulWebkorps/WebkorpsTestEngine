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

	@Autowired
	private CodeProcessingService codeProcessingService;
	
	@Autowired
	private QuestionService1 questionService;
	
	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private ContestService contestService;
	
	Logger logger = LogManager.getLogger(CodeCompilerController.class);

	@PostMapping("startContestPage")
	public ResponseEntity<Object> contestPage(@RequestParam(value = "contestId", required = false) String contestId,
			@RequestParam(value = "studentId", required = false) String studentId, @RequestParam(value = "language", required = false) String selectlanguage) {
		Language language = languageService.findByLanguage(selectlanguage);
		Contest contestTime = contestService.findByContestId(contestId);
		List<Question> contestQuestionsList = questionService.getAllQuestion(contestId, studentId);
		return generateResponse(contestQuestionsList, language, contestId, contestTime.getContestTime() ,studentId, 0, false, true, HttpStatus.OK);
	}
	
	@PostMapping("runAndCompilerCode")
	public ResponseEntity<ResponseToFE> getCompiler(@RequestBody Map<String, Object> data)
			throws Exception {
		ResponseToFE responsef = codeProcessingService.compileCode(data);
		return ResponseEntity.ok(responsef);
	}
	
	public ResponseEntity<Object> generateResponse(List<Question> contestQuestionsList, Language language, String contestId, String contestTime ,String studentId, Integer nextQuestion, boolean previous, boolean next, HttpStatus status) {
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
	private ResponseEntity<Object> addContest(@RequestBody Contest contest) {
		List<Contest> contestIdAndName = new ArrayList<>();
		try {
			contestService.saveContest(contest);
			contestIdAndName = contestService.findAllContest();			
			logger.info("Contest added successfully");
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<Object>(contestIdAndName, HttpStatus.OK);
	}
	
	@DeleteMapping("admin/deleteContest")
	public ResponseEntity<Object> deleteContest(@RequestParam String contestId) {
		try {		
			contestService.deleteContest(contestId);
		} catch (Exception e) {
			e.printStackTrace();
			return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Check Contest Id");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Contest deleted successfully");
	}

	
	@GetMapping("admin/getContestDetail")
	private ResponseEntity<Object> getContestDetail(@RequestParam String contestId) {
		Map<String, Object> contestDetail = new HashedMap<String, Object>();
		ArrayList<String> qListStatusTrue = new ArrayList<>();
		List<Question> totalQuestionWithStatusTrue = new ArrayList<>();
		try {
			Contest contestRecord = contestService.findByContestId(contestId);
			contestDetail.put("contest", contestRecord);
			for (QuestionStatus questionStatus : contestRecord.getQuestionStatus()) {				
				if (questionStatus.getStatus()) {
					qListStatusTrue.add(questionStatus.getQuestionId());
				}
			}			
			List<Question> questionDetailList = questionService.findByQuestionIdIn(qListStatusTrue);
			List<Question> questionDetailListFormat = new ArrayList<>();
			for (Question question : questionDetailList) {
				Question formateQuestion = new Question(); 
				formateQuestion.setQuestionId(question.getQuestionId());
				formateQuestion.setQuestion(question.getQuestion());				
				questionDetailListFormat.add(formateQuestion);
			}			
			contestDetail.put("contestQuestionDetail", questionDetailListFormat);			
			totalQuestionWithStatusTrue = questionService.findAllQuestion();
			List<Question> totalQuestionWithStatusTrueFormat = new ArrayList<>();
			for (Question question : totalQuestionWithStatusTrue) {
				Question formateQuestion = new Question(); 
				formateQuestion.setQuestionId(question.getQuestionId());
				formateQuestion.setQuestion(question.getQuestion());				
				totalQuestionWithStatusTrueFormat.add(formateQuestion);
			}
			contestDetail.put("totalAvailableQuestion",totalQuestionWithStatusTrueFormat);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<Object>(contestDetail, HttpStatus.OK);
	}
	
	@GetMapping("admin/filterQuestion")
	public ResponseEntity<Object> filterQuestion(@RequestParam String filterByString) {
		List<Question> totalQuestionByFilter = new ArrayList<>();
		try {
			if (filterByString.equals("Level 1") || filterByString.equals("Level 2"))
				totalQuestionByFilter = questionService.findByContestLevel(filterByString);
			else
				totalQuestionByFilter = questionService.findAllQuestion();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(totalQuestionByFilter, HttpStatus.OK);
	}
	
	
}
