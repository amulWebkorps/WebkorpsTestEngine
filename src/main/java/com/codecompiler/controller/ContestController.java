package com.codecompiler.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.SampleTestCase;
import com.codecompiler.service.CommonService;
import com.codecompiler.service.ContestServiceImpl;
import com.codecompiler.service.StudentService;

@Controller
public class ContestController {
	@Autowired
	private CommonService commonService;
	@Autowired
	private ContestServiceImpl contestService;
	@Autowired
	StudentService studentService;
	public String contestId = "";
	public String contestLevel = "";
	public String contestLevelForPage = "";
	SampleTestCase sampleTestCase = new SampleTestCase();
	int questionID;
	Question q = new Question();
	List<Question> allQuestionsSpecificContestLevel = new ArrayList<>();
		
	@RequestMapping("/addcontest1")
	private ResponseEntity<Contest> addContest1(@RequestBody Contest contest, Model model) {	
		System.out.println(" 1 contest => "+contest);
		Contest contestToSave = new Contest();
		contestToSave.setContestName(contest.getContestName());
		contestToSave.setContestDescription(contest.getContestDescription());
		contestToSave.setContestLevel(contest.getContestLevel());
		Contest con1 = contestService.saveContest(contestToSave);
		System.out.println(" 2 contest => "+contest);
		return ResponseEntity.ok(con1);
	}
	
}
