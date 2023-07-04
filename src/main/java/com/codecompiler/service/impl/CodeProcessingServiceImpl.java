package com.codecompiler.service.impl;


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import com.codecompiler.dto.CodeDetailsDTO;
import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.dto.ExecuteAllTestCasesDTO;
import com.codecompiler.dto.QuestionDetailDTO;
import com.codecompiler.dto.StudentTestDetailDTO;
import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.StudentTestDetail;
import com.codecompiler.entity.TestCases;
import com.codecompiler.exception.StudentNotFoundException;
import com.codecompiler.repository.StudentRepository;
import com.codecompiler.repository.StudentTestDetailRepository;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.util.CodeProcessingUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CodeProcessingServiceImpl implements CodeProcessingService {
  private static final String SAVED_CODE_FILE_PATH = "src/main/resources/";
  private static final String CODE_FILE_PATH = "src/main/resources/static";
  private static final String CLASS_NAME = "Main";
  private static final String METHOD_NAME = "writeCode";
  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private QuestionService questionService;

  @Autowired
  private CodeProcessingUtil codeProcessingUtil;

  @Autowired
  private StudentTestDetailRepository studentTestDetailRepository;

  private CodeResponseDTO saveSubmittedCode(CodeDetailsDTO codeDetailsDTO, int index, ArrayList<Boolean> testCasesSuccess,
                                            String compilationMessage) throws IOException {
    log.info("saveSubmittedCode :: started");
    String submittedCodeFileName = codeDetailsDTO.getQuestionsAndCode().get(index).getQuestionId() + "_"
        + codeDetailsDTO.getStudentId();
    Set<String> studentQuestionIds = new HashSet<>();
    studentQuestionIds.add(codeDetailsDTO.getQuestionsAndCode().get(index).getQuestionId());
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();

    FileWriter flSubmitted = new FileWriter("src/main/resources/CodeSubmittedByCandidate/" + submittedCodeFileName);
    PrintWriter prSubmitted = new PrintWriter(flSubmitted);
    prSubmitted.write(codeDetailsDTO.getQuestionsAndCode().get(index).getCode());
    this.updateStudentDetails(codeDetailsDTO.getStudentId(), codeDetailsDTO.getContestId(),
        studentQuestionIds, testCasesSuccess, compilationMessage, submittedCodeFileName);
    prSubmitted.flush();
    prSubmitted.close();
    codeResponseDTO.setSuccessMessage("Code Submitted Successfully");
    log.info("saveSubmittedCode ::ended & Code Submitted Successfully");
    return codeResponseDTO;
  }

  private String executeProcess(String command) {
    Process pro;
    try {
      pro = Runtime.getRuntime().exec(command, null, new File("src/main/resources/"));
      String message = codeProcessingUtil.getMessagesFromProcessInputStream(pro.getInputStream());
      if(message=="" || message.isBlank())
    	  message = codeProcessingUtil.getMessagesFromProcessInputStream(pro.getErrorStream());
      return message;
    } catch (IOException e) {
      log.error("Object is null " + e.getMessage());
      return e.getMessage();
    }

  }

  int counter = 0;

  @Override
  public StudentTestDetailDTO compileCode(StudentTestDetail studentTestDetail) throws IOException {
    log.info("compileCode(): started");
    StudentTestDetailDTO studentTestDetailDTO = null;
    String language = studentTestDetail.getLanguage();
    String studentId = studentTestDetail.getStudentId();
    List<QuestionDetailDTO> questionDetailsList = studentTestDetail.getQuestionDetails();
    List<QuestionDetailDTO> questionDetailDTOList = new ArrayList<>();
    Double percentage = 0.00;
    int count = 0;

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    for (QuestionDetailDTO questionDetailDTO : questionDetailsList) {
      Future<QuestionDetailDTO> testCaseSuccessFutureResult = executorService.submit(new Callable<QuestionDetailDTO>() {
        public QuestionDetailDTO call() throws Exception {
          return testCaseSuccessCount(questionDetailDTO, language, studentId);
        }
      });

      try {
        QuestionDetailDTO updatedQuestionDetailDTO = testCaseSuccessFutureResult.get();
        count += updatedQuestionDetailDTO.getCount();
        questionDetailDTOList.add(updatedQuestionDetailDTO);
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException("Something went wrong, Please contact to HR\n" + e.getMessage());
      }
    }
    percentage = generatePercentage(questionDetailsList, count);
    StudentTestDetail savedStudentDetails = this.studentTestDetailRepository.findByStudentId(studentId);
    savedStudentDetails.setCount(count);
    savedStudentDetails.setPercentage(percentage);
    savedStudentDetails.setQuestionDetails(questionDetailDTOList);
    this.studentTestDetailRepository.save(savedStudentDetails);

    studentTestDetailDTO = new StudentTestDetailDTO(studentTestDetail.getStudentId(), studentTestDetail.getContestId(),
        savedStudentDetails.getLanguage(), savedStudentDetails.getQuestionDetails(), percentage);

    log.info("compileCode(): ended");
    return studentTestDetailDTO;
  }

  private QuestionDetailDTO testCaseSuccessCount(QuestionDetailDTO questionDetailDTO, String language, String studentId) {
    log.info("testCaseSuccessCount() started");
    List<Boolean> testCaseResult = new ArrayList<>();
    AtomicInteger count = new AtomicInteger();
    StringBuffer code = new StringBuffer(questionDetailDTO.getCode());
    counter = ++counter;
    code.insert(37, counter);
    try {
      String codeFile = this.codeProcessingUtil.saveCodeTemporary(String.valueOf(code), language, studentId, counter);
      String compilationCommand = this.codeProcessingUtil.compilationCommand(language, studentId, counter);
      String compilationMessage = this.executeProcess(compilationCommand);
      if (!compilationMessage.isEmpty()) {
        questionDetailDTO.setCompilationMsg(compilationMessage);
        log.info("compile code :: compilation error :: " + compilationMessage);
        return questionDetailDTO;
      }
      List<TestCases> testCases = this.questionService.getTestCase(questionDetailDTO.getQuestionId());
      String interpretationCommand = this.codeProcessingUtil.interpretationCommand(language, studentId, counter);
      String exceptionMessage = executeProcess(interpretationCommand);
     
      if (!exceptionMessage.isEmpty()) {
        questionDetailDTO.setCompilationMsg(exceptionMessage);
        log.info("compile code :: exception occurred :: " + exceptionMessage);
        return questionDetailDTO;
      }

      //preparing command line argument
      List<String> inputList = new ArrayList<>();
      List<String> outputList = new ArrayList<>();
      testCases.forEach(testCases1 -> {
        inputList.add(testCases1.getInput());
        outputList.add(testCases1.getOutput());
      });

      inputList.forEach(input -> {
        try {
          //Creating an object of dynamically generated class and testing against testCase input at once
          File savedJavaFile = new File(CODE_FILE_PATH);
          URL[] urls = new URL[]{new File(String.valueOf(savedJavaFile)).getParentFile().toURI().toURL()};
          ClassLoader cl = new URLClassLoader(urls);
          Class<?> yourClass = cl.loadClass(CLASS_NAME+counter);
          Method method = yourClass.getMethod(METHOD_NAME, String.class);
          Object programOutput = method.invoke(METHOD_NAME, input.toString());
         String result="";
          
          if(programOutput!=null)
        	  result=programOutput+"";
          if (outputList.contains(result)) {
            testCaseResult.add(true);
            count.incrementAndGet();
          } else {
            testCaseResult.add(false);
          }
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | MalformedURLException e) {
          throw new RuntimeException(e);
        }
      });

      //deleting the saved java file & java dot class file after completing program execution
      File savedJavaFile = new File(SAVED_CODE_FILE_PATH + codeFile);
      File savedJavaDotClassFile = new File(SAVED_CODE_FILE_PATH + "Main" + counter + ".class");
      if(savedJavaDotClassFile.exists()) {
    	  if (savedJavaFile.delete() && savedJavaDotClassFile.delete()) {
    	        log.info(savedJavaFile.getName() + " is successfully deleted");
    	      } else {
    	        log.info("Failed to delete " + savedJavaFile.getName() + " file");
    	      }
      }else
    	  savedJavaFile.delete();
     
    } catch (Exception e) {
      log.error("Object is null " + e.getMessage());
      throw new RuntimeException("Something went wrong, Please contact to HR\n" + e.getMessage());
    }
    questionDetailDTO.setTestCasesResult(testCaseResult);
    questionDetailDTO.setCount(count.intValue());
    log.info("testCaseSuccessCount() ended");
    return questionDetailDTO;
  }

  public Double generatePercentage(List<QuestionDetailDTO> questionDetailDTO, int count) {
    List<String> questionId = questionDetailDTO.stream().map(id -> id.getQuestionId()).collect(Collectors.toList());
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
      codeResponseDTO.setComplilationMessage("Something went wrong. Please contact to HR\n"+e.getMessage());
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
      savedStudentDetails.setQuestionDetails(studentTestDetailsDTO.getQuestionsAndCode());
      return studentTestDetailRepository.save(savedStudentDetails);
    }
  }

  private Object executeStudentCode(ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IllegalArgumentException, InstantiationException {
    log.info("executeStudentCode() -> started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    String language = executeAllTestCasesDTO.getLanguage();
    String studentId = executeAllTestCasesDTO.getStudentId();
    String questionId = executeAllTestCasesDTO.getQuestionId();

    String compilationCommand = codeProcessingUtil.compilationCommand(language, studentId);
    String compilationMessage = executeProcess(compilationCommand);
    if (!compilationMessage.isEmpty()) {
      codeResponseDTO.setComplilationMessage(compilationMessage);
      log.info("runORExecuteAllTestCases code :: compilation error message :: " + compilationMessage);
      return codeResponseDTO;
    }
    String interpretationCommand = codeProcessingUtil.interpretationCommand(language, studentId);
    System.out.println("INTER COMMAND : "+interpretationCommand);
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
      codeResponseDTO.setComplilationMessage("Something went wrong. Please contact to HR\n"+e.getMessage());
      return codeResponseDTO;
    } catch (Exception e) {
      log.error("executeAllTestCases() -> Something went wrong with this message: " + e.getMessage());
      codeResponseDTO.setComplilationMessage("Something went wrong. Please contact to HR\n"+e.getMessage());
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

  private Boolean getTestCaseResponse(TestCases testCase, String interpretationCommand) throws IllegalArgumentException, InstantiationException {
    log.info("executeAllTestCases() -> started");
    Boolean testCaseResponse;
    String input = testCase.getInput();
    try {
        //Creating an object of dynamically generated class and testing against testCase input at once
        File savedJavaFile = new File(SAVED_CODE_FILE_PATH);
        URL[] urls = new URL[]{new File(String.valueOf(savedJavaFile)).getParentFile().toURI().toURL()};
        ClassLoader cl = new URLClassLoader(urls);
        Class<?> yourClass = cl.loadClass(CLASS_NAME);
        Method method = yourClass.getMethod(METHOD_NAME, String.class);
        Object programOutput =method.invoke(METHOD_NAME, input+"");
        
        String result=String.valueOf(programOutput);
        if (result.contains(testCase.getOutput()) || result.equalsIgnoreCase(testCase.getOutput())) {
        	
        	testCaseResponse=true;
        } else {
        	testCaseResponse=false;
        }
      } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException | MalformedURLException e) {
        throw new RuntimeException(e);
      }
    
    File savedJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+".java");
    if(savedJavaFile.exists()) {
  	  if (savedJavaFile.delete()) {
  	        log.info(savedJavaFile.getName() + " is successfully deleted");
  	      } else {
  	        log.info("Failed to delete " + savedJavaFile.getName() + " file");
  	      }
    }
    log.info("executeAllTestCases() -> end");
    return testCaseResponse;
  }

  private CodeResponseDTO executeSampleTestCase(String questionId, String interpretationCommand) throws IllegalArgumentException, InstantiationException {
    log.info("executeSampleTestCase() -> started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    ArrayList<Boolean> testCasesSuccess = new ArrayList<Boolean>();
    TestCaseDTO testCases = questionService.getSampleTestCase(questionId);
    String input = testCases.getInput();
    ClassLoader cl =null;
    try {
        //Creating an object of dynamically generated class and testing against testCase input at once
//        File savedJavaFile = new File(SAVED_CODE_FILE_PATH);
//        URL[] urls = new URL[]{new File(String.valueOf(savedJavaFile)).getParentFile().toURI().toURL()};
//        cl = new URLClassLoader(urls);
//        Class<?> yourClass = cl.loadClass(CLASS_NAME);
//        Method method = yourClass.getMethod(METHOD_NAME, String.class);
//        Object programOutput =method.invoke(CLASS_NAME, input+"");
    	
    	String filePath = SAVED_CODE_FILE_PATH+"Main.java";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line of the file
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    	Class<?> yourClass = Class.forName(CLASS_NAME);
        Method method = yourClass.getMethod(METHOD_NAME, String.class);
        Object programOutput = method.invoke(yourClass.getDeclaredConstructor().newInstance(), input);
        yourClass=null;
        
        String result=String.valueOf(programOutput);
        System.out.println("RESULT : "+result);
        System.out.println("OUTPIT : "+programOutput);
        System.out.println("TEST : "+input+"======"+testCases.getOutput());
        if (result.equalsIgnoreCase(testCases.getOutput())) {
        	testCasesSuccess.add(true);
        } else {
        	testCasesSuccess.add(false);
        }
      } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException |
               NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    File savedJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+".java");
    if(savedJavaFile.exists()) {
  	  if (savedJavaFile.delete()) {
  	        log.info(savedJavaFile.getName() + " is successfully deleted");
  	      } else {
  	        log.info("Failed to delete " + savedJavaFile.getName() + " file");
  	      }
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

  public Student updateStudentDetails(String studentId, String contestId, Set<String> questionIds,
                                      ArrayList<Boolean> testCasesSuccess, String complilationMessage, String fileName) {
    log.info("updateStudentDetails: has started");
    TestCaseDTO testCaseRecord = new TestCaseDTO();
    List<TestCaseDTO> testCasesRecord1 = new ArrayList<>(); // need to remove in future
    testCaseRecord.setQuestionId(questionIds);
    testCaseRecord.setFileName(fileName);
    testCaseRecord.setComplilationMessage(complilationMessage);
    testCaseRecord.setTestCasesSuccess(testCasesSuccess); // create new collection for testcasesrecord and save that
    // pass id in get method
    Student existingRecord = studentRepository.findById(studentId);
    existingRecord.setContestId(contestId);
    existingRecord.setParticipateDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
    log.info("updateStudentDetails:: existingRecord: " + existingRecord);
    if (existingRecord.getQuestionId() != null) {
      existingRecord.getQuestionId().addAll(questionIds);
    } else {
      existingRecord.setQuestionId(questionIds);
    }
    if (existingRecord.getTestCaseRecord() != null) {
      existingRecord.getTestCaseRecord().removeIf(x -> x.getQuestionId().equals(questionIds));
      existingRecord.getTestCaseRecord().add(testCaseRecord);
    } else {
      testCasesRecord1.add(testCaseRecord);
      existingRecord.setTestCaseRecord(testCasesRecord1);
    }
    return studentRepository.save(existingRecord);
  }

  public StudentTestDetail updateStudentPercentage(String studentId, Double percentage) {
    if (studentId == null)
      throw new NullPointerException();
    else if (studentId.isBlank())
      throw new IllegalArgumentException();

    //Old API implementation, updating password field with null
    // Need to discuss on this
    Student student = this.studentRepository.findById(studentId);
    if (student == null || student.getId().isBlank())
      throw new StudentNotFoundException("Student not preset with given ID");

    student.setPassword(null);
    student.setPercentage(percentage);
    studentRepository.save(student);

    //new API implementation, Updating studentPercentage Field
    StudentTestDetail savedStudentDetail = this.studentTestDetailRepository.findByStudentId(studentId);

    savedStudentDetail.setPercentage(percentage);

    return this.studentTestDetailRepository.save(savedStudentDetail);
  }
  private static int counter1 = 0;
  private static String[] array = new String[5];
  private static void resetProjectState() {
      // Reset variables
      counter1 = 0;
      Arrays.fill(array, null);
  }

  private static void refreshProject() {
      // Reinitialize variables
      counter1 = 0;
      Arrays.fill(array, null);

      List<String> list = new ArrayList<>();
      list.clear();
  }
}

