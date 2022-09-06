package com.codecompiler.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Language;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.reponse.ResponseHandler;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.LanguageService;
import com.codecompiler.service.QuestionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ContestController {
	
	@Autowired
	private ContestService contestService;
	
	@Autowired
	private QuestionService questionService;
	
	@Autowired
	private LanguageService languageService;
	
	
	@GetMapping("admin/getAllContestList")
	public ResponseEntity<Object> showContestList() {
		log.info("showContestList: started");
		try {
			List<Contest> contesList = contestService.findAllContest();
			log.info("showContestList: contestList size ::"+contesList.size());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contesList);
		} catch (Exception ex) {
			log.error("Exception occured in getContestDetail :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}	
	} 

	@DeleteMapping("admin/deleteContest")
	public ResponseEntity<Object> deleteContest(@RequestParam String contestId) {
		log.info("deleteContest: started contestId = "+contestId);
		try {
			contestService.deleteContest(contestId);
			log.info("deleteContest: deleted successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, "Contest Deleted Successfully");
		} catch (Exception e) {
			log.error("Exception occured in deleteContest :: "+e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	@PostMapping("admin/createContest")
	public ResponseEntity<Object> addContest(@RequestBody Contest contest) {
		log.info("addContest: started contestName = "+contest.getContestName());
		try {
			contest = contestService.saveContest(contest);
			log.info("Contest added successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contest);
		} catch (Exception ex) {
			log.error("Exception occured in addContest :: "+ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.OK, ex.getMessage());
		}
	}

	@GetMapping("admin/getContestDetail")
	public ResponseEntity<Object> getContestDetail(@RequestParam String contestId) {
		log.info("getContestDetail: started contestId = "+contestId);
		Map<String, Object> contestDetail = new HashedMap<String, Object>();
		ArrayList<String> qListStatusTrue = new ArrayList<>();
		try {
			Contest contestRecord = contestService.findByContestId(contestId);
			contestDetail.put("contest", contestRecord);
			ArrayList<QuestionStatus> questionStatusTemp = contestRecord.getQuestionStatus();
			for (QuestionStatus questionStatus : questionStatusTemp) {
				if (questionStatus.getStatus()) {
					qListStatusTrue.add(questionStatus.getQuestionId());
				}
			}
			List<Question> questionDetailList = questionService.findByQuestionIdIn(qListStatusTrue);
			List<Question> totalQuestionWithStatusTrue = questionService.findAllQuestion();			
			for (Question question : questionDetailList) 
				totalQuestionWithStatusTrue.removeIf(x -> x.getQuestionId().equalsIgnoreCase(question.getQuestionId()));
						
			contestDetail.put("contestQuestionDetail", questionDetailList);			

			List<Question> totalQuestionWithStatusTrueFormat = new ArrayList<>();
			for (Question question : totalQuestionWithStatusTrue) {
				Question formateQuestion = new Question();
				formateQuestion.setQuestionId(question.getQuestionId());
				formateQuestion.setQuestion(question.getQuestion());
				totalQuestionWithStatusTrueFormat.add(formateQuestion);
			}
			contestDetail.put("totalAvailableQuestion", totalQuestionWithStatusTrueFormat);
		} catch (Exception ex) {
			log.error("Exception occured in getContestDetail :: "+ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
		log.info("getContestDetail: ended");
		return new ResponseEntity<Object>(contestDetail, HttpStatus.OK);
	}
	
	
	@PostMapping("startContestPage")
	public ResponseEntity<Object> contestPage(@RequestParam(value = "contestId", required = false) String contestId,
			@RequestParam(value = "studentId", required = false) String studentId,
			@RequestParam(value = "language", required = false) String selectlanguage) {
		log.info("contestPage: started contestId = " + contestId + ", studentId = "+studentId+", language = "+selectlanguage);
		Language language = languageService.findByLanguage(selectlanguage);
		Contest contestTime = contestService.findByContestId(contestId);
		List<Question> contestQuestionsList = questionService.getAllQuestion(contestId, studentId);
		log.info("contestPage: ended");
		return generateResponse(contestQuestionsList, language, contestId, contestTime.getContestTime(), studentId, 0,
				false, true, HttpStatus.OK);
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
}
