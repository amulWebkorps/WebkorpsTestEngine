package com.codecompiler.codecompilercontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Language;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.LanguageService;

@Controller
@CrossOrigin(origins = "*")
public class HomeController {

	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private ContestService contestService;
	
	@GetMapping("login")
	public ResponseEntity<Object> login(@RequestParam("contestId") String contestId) {
		return new ResponseEntity<Object>(contestId, HttpStatus.OK);
	}

	@GetMapping("showAllLanguage")
	public ResponseEntity<Object> showAllLanguage() {
		List<Language> language = languageService.findAllLanguage();
		return new ResponseEntity<Object>(language, HttpStatus.OK);
	}
	
	@GetMapping("getAllContestList")
	public ResponseEntity<Object> showContestList() {
		List<Contest> contesList = contestService.findAllContest();
		return new ResponseEntity<Object>(contesList, HttpStatus.OK);
	} 

}
