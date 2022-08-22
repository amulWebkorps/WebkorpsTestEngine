package com.codecompiler.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.helper.Helper;
import com.codecompiler.service.QuestionService;

//@Controller
public class QuestionController {
	@Autowired
	QuestionService qs;
	String contestLevel = null;

	@RequestMapping(value = "/question/upload", headers = "content-type=multipart/*", method = RequestMethod.POST)
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("level") String level) {
		contestLevel = level;
		if (Helper.checkExcelFormat(file)) {
			// true
			try {
				//this.qs.save(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ResponseEntity.ok(contestLevel);

		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file ");
	}

	@RequestMapping("/questionuploaded")
	public String upload(Model model) {
		List<Question> contestQuestionsTemp = new ArrayList<>();
		if (contestLevel.equals("Level 2")) {

			contestQuestionsTemp = qs.findQuestionByContestLevel(contestLevel);
			model.addAttribute("questions", contestQuestionsTemp);
			return "level2Questions";

		} else if (contestLevel.equals("Level 1")) {
			contestQuestionsTemp = qs.findQuestionByContestLevel(contestLevel);
			model.addAttribute("questions", contestQuestionsTemp);
			return "level1Questions";
		} else {
			contestQuestionsTemp = qs.getAllQuestion();
			model.addAttribute("questions", contestQuestionsTemp);
			return "questionListAndAddNewQuestion";
		}

	}
}
