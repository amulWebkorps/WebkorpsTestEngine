package com.codecompiler.service.impl;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.codecompiler.dto.*;
import com.codecompiler.entity.StudentTestDetail;
import com.codecompiler.exception.StudentNotFoundException;
import com.codecompiler.repository.StudentTestDetailRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.codecompiler.entity.TestCases;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.StudentService;
import com.codecompiler.util.CodeProcessingUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CodeProcessingServiceImpl implements CodeProcessingService {

  @Autowired
  private StudentService studentService;

  @Autowired
  private QuestionService questionService;

  @Autowired
  private CodeProcessingUtil codeProcessingUtil;

  @Autowired
  private StudentTestDetailRepository studentTestDetailRepository;

  int count = 0;

  private CodeResponseDTO saveSubmittedCode(CodeDetailsDTO codeDetailsDTO, int index, ArrayList<Boolean> testCasesSuccess,
                                            String complilationMessage) throws IOException {
    log.info("saveSubmittedCode :: started");
    String submittedCodeFileName = codeDetailsDTO.getQuestionsAndCode().get(index).getQuestionId() + "_"
        + codeDetailsDTO.getStudentId();
    Set<String> studentQuestionIds = new HashSet<>();
    studentQuestionIds.add(codeDetailsDTO.getQuestionsAndCode().get(index).getQuestionId());
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();

    FileWriter flSubmitted = new FileWriter("src/main/resources/CodeSubmittedByCandidate/" + submittedCodeFileName);
    PrintWriter prSubmitted = new PrintWriter(flSubmitted);
    prSubmitted.write(codeDetailsDTO.getQuestionsAndCode().get(index).getCode());
    studentService.updateStudentDetails(codeDetailsDTO.getStudentId(), codeDetailsDTO.getContestId(),
        studentQuestionIds, testCasesSuccess, complilationMessage, submittedCodeFileName);
    prSubmitted.flush();
    prSubmitted.close();
    codeResponseDTO.setSuccessMessage("Code Submitted Successfully");
    log.info("saveSubmittedCode ::ended & Code Submitted Successfully");
    return codeResponseDTO;
  }

  private String executeProcess(String command) {
    Process pro;
    try {
      pro = Runtime.getRuntime().exec(command, null, new File("src/main/resources/temp/"));
      String message = codeProcessingUtil.getMessagesFromProcessInputStream(pro.getInputStream());
      return message;
    } catch (IOException e) {
      log.error("Object is null " + e.getMessage());
      return e.getMessage();
    }

  }


  @Override
  public CodeResponseDTO compileCode(CodeDetailsDTO codeDetailsDTO) throws IOException {
    log.info("compile code: started");
    String language = codeDetailsDTO.getLanguage();
    String studentId = codeDetailsDTO.getStudentId();
    List<QuestionAndCodeDTO> questionIds = codeDetailsDTO.getQuestionsAndCode();
    int flag = codeDetailsDTO.getFlag();
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    Double percentage = 0.00;
    for (int i = 0; i < questionIds.size(); i++) {
      codeProcessingUtil.saveCodeTemporary(questionIds.get(i).getCode(), language, studentId);
      try {
        ArrayList<Boolean> testCasesSuccess = new ArrayList<Boolean>();
        String compilationCommand = codeProcessingUtil.compilationCommand(language, studentId);
        String complilationMessage = executeProcess(compilationCommand);
        if (!complilationMessage.isEmpty() && flag == 0) {
          codeResponseDTO.setComplilationMessage(complilationMessage);
          log.info("compile code :: compilation error :: " + complilationMessage);
          return codeResponseDTO;
        }
        List<TestCases> testCases = questionService.getTestCase(questionIds.get(i).getQuestionId());
        String interpretationCommand = codeProcessingUtil.interpretationCommand(language, studentId);
        String exceptionMessage = executeProcess(interpretationCommand);
        if (!exceptionMessage.isEmpty() && flag == 0) {
          codeResponseDTO.setComplilationMessage(exceptionMessage);
          log.info("compile code :: exception occured :: " + exceptionMessage);
          return codeResponseDTO;
        }
        for (TestCases testCase : testCases) {
          String input = testCase.getInput();
          String interpretationMessage = executeProcess(interpretationCommand + input);
          interpretationMessage = interpretationMessage.substring(0, interpretationMessage.length() - 1);
          if (interpretationMessage.contains(testCase.getOutput())
              || interpretationMessage.equals(testCase.getOutput())) {
            testCasesSuccess.add(true);
            count++;
          } else {
            testCasesSuccess.add(false);
          }
        }
        if (flag == 1) {
          codeResponseDTO = saveSubmittedCode(codeDetailsDTO, i, testCasesSuccess, complilationMessage);
        }
        codeResponseDTO.setTestCasesSuccess(testCasesSuccess);
      } catch (Exception e) {
        log.error("Object is null " + e.getMessage());
        codeResponseDTO.setComplilationMessage("Something wents wrong. Please contact to HR");
      }
    }
    if (codeDetailsDTO.getTimeOut()) {
      percentage = generatePercentage(questionIds, count);
      studentService.finalSubmitContest(codeDetailsDTO.getStudentId(), percentage);
    }
    log.info("compile code: ended");

    return codeResponseDTO;
  }

  public Double generatePercentage(List<QuestionAndCodeDTO> questionIds, int count) {
    List<String> questionId = questionIds.stream().map(id -> id.getQuestionId()).collect(Collectors.toList());
    List<List<TestCases>> testCases = questionService.findByQuestionIdIn(questionId);
    List<String> testCasesSize = new ArrayList<String>();
    testCases.stream().forEach(testCase -> testCase.stream().forEach(testcase -> testCasesSize.add(testcase.getInput())));
    double percentage = ((100 * count) / testCasesSize.size());
    return percentage;
  }

  @Override
  public CodeResponseDTO runORExecuteAllTestCases(ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IOException {
    log.info("runORExecuteAllTestCases code: started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    Process pro = null;
    codeProcessingUtil.saveCodeTemporary(executeAllTestCasesDTO.getCode(), executeAllTestCasesDTO.getLanguage(),
        executeAllTestCasesDTO.getStudentId());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<CodeResponseDTO> futureResult = null;
    try {
      futureResult = executorService.submit(new Callable<CodeResponseDTO>() {
        @Override
        public CodeResponseDTO call() throws Exception {
          return (CodeResponseDTO) executeStudentCode(executeAllTestCasesDTO);
        }
      });
      codeResponseDTO = futureResult.get();
    } catch (Exception e) {
      log.error("Object is null " + e.getMessage());
      codeResponseDTO.setComplilationMessage("Something wents wrong. Please contact to HR");
    } finally {
      executorService.shutdown();
      futureResult = null;
    }
    log.info("runORExecuteAllTestCases code: ended");
    return codeResponseDTO;
  }

  @Override
  public StudentTestDetail saveStudentTestDetail(StudentTestDetailDTO studentTestDetailsDTO) {
    log.info("saveStudentCodeInfo() -> Started");
    StudentTestDetailDTO studentTestDetailDTO = new StudentTestDetailDTO();
    StudentTestDetail studentDetails = null;
    if (studentTestDetailsDTO.getStudentId().isBlank())
      throw new StudentNotFoundException("Invalid student details");

    StudentTestDetail studentTestDetail = studentTestDetailDTO.prepareStudentObj(studentTestDetailsDTO);
    StudentTestDetail savedStudentDetails = studentTestDetailRepository.findByStudentId(studentTestDetailsDTO.getStudentId());
    if (savedStudentDetails == null || savedStudentDetails.getId().isBlank()) {
      return studentTestDetailRepository.save(studentTestDetail);
    } else {
      savedStudentDetails.setQuestionDetails(studentTestDetailsDTO.getQuestionDetails());
      return studentTestDetailRepository.save(savedStudentDetails);
    }
  }

  private Object executeStudentCode(ExecuteAllTestCasesDTO executeAllTestCasesDTO) {
    log.info("executeStudentCode() -> started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    String language = executeAllTestCasesDTO.getLanguage();
    String studentId = executeAllTestCasesDTO.getStudentId();
    String questionId = executeAllTestCasesDTO.getQuestionId();

    String compilationCommand = codeProcessingUtil.compilationCommand(language, studentId);
    String complilationMessage = executeProcess(compilationCommand);

    if (!complilationMessage.isEmpty()) {
      codeResponseDTO.setComplilationMessage(complilationMessage);
      log.info("runORExecuteAllTestCases code :: compilation error message :: " + complilationMessage);
      return codeResponseDTO;
    }
    String interpretationCommand = codeProcessingUtil.interpretationCommand(language, studentId);
    if (executeAllTestCasesDTO.getFlag() == 1) {
      codeResponseDTO = executeAllTestCases(questionId, interpretationCommand);
    } else {
      codeResponseDTO = executeSampleTestCase(questionId, interpretationCommand);
    }
    log.info("executeStudentCode() -> end");
    return codeResponseDTO;
  }

  @Cacheable(value = "executeCodeForEveryTestcase", key = "#questionId")
  public CodeResponseDTO executeAllTestCases(String questionId, String interpretationCommand) {
    log.info("executeAllTestCases() -> started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    List<Callable<Boolean>> taskList = new ArrayList<Callable<Boolean>>();
    List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
    ArrayList<Boolean> testCasesResult = new ArrayList<Boolean>();
    List<TestCases> testCases = questionService.getTestCase(questionId);
    ExecutorService executorService = Executors.newFixedThreadPool(testCases.size());

    try {
      for (TestCases testCase : testCases) {
        taskList.add(new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            return getTestCaseResponse(testCase, interpretationCommand);
          }
        });
      }
      futureList = executorService.invokeAll(taskList);
      for (Future<Boolean> testCaseResponse : futureList) {
        testCasesResult.add(testCaseResponse.get());
      }
    } catch (InterruptedException e) {
      log.error("executeAllTestCases() -> Something went wrong with this message: " + e.getMessage());
      codeResponseDTO.setComplilationMessage("Something went wrong. Please contact to HR");
      return codeResponseDTO;
    } catch (Exception e) {
      log.error("executeAllTestCases() -> Something went wrong with this message: " + e.getMessage());
      codeResponseDTO.setComplilationMessage("Something went wrong. Please contact to HR");
      return codeResponseDTO;
    } finally {
      executorService.shutdown();
      futureList.clear();
      taskList.clear();
    }
    codeResponseDTO.setTestCasesSuccess(testCasesResult);
    log.info("executeAllTestCases() -> end");
    return codeResponseDTO;
  }

  private Boolean getTestCaseResponse(TestCases testCase, String interpretationCommand) {
    log.info("executeAllTestCases() -> started");
    Boolean testCaseResponse;
    String input = testCase.getInput();
    String interpretationMessage = executeProcess(interpretationCommand + input);
    interpretationMessage = interpretationMessage.substring(0, interpretationMessage.length() - 1);
    if (interpretationMessage.contains(testCase.getOutput())
        || interpretationMessage.equals(testCase.getOutput())) {
      testCaseResponse = true;
      count++;
    } else {
      testCaseResponse = false;
    }
    log.info("executeAllTestCases() -> end");
    return testCaseResponse;
  }

  private CodeResponseDTO executeSampleTestCase(String questionId, String interpretationCommand) {
    log.info("executeSampleTestCase() -> started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    ArrayList<Boolean> testCasesSuccess = new ArrayList<Boolean>();
    TestCaseDTO testCases = questionService.getSampleTestCase(questionId);

    String input = sliptInput(testCases.getInput());
    String interpretationMessage = executeProcess(interpretationCommand + input);
    if (interpretationMessage.isBlank()) {
      codeResponseDTO.setComplilationMessage(interpretationMessage);
      log.info("runORExecuteAllTestCases code :: exception occured :: " + interpretationMessage);
      return codeResponseDTO;
    }
    interpretationMessage = interpretationMessage.substring(0, interpretationMessage.length() - 1);
    if (interpretationMessage.contains(testCases.getOutput())
        || interpretationMessage.equals(testCases.getOutput())) {
      testCasesSuccess.add(true);
      count++;
    } else {
      testCasesSuccess.add(false);
    }
    codeResponseDTO.setTestCasesSuccess(testCasesSuccess);
    log.info("executeSampleTestCase() -> end");
    return codeResponseDTO;
  }

  private String sliptInput(String input) {
    String separator = "- ";
    int sepPos = input.indexOf(separator);
    return input.substring(sepPos + separator.length());
  }
}
