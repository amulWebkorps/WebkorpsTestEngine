package com.codecompiler.service.impl;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.codecompiler.dto.CodeDetailsDTO;
import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.dto.ExecuteAllTestCasesDTO;
import com.codecompiler.dto.QuestionDetailDTO;
import com.codecompiler.dto.StudentTestDetailDTO;
import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.StudentTestDetail;
import com.codecompiler.entity.TestCases;
import com.codecompiler.exception.StudentNotFoundException;
import com.codecompiler.repository.QuestionRepository;
import com.codecompiler.repository.StudentRepository;
import com.codecompiler.repository.StudentTestDetailRepository;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.util.CodeProcessingUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CodeProcessingServiceImpl implements CodeProcessingService {
  private static final String SAVED_CODE_FILE_PATH = "src/main/resources/temp/";
  private static final String CLASS_NAME = "Main";
  @Autowired
  private StudentRepository studentRepository;
  
  @Autowired
  private QuestionRepository questionRepository;

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

  private String executeProcess(String command) throws IOException, InterruptedException {
	  ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
	  Process pro = processBuilder.start();
    try {
      pro = Runtime.getRuntime().exec(command, null, new File("src/main/resources/temp/"));
      String message = codeProcessingUtil.getMessagesFromProcessInputStream(pro.getInputStream());
      
      int exitCode = pro.waitFor();
      if (exitCode == 0) {
          return message.toString().trim();
      } else {
          BufferedReader errorReader = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
          StringBuilder errorBuilder = new StringBuilder();
          String errorLine;
          while ((errorLine = errorReader.readLine()) != null) {
              errorBuilder.append(errorLine).append("\n");
          }
          return errorBuilder.toString().trim();
      }
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
    String code = questionDetailDTO.getCode();
    counter = ++counter;
//    code.insert(37, counter);
    String result = code.replace("Main", CLASS_NAME+ counter);
    try {
      String codeFile = this.codeProcessingUtil.saveCodeTemporary(String.valueOf(result), language, studentId, counter);
      String compilationCommand = this.codeProcessingUtil.compilationCommand(language, studentId, counter);
      String compilationMessage = this.executeProcess(compilationCommand);
      if (!compilationMessage.isEmpty()) {
        questionDetailDTO.setCompilationMsg(compilationMessage);
        log.info("compile code :: compilation error :: " + compilationMessage);
        return questionDetailDTO;
      }
//      List<TestCases> testCases = this.questionService.getTestCase(questionDetailDTO.getQuestionId());
      Question questions = questionRepository.findByQuestionId(questionDetailDTO.getQuestionId());
      List<TestCases> testCases = questions.getTestcases();
      String questionType=questions.getQuestionType();
      //preparing command line argument
      List<String> inputList = new ArrayList<>();
      List<String> outputList = new ArrayList<>();
      testCases.forEach(testCases1 -> {
        inputList.add(testCases1.getInput());
        outputList.add(testCases1.getOutput());
      });
      String interpretationCommand = this.codeProcessingUtil.interpretationCommand(language, studentId, counter);
      for(int i=0;i<inputList.size();i++) {
    	  String input=inputList.get(i);
    	  try {
          	
          	if(questionType.equalsIgnoreCase("Array")) {
              	Pattern keyValuePattern = Pattern.compile("([^=,]+)=([^=,]+)");
              	Matcher keyValueMatcher = keyValuePattern.matcher(input);
              	while (keyValueMatcher.find()) {
              	    String value = keyValueMatcher.group(2);

              	    if (value.startsWith("[") && value.endsWith("]")) {
              	        // Extract array values
              	        String[] stringArray = value.substring(1, value.length() - 1).split("/");
              	        int[] intArray = new int[stringArray.length];
              	        for (int j = 0; j < stringArray.length; j++) {
              	            intArray[j] = Integer.parseInt(stringArray[j]);
              	        }
              	        String arr="[";
              	        for(int j=0;j<intArray.length-1;j++)
              	        	arr=arr+intArray[j]+",";
              	        arr=arr+intArray[intArray.length-1]+"]";
              	        interpretationCommand=interpretationCommand+" "+arr;
              	    } else {
              	        // Treat value as integer
              	        int intValue = Integer.parseInt(value);
              	        interpretationCommand=interpretationCommand+" "+intValue;
              	    }
              	}
              }else if(questionType.equalsIgnoreCase("String")) {
              	String[] parts = input.split(",");
          		for (String part : parts) {
              	    String[] keyValue = part.split("=");
              	    if (keyValue.length == 2) {
              	        String value = keyValue[1];
              	        interpretationCommand=interpretationCommand+" "+value;
              	    }else if (keyValue.length==1) {
              	    	String value = keyValue[0];
              	        interpretationCommand=interpretationCommand+" "+value;
          			}
              	}
              	
              }
              String interpritionMessage = executeProcess(interpretationCommand);
              if (interpritionMessage.isEmpty()) {
                questionDetailDTO.setCompilationMsg(interpritionMessage);
                log.info("compile code :: exception occurred :: " + interpritionMessage);
                return questionDetailDTO;
              }
              
            if (outputList.get(i).equals(interpritionMessage)) {
              testCaseResult.add(true);
              count.incrementAndGet();
            } else {
              testCaseResult.add(false);
            }
          } catch (MalformedURLException e) {
            throw new RuntimeException(e);
          }
      }

      //deleting the saved java file & java dot class file after completing program execution
      deleteFile(language, counterForTempSaveCode);
     
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

  private Object executeStudentCode(ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IllegalArgumentException, InstantiationException, IOException, InterruptedException {
    log.info("executeStudentCode() -> started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    String questionId=executeAllTestCasesDTO.getQuestionId();
    if (executeAllTestCasesDTO.getFlag() == 1) {
      codeResponseDTO = executeAllTestCases(questionId, executeAllTestCasesDTO);
    } else {
      codeResponseDTO = executeSampleTestCase(questionId,executeAllTestCasesDTO);
    }
    log.info("executeStudentCode() -> end");
    return codeResponseDTO;
  }

  @Cacheable(value = "executeCodeForEveryTestcase", key = "#questionId")
  public CodeResponseDTO executeAllTestCases(String questionId,ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IOException, InterruptedException {
    log.info("executeAllTestCases() -> started");
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    
  //Save Temporary Code
    String result = executeAllTestCasesDTO.getCode().replace("Main", CLASS_NAME+ ++counterForTempSaveCode);
    codeProcessingUtil.saveCodeTemporary(result, executeAllTestCasesDTO.getLanguage(),
         executeAllTestCasesDTO.getStudentId(),counterForTempSaveCode);
    
    codeResponseDTO=javaCodeTestCases(counterForTempSaveCode,executeAllTestCasesDTO.getStudentId(),executeAllTestCasesDTO.getLanguage(),executeAllTestCasesDTO,questionId);
   
    log.info("executeAllTestCases() -> end");
    return codeResponseDTO;
  }

  private Object getTestCaseResponse(int counterForTempSaveCode,TestCases testCase,String questionId,ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IllegalArgumentException, InstantiationException, IOException, InterruptedException {
    log.info("executeAllTestCases() -> started");
    Boolean testCaseResponse;
    
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    String language = executeAllTestCasesDTO.getLanguage();
    String studentId = executeAllTestCasesDTO.getStudentId();
    TestCaseDTO testCases = questionService.getSampleTestCase(questionId);
    
    String interpretationCommand = codeProcessingUtil.interpretationCommand(language, studentId,counterForTempSaveCode);
    interpretationCommand=testCasesResult(interpretationCommand, testCase.getInput(),testCases.getQuestionType());
    String interprationMessage = executeProcess(interpretationCommand);
    if (interprationMessage.isEmpty()) {
 	   codeResponseDTO.setComplilationMessage(interprationMessage);
 	   log.info("runORExecuteAllTestCases code :: compilation error message :: " + interprationMessage);
 	   deleteFile(language, counterForTempSaveCode);
 	   testCaseResponse=false;
 	   return testCaseResponse;
 	}
    
    if (interprationMessage.equals(testCase.getOutput())) {
    	testCaseResponse=true;
    } else {
    	testCaseResponse=false;
    }
    
    log.info("executeAllTestCases() -> end");
    return testCaseResponse;
  }

  public static int counterForTempSaveCode=0;
  private CodeResponseDTO executeSampleTestCase(String questionId,ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IllegalArgumentException, InstantiationException, IOException, InterruptedException {
    log.info("executeSampleTestCase() -> started"); 
    CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    String language = executeAllTestCasesDTO.getLanguage();
    String studentId = executeAllTestCasesDTO.getStudentId();
    
    //Save Temporary Code
    String result = executeAllTestCasesDTO.getCode().replace("Main", CLASS_NAME+ ++counterForTempSaveCode);
    codeProcessingUtil.saveCodeTemporary(result, executeAllTestCasesDTO.getLanguage(),
         executeAllTestCasesDTO.getStudentId(),counterForTempSaveCode);
    
    codeResponseDTO=javaCodeSampleTestCases(counterForTempSaveCode,studentId,language,executeAllTestCasesDTO,questionId);

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
  
  public CodeResponseDTO javaCodeSampleTestCases(int counterForTempSaveCode,String studentId,String language,ExecuteAllTestCasesDTO executeAllTestCasesDTO,String questionId) throws IOException, InterruptedException {
	CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
	//Compile Code
	String compilationCommand = codeProcessingUtil.compilationCommand(language, studentId,counterForTempSaveCode);
	String compilationMessage = executeProcess(compilationCommand);
	ArrayList<Boolean> testCasesSuccess = new ArrayList<Boolean>();
    TestCaseDTO testCases = questionService.getSampleTestCase(questionId);
	if (!compilationMessage.isEmpty()) {
	   codeResponseDTO.setComplilationMessage(compilationMessage);
	   log.info("runORExecuteAllTestCases code :: compilation error message :: " + compilationMessage);
	   deleteFile(language, counterForTempSaveCode);
	   return codeResponseDTO;
	}
    String interpretationCommand = codeProcessingUtil.interpretationCommand(language, studentId,counterForTempSaveCode);
    interpretationCommand=testCasesResult(interpretationCommand, testCases.getInput(),testCases.getQuestionType());
    String interprationMessage = executeProcess(interpretationCommand);
    if (interprationMessage.isEmpty()) {
 	   codeResponseDTO.setComplilationMessage(interprationMessage);
 	   log.info("runORExecuteAllTestCases code :: compilation error message :: " + interprationMessage);
 	   deleteFile(language, counterForTempSaveCode);
 	   testCasesSuccess.add(false);
 	   codeResponseDTO.setTestCasesSuccess(testCasesSuccess);
 	   return codeResponseDTO;
 	}
    if(interprationMessage.equals(testCases.getOutput()))
        testCasesSuccess.add(true);
    else
        testCasesSuccess.add(false);
    
    deleteFile(language, counterForTempSaveCode);
    codeResponseDTO.setTestCasesSuccess(testCasesSuccess);
    return codeResponseDTO;
  }
  
  
  public CodeResponseDTO javaCodeTestCases(int counterForTempSaveCode,String studentId,String language,ExecuteAllTestCasesDTO executeAllTestCasesDTO,String questionId) throws IOException, InterruptedException{
	CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
    List<Callable<Boolean>> taskList = new ArrayList<Callable<Boolean>>();
    List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
    ArrayList<Boolean> testCasesResult = new ArrayList<Boolean>();
    List<TestCases> testCases = questionService.getTestCase(questionId);
    ExecutorService executorService = Executors.newFixedThreadPool(testCases.size());
  //Compile Code
    
  	String compilationCommand = codeProcessingUtil.compilationCommand(executeAllTestCasesDTO.getLanguage(), executeAllTestCasesDTO.getStudentId(),counterForTempSaveCode);
  	String compilationMessage = executeProcess(compilationCommand);
      
  	if (!compilationMessage.isEmpty()) {
  	   codeResponseDTO.setComplilationMessage(compilationMessage);
  	   log.info("runORExecuteAllTestCases code :: compilation error message :: " + compilationMessage);
  	   deleteFile(language, counterForTempSaveCode);
  	   return codeResponseDTO;
  	}

    try {
      for (TestCases testCase : testCases) {
        taskList.add(new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            return (Boolean) getTestCaseResponse(counterForTempSaveCode,testCase, questionId,executeAllTestCasesDTO);
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
	 return codeResponseDTO;
  }
  
  public String testCasesResult(String interpretationCommand,String input,String questionType) {
	  if(questionType.equalsIgnoreCase("Array")) {
	    	Pattern keyValuePattern = Pattern.compile("([^=,]+)=([^=,]+)");
	    	Matcher keyValueMatcher = keyValuePattern.matcher(input);
	    	while (keyValueMatcher.find()) {
	    	    String value = keyValueMatcher.group(2);

	    	    if (value.startsWith("[") && value.endsWith("]")) {
	    	        // Extract array values
	    	        String[] stringArray = value.substring(1, value.length() - 1).split("/");
	    	        int[] intArray = new int[stringArray.length];
	    	        for (int i = 0; i < stringArray.length; i++) {
	    	            intArray[i] = Integer.parseInt(stringArray[i]);
	    	        }
	    	        String arr="[";
	    	        for(int i=0;i<intArray.length-1;i++)
	    	        	arr=arr+intArray[i]+",";
	    	        arr=arr+intArray[intArray.length-1]+"]";
	    	        interpretationCommand=interpretationCommand+" "+arr;
	    	    } else {
	    	        // Treat value as integer
	    	        int intValue = Integer.parseInt(value);
	    	        interpretationCommand=interpretationCommand+" "+intValue;
	    	    }
	    	}
	    }else if(questionType.equalsIgnoreCase("String")) {
//	    	String input=testCases.getInput();
	    	String[] parts = input.split(",");
			for (String part : parts) {
	    	    String[] keyValue = part.split("=");
	    	    if (keyValue.length == 2) {
	    	        String value = keyValue[1];
	    	        interpretationCommand=interpretationCommand+" "+value;
	    	    }else if (keyValue.length==1) {
	    	    	String value = keyValue[0];
	    	        interpretationCommand=interpretationCommand+" "+value;
				}
	    	}
	    	
	    }
	  return interpretationCommand;
  }
  
  
  public void deleteFile(String language,int counterForTempSaveCode) {
	  if(language.equalsIgnoreCase("java")) {
		  File savedJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+counterForTempSaveCode+".java");
	 	   if(savedJavaFile.exists()) {
	 		  	 if (savedJavaFile.delete()) 
	 		  		 log.info(savedJavaFile.getName() + " is successfully deleted");
	 		  	 else
	 		  	     log.info("Failed to delete " + savedJavaFile.getName() + " file");
	 	   }
	 	   File savedClassJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+counterForTempSaveCode+".class");
	 	   if(savedClassJavaFile.exists()) {
	 		  	 if (savedClassJavaFile.delete()) 
	 		  		 log.info(savedJavaFile.getName() + " is successfully deleted");
	 		  	 else
	 		  	     log.info("Failed to delete " + savedClassJavaFile.getName() + " file");
	 	   }
	  }else if(language.equalsIgnoreCase("c")) {
		  File savedJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+counterForTempSaveCode+".c");
	 	   if(savedJavaFile.exists()) {
	 		  	 if (savedJavaFile.delete()) 
	 		  		 log.info(savedJavaFile.getName() + " is successfully deleted");
	 		  	 else
	 		  	     log.info("Failed to delete " + savedJavaFile.getName() + " file");
	 	   }
	 	   File savedClassJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+counterForTempSaveCode+".exe");
	 	   if(savedClassJavaFile.exists()) {
	 		  	 if (savedClassJavaFile.delete()) 
	 		  		 log.info(savedJavaFile.getName() + " is successfully deleted");
	 		  	 else
	 		  	     log.info("Failed to delete " + savedClassJavaFile.getName() + " file");
	 	   }
	  }else if(language.equalsIgnoreCase("cpp")) {
		  File savedJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+counterForTempSaveCode+".cpp");
	 	   if(savedJavaFile.exists()) {
	 		  	 if (savedJavaFile.delete()) 
	 		  		 log.info(savedJavaFile.getName() + " is successfully deleted");
	 		  	 else
	 		  	     log.info("Failed to delete " + savedJavaFile.getName() + " file");
	 	   }
	 	   File savedClassJavaFile = new File(SAVED_CODE_FILE_PATH + CLASS_NAME+counterForTempSaveCode+".exe");
	 	   if(savedClassJavaFile.exists()) {
	 		  	 if (savedClassJavaFile.delete()) 
	 		  		 log.info(savedJavaFile.getName() + " is successfully deleted");
	 		  	 else
	 		  	     log.info("Failed to delete " + savedClassJavaFile.getName() + " file");
	 	   }
	  }else if(language.equalsIgnoreCase("python")) {
		  
	  }
  }
}

