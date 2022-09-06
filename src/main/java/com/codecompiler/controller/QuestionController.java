package com.codecompiler.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService;

@Controller
@CrossOrigin(origins = "*")
public class QuestionController {

	private static final Logger logger = LogManager.getLogger(QuestionController.class);

	@Autowired
	private QuestionService questionService;

	@Autowired
	private ContestService contestService;

	@Autowired
	private ExcelConvertorService excelConvertorService;

	@PostMapping(value = "/admin/questionUpload", headers = "content-type=multipart/*")
	public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file,
			@RequestParam("contestId") String contestId) {
		logger.info("questionUpload: started");
		if (excelConvertorService.checkExcelFormat(file)) {
			try {
				List<Question> allQuestions = questionService.saveFileForBulkQuestion(file, contestId);
				logger.info("Bulk Question saved");
				return new ResponseEntity<Object>(allQuestions, HttpStatus.OK);
			} catch (Exception e) {
				logger.error("Excel not uploaded :: " + e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Excel not uploaded");
			}
		} else {
			logger.error("Please check excel file format");
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Please check excel file format");
		}
	}

	@PostMapping("admin/saveQuestion")
	public ResponseEntity<Object> saveQuestion(@RequestBody Question question) throws IOException {
		logger.info("saveQuestion: started");
		Question savedQuestion = new Question();
		try {
			String[] stringOfCidAndCl = new String[2];
			stringOfCidAndCl = question.getContestLevel().split("@");
			String tempQid = question.getQuestionId();
			if (tempQid == ("")) {
				tempQid = UUID.randomUUID().toString();
				question.setQuestionId(tempQid);
				question.setQuestionStatus("true");
			}
			if (stringOfCidAndCl.length == 1) {
				question.setContestLevel(stringOfCidAndCl[0]);
				savedQuestion = questionService.saveQuestion(question);
			} else {
				Contest contest = new Contest(); // id, level
				contest = contestService.findByContestId(stringOfCidAndCl[1]);
				question.setContestLevel(stringOfCidAndCl[0]);
				savedQuestion = questionService.saveQuestion(question);
				QuestionStatus queStatus = new QuestionStatus();
				queStatus.setQuestionId(savedQuestion.getQuestionId());
				queStatus.setStatus(true);
				contest.getQuestionStatus().add(queStatus);
				contestService.saveContest(contest);
			}
		} catch (Exception e) {
			logger.error("Questions not saved :: "+e.getMessage());
		}
		return new ResponseEntity<Object>(savedQuestion, HttpStatus.OK);
	}

	@PostMapping("admin/addSelectedAvailableQuestiontoContest")
	public ResponseEntity<Object> addSelectedAvailableQueToContest(
			@RequestBody Map<String, List<String>> questionIdList) {
		logger.info("addSelectedAvailableQuestiontoContest: started");
		List<Question> questionDetails = new ArrayList<>();
		try {
			String contestId = questionIdList.get("contestId").get(0);
			Contest con = new Contest();
			con = contestService.findByContestId(contestId);
			questionDetails = questionService.findByQuestionIdIn(questionIdList.get("questionsIds"));
			ArrayList<QuestionStatus> idWithstatus = con.getQuestionStatus();
			boolean flag = false;
			for (String idToChangeStatus : questionIdList.get("questionsIds")) {
				int index = 0;
				for (QuestionStatus qs : idWithstatus) {
					if (idToChangeStatus.equals(qs.getQuestionId())) {
						if (qs.getStatus() == false) {
							con.getQuestionStatus().get(index).setStatus(true);
							flag = true;
						} else if (qs.getStatus() == true) {
							flag = true;
						}
					}
					index++;
				}
				if (flag == false) {
					QuestionStatus qsTemp = new QuestionStatus();
					qsTemp.setQuestionId(idToChangeStatus);
					qsTemp.setStatus(true);
					con.getQuestionStatus().add(qsTemp);
				} else {
					flag = false;
				}
			}
			contestService.saveContest(con);
			logger.info("Question saved in Contest");
		} catch (Exception ex) {
			logger.error("Question not saved List is null :: "+ex.getMessage());
		}
		return new ResponseEntity<Object>(questionDetails, HttpStatus.OK);
	}

	@DeleteMapping("admin/deleteQuestion") // cid, qid
	public ResponseEntity<Object> deleteQuestion(@RequestBody ArrayList<String> contestAndQuestionId) {
		logger.info("deleteQuestion: started");
		try {
			if (contestAndQuestionId.get(0).equals("questionForLevel")) {
				Question questionStatusChange = questionService.findByQuestionId(contestAndQuestionId.get(1));
				questionStatusChange.setQuestionStatus("false");
				questionService.saveQuestion(questionStatusChange);
			} else {
				Contest contest = new Contest();
				contest = contestService.findByContestId(contestAndQuestionId.get(0));
				int index = 0;
				for (QuestionStatus qs : contest.getQuestionStatus()) {
					if (qs.getQuestionId().equals(contestAndQuestionId.get(1))) {
						contest.getQuestionStatus().get(index).setStatus(false);
					}
					index++;
				}
				contestService.saveContest(contest);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please check Input ");
		}
		return ResponseEntity.status(HttpStatus.OK).body("Question deleted successfully");
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
