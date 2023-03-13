package com.codecompiler.controller;

import java.util.List;

import com.codecompiler.dto.*;
import com.codecompiler.entity.StudentTestDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

  @PostMapping("runORExecuteAllTestCases")
  public ResponseEntity<Object> runORExecuteAllTestCases(@RequestBody ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws Exception {
    log.info("runORExecuteAllTestCases: started");
    CodeResponseDTO response = codeProcessingService.runORExecuteAllTestCases(executeAllTestCasesDTO);
    log.info("runORExecuteAllTestCases: ended");
    return ResponseHandler.generateResponse("success", HttpStatus.OK, response);
  }

  @PostMapping("save/code")
  public ResponseEntity<?> saveStudentTestDetail(@RequestBody StudentTestDetailDTO studentTestDetailDTO) {
    log.info("saveStudentCodeInfo() -> Started");
    try {
      StudentTestDetail savedStudentTestDetail = codeProcessingService.saveStudentTestDetail(studentTestDetailDTO);
      log.info("saveStudentCodeInfo() -> studentCodeInfo has been save successfully");
      return ResponseHandler.generateResponse("success", HttpStatus.OK, "StudentTestDetail saved successfully");
    } catch (Exception e) {
      log.info("Something went wrong with this message: " + e.getMessage());
      return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PostMapping("finish/test")
  public ResponseEntity<?> saveStudentTestDetailAndFinishTest(@RequestBody StudentTestDetailDTO studentTestDetailDTO) {
    log.info("saveStudentTestDetailAndFinishTest() -> Started");
    try {
      StudentTestDetail savedStudentTestDetail = codeProcessingService.saveStudentTestDetail(studentTestDetailDTO);
      log.info("saveStudentTestDetailAndFinishTest() -> studentTestInfo has been save successfully");
      return ResponseHandler.generateResponse("success", HttpStatus.OK, "StudentTestDetail saved successfully And Finishing the test");
    } catch (Exception e) {
      log.info("Something went wrong with this message: " + e.getMessage());
      return ResponseHandler.generateResponse("error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

}
