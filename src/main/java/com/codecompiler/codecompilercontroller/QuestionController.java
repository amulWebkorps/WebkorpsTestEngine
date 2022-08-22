package com.codecompiler.codecompilercontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.helper.Helper;
import com.codecompiler.service.QuestionService1;

@Controller
@CrossOrigin(origins = "*")
public class QuestionController {

	@Autowired
	private QuestionService1 questionService;

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
