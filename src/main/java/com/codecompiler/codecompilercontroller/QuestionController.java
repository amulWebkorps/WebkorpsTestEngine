package com.codecompiler.codecompilercontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.helper.Helper;
import com.codecompiler.service.CommonService;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.QuestionService1;
import com.codecompiler.service.impl.ContestServiceImpl;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.helper.Helper;
import com.codecompiler.service.QuestionService1;

@Controller
@CrossOrigin(origins = "*")
public class QuestionController {

	@Autowired
	private QuestionService1 questionService;
	
	@Autowired
	private ContestService contestService;
	
	@Autowired
	QuestionService qs;
	String contestLevel = null;

	@PostMapping(value = "/questionUpload", headers = "content-type=multipart/*")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("level") String level) {
		if (Helper.checkExcelFormat(file)) {
			try {
				questionService.saveFileForBulkQuestion(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ResponseEntity.ok(level);

		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please check excel file format");
		}
	}
	
	@PostMapping("/savequestion")
	public ResponseEntity<Object> saveQuestion(@RequestBody Question question, Model model) throws IOException {
		System.out.println("savequestion Obj Prev : " + question);
		Question savedQuestion = null;
		String[] stringOfCidAndCl = new String[2];		
		stringOfCidAndCl = question.getContestLevel().split("@");
		String tempQid = question.getQuestionId();
		if (tempQid == ("")) {			
			tempQid = UUID.randomUUID().toString();
			question.setQuestionId(tempQid);
			question.setQuestionStatus("true");
		}		
		if(stringOfCidAndCl.length == 1) {
			question.setContestLevel(stringOfCidAndCl[0]);			
			savedQuestion = questionService.saveQuestion(question);
			}else {			
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
		return new ResponseEntity<Object>(savedQuestion, HttpStatus.OK);
	}

	/*
	 * @RequestMapping("/questionuploaded") public String upload(Model model) {
	 * List<Question> contestQuestionsTemp = new ArrayList<>(); if
	 * (contestLevel.equals("Level 2")) {
	 * 
	 * contestQuestionsTemp = qs.findQuestionByContestLevel(contestLevel);
	 * model.addAttribute("questions", contestQuestionsTemp); return
	 * "level2Questions";
	 * 
	 * } else if (contestLevel.equals("Level 1")) { contestQuestionsTemp =
	 * qs.findQuestionByContestLevel(contestLevel); model.addAttribute("questions",
	 * contestQuestionsTemp); return "level1Questions"; } else {
	 * contestQuestionsTemp = qs.getAllQuestion(); model.addAttribute("questions",
	 * contestQuestionsTemp); return "questionListAndAddNewQuestion"; }
	 * 
	 * }
	 */
}
