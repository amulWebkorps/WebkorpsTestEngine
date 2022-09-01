package com.codecompiler.codecompilercontroller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	
	private static final Logger logger = LogManager.getLogger(HomeController.class);

	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private ContestService contestService;
	
	@GetMapping("public/login")
	public ResponseEntity<Object> login(@RequestParam("contestId") String contestId) {
		logger.info("login: started contestId ::"+contestId);
		return new ResponseEntity<Object>(contestId, HttpStatus.OK);
	}

	@GetMapping("showAllLanguage")
	public ResponseEntity<Object> showAllLanguage() {
		logger.info("showAllLanguage: started.");
		List<Language> language = languageService.findAllLanguage();
		return new ResponseEntity<Object>(language, HttpStatus.OK);
	}
	
	@GetMapping("admin/getAllContestList")
	public ResponseEntity<Object> showContestList() {
		logger.info("showContestList: started");
		List<Contest> contesList = new ArrayList<>();	
		try {
			contesList = contestService.findAllContest();
			logger.info("showContestList: contestList size ::"+contesList.size());
			return new ResponseEntity<Object>(contesList, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Exception occured in getContestDetail :: "+ex.getMessage());
			return new ResponseEntity<Object>("No contest Found", HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	} 

}
