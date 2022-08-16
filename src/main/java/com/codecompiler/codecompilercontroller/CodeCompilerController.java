package com.codecompiler.codecompilercontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.QuestionStatus;
import com.codecompiler.entity.ResponseToFE;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.CommonService;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.QuestionService1;

@Controller
public class CodeCompilerController {

	@Autowired
	private CodeProcessingService codeProcessingService;
	
	@Autowired
	private QuestionService1 questionService;

	@PostMapping("startContestPage")
	public ResponseEntity<Object> contestPage(@RequestParam(value = "contestId", required = false) String contestId,
			@RequestParam(value = "studentId", required = false) String studentId) {
		
		List<Question> contestQuestions = questionService.getAllQuestion(contestId, studentId);
		return generateResponse(contestQuestions.get(0), contestId, studentId, 0, false, true, HttpStatus.OK);
	}
	
	@PostMapping("javacompiler")
	public ResponseEntity<ResponseToFE> getCompiler(@RequestBody Map<String, Object> data)
			throws Exception {
		ResponseToFE responsef = codeProcessingService.compileCode(data);
		return ResponseEntity.ok(responsef);
	}
	
	public ResponseEntity<Object> generateResponse(Question contestQuestions, String contestId, String studentId, Integer nextQuestion, boolean previous, boolean next, HttpStatus status) {
		Map<String, Object> mp = new HashedMap();
		mp.put("QuestionList", contestQuestions);
		mp.put("contestId", contestId);
		mp.put("studentId", studentId);
		mp.put("nextQuestion", nextQuestion);
		mp.put("previous", previous);
		mp.put("next", next);
		return new ResponseEntity<Object>(mp, status);
	}
}
