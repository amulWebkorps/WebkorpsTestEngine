package com.codecompiler.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.codecompiler.dto.CodeDetailsDTO;
import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.entity.Language;
import com.codecompiler.reponse.ResponseHandler;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.LanguageService;

import lombok.extern.slf4j.Slf4j;

@Controller
@CrossOrigin(origins = "*")
@Slf4j
public class CodeCompilerController {

	@Autowired
	private CodeProcessingService codeProcessingService;

	@Autowired
	private LanguageService languageService;

	@GetMapping("showAllLanguage")
	public ResponseEntity<Object> showAllLanguage() {
		log.info("showAllLanguage: started.");
		List<Language> language = languageService.findAllLanguage();
		log.info("showAllLanguage ended total languages ::" + language.size());
		return ResponseHandler.generateResponse("success", HttpStatus.OK, language);
	}

	@PostMapping("runAndCompilerCode")
	public ResponseEntity<Object> getCompiler(@RequestBody CodeDetailsDTO codeDetailsDTO) throws Exception {
		log.info("getCompiler: started");
		CodeResponseDTO response = codeProcessingService.compileCode(codeDetailsDTO);
		log.info("getCompiler: ended");
		return ResponseHandler.generateResponse("success", HttpStatus.OK, response);
	}

}
