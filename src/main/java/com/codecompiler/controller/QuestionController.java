package com.codecompiler.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.MCQ;
import com.codecompiler.entity.Question;
import com.codecompiler.reponse.ResponseHandler;
import com.codecompiler.service.MCQService;
import com.codecompiler.service.QuestionService;

@Controller
@CrossOrigin(origins = "*")
public class QuestionController {

	private static final Logger logger = LogManager.getLogger(QuestionController.class);

	@Autowired
	private QuestionService questionService;

	@Autowired
	private MCQService mcqService;

	@PostMapping(value = "/admin/questionUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> questionUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("contestId") String contestId, @RequestParam("contestLevel") String contestLevel) {
		logger.info("questionUpload :: started with contestId: " + contestId);
		try {
			List<Question> allQuestions = questionService.saveFileForBulkQuestion(file, contestId, contestLevel);
			logger.info("questionUpload:: Bulk Question saved successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, allQuestions);
		} catch (Exception e) {
			logger.error("questionUpload:: Exception occured: " + e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping(value = "/admin/mcqQuestionUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> mcqQuestionUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("contestId") String contestId) {
		logger.info("MCQQuestionUpload :: started with contestId: " + contestId);
		try {
			List<MCQ> allMCQQuestions = mcqService.saveFileForBulkMCQQuestion(file, contestId);
			logger.info("MCQQuestionUpload:: Bulk MCQ Question saved successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, allMCQQuestions);
		} catch (Exception e) {
			logger.error("MCQQuestionUpload:: Exception occured: " + e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping("admin/saveQuestion")
	public ResponseEntity<Object> saveQuestion(@RequestBody Question question) {
		logger.info("saveQuestion:: started with question: " + question);
		try {
			question.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
			Question savedQuestion = questionService.saveQuestion(question);
			logger.info("saveQuestion:: Question saved successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, savedQuestion);
		} catch (Exception e) {
			logger.error("saveQuestion:: Exception occured: " + e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping("admin/addSelectedAvailableQuestiontoContest")
	public ResponseEntity<Object> addSelectedAvailableQueToContest(
			@RequestBody Map<String, List<String>> questionIdList) {
		logger.info("addSelectedAvailableQueToContest:: started with questionIdList: " + questionIdList.size());
		try {
			List<Question> questionDetails = questionService.getAllQuestions(questionIdList);
			logger.info("addSelectedAvailableQueToContest:: Question saved in Contest successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, questionDetails);
		} catch (Exception ex) {
			logger.error("addSelectedAvailableQueToContest:: Exception occured: " + ex.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	@PutMapping("admin/deleteQuestion") // cid, qid
	public ResponseEntity<Object> updateQuestionStatus(@RequestBody ArrayList<String> contestAndQuestionId) {
		logger.info("updateQuestionStatus:: started with contestAndQuestionId: " + contestAndQuestionId.toString());
		try {
			questionService.saveQuestionOrContest(contestAndQuestionId);
			logger.info("updateQuestionStatus:: saveQuestionOrContest saved successfully");
			return ResponseHandler.generateResponse("success", HttpStatus.OK, contestAndQuestionId);
		} catch (Exception e) {
			logger.info("updateQuestionStatus:: Exception occured : " + e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("admin/filterQuestion")
	public ResponseEntity<Object> filterQuestion(@RequestParam String filterByString) {
		logger.info("filterQuestion: started filterByString = " + filterByString);
		try {
			List<Question> totalQuestionByFilter = questionService.filterQuestion(filterByString);
			logger.info("filterQuestion:: totalQuestionByFilter size : " + totalQuestionByFilter.size());
			return ResponseHandler.generateResponse("success", HttpStatus.OK, totalQuestionByFilter);
		} catch (Exception e) {
			logger.error("filterQuestion:: Exception occured: " + e.getMessage());
			return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

}
