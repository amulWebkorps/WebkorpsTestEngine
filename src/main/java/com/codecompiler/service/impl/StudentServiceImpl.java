package com.codecompiler.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.codecompiler.dto.*;
import com.codecompiler.entity.StudentTestDetail;
import com.codecompiler.repository.StudentTestDetailRepository;
import com.codecompiler.service.CodeProcessingService;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.dto.MyCellDTO;
import com.codecompiler.dto.ParticipantDTO;
import com.codecompiler.dto.StudentDTO;
import com.codecompiler.dto.TestCaseDTO;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.Student;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.UnSupportedFormatException;
import com.codecompiler.exception.UserNotFoundException;
import com.codecompiler.helper.ExcelPOIHelper;
import com.codecompiler.repository.StudentRepository;
import com.codecompiler.service.ExcelConvertorService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.StudentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private StudentTestDetailRepository studentTestDetailRepository;

  @Autowired
  private QuestionService questionService;

  @Autowired
  private CodeProcessingService codeProcessingService;

  @Resource(name = "excelPOIHelper")
  private ExcelPOIHelper excelPOIHelper;

  @Autowired
  private ExcelConvertorService excelConvertorService;

  public Student findById(String studentId) {
    log.info("findById:: has started with studentId: " + studentId);
    if (studentId == null)
      throw new NullPointerException();
    else if (studentId.isBlank())
      throw new IllegalArgumentException();
    Student student = studentRepository.findById(studentId);
    if (student == null) {
      throw new UserNotFoundException("Student with id :: " + studentId + " does not found");
    }
    log.info("findById:: ended with Student: " + student.toString());
    return student;
  }

  public Student findByEmailAndPassword(String email, String password) {
    if (email == null || password == null)
      throw new NullPointerException();
    if (email.isBlank() || password.isBlank())
      throw new IllegalArgumentException(
          "Method parameter should not be blank or should not contain whitespace only");
    Student student = studentRepository.findByEmailAndPassword(email, password);
    if (student == null) {
      throw new UserNotFoundException("Student with id :: " + email + " does not found");
    }
    return student;
  }

  public Student findByEmail(String studentEmail) {
    if (studentEmail == null)
      throw new NullPointerException();
    else if (studentEmail.isBlank())
      throw new IllegalArgumentException();
    Student student = studentRepository.findByEmail(studentEmail);
    if (student == null) {
      throw new UserNotFoundException("Student with id :: " + studentEmail + " does not found");
    }
    return student;
  }

  public List<StudentDTO> findByContestId(String contestId) {
    if (contestId == null)
      throw new NullPointerException();
    else if (contestId.isBlank())
      throw new IllegalArgumentException();
    log.info("findByContestId:: has started with contestId: " + contestId);
    List<Student> students = studentRepository.findByContestId(contestId);
    if (students == null || students.size() == 0) {
      throw new RecordNotFoundException("No Student Found in Contest with id ::" + contestId);
    }
    List<StudentDTO> studentDetails = new ArrayList<StudentDTO>();
    for (Student student : students) {
      StudentDTO studentDto = new StudentDTO();
      studentDto.setId(student.getId());
      studentDto.setEmail(student.getEmail());
      studentDetails.add(studentDto);
    }
    log.info("findByContestId:: has been ended with studentDetails" + studentDetails.size());
    return studentDetails;
  }

  public Student saveStudent(Student studentDetails) {
    if (studentDetails == null) {
      throw new NullPointerException();
    }
    if (studentDetails.getEmail() == null)
      throw new IllegalArgumentException("Student entity must contain email id");
    return studentRepository.save(studentDetails);
  }

  public List<String> findEmailByStatus(Boolean True) {
    if (True == null) {
      throw new NullPointerException();
    }
    List<Student> sentMail = studentRepository.findEmailByStatus(True);
    return sentMail.stream().map(Student::getEmail).collect(Collectors.toList());
  }

  @Override
  public List<String> saveFileForBulkParticipator(MultipartFile file) {
    log.info("saveFileForBulkParticipator:: has started");
    if (!ExcelConvertorService.checkExcelFormat(file)) {
      throw new UnSupportedFormatException("Please check excel file format");
    }
    List<Student> uploadParticipator = new ArrayList<>();
    try {
      Map<Integer, List<MyCellDTO>> data = excelPOIHelper.readExcel(file.getInputStream(),
          file.getOriginalFilename());
      uploadParticipator = excelConvertorService.convertExcelToListOfStudent(data);
      studentRepository.saveAll(uploadParticipator);
      log.info("saveFileForBulkParticipator:: bulk participators saved successfully");
    } catch (IOException e) {
      log.info("Exception occurs in saveFileForBulkParticipator: " + e.getMessage());
    }
    return uploadParticipator.stream().map(Student::getEmail).collect(Collectors.toList());
  }

  public Student deleteByEmail(String emailId) {
    log.info("deleteByEmail:: started with an email: " + emailId);
    if (emailId == null)
      throw new NullPointerException();
    else if (emailId.isBlank())
      throw new IllegalArgumentException();
    Student student = studentRepository.findByEmail(emailId);
    if (student == null)
      throw new UserNotFoundException("User with email :: " + emailId + " not found");
    return studentRepository.deleteByEmail(emailId);
  }

  public Student updateStudentDetails(String studentId, String contestId, Set<String> questionIds,
      ArrayList<Boolean> testCasesSuccess, String compilationMessage, String fileName) {
    log.info("updateStudentDetails() : has started");
    TestCaseDTO testCaseRecord = new TestCaseDTO();
    List<TestCaseDTO> testCasesRecord1 = new ArrayList<>(); // need to remove in future
    testCaseRecord.setQuestionId(questionIds);
    testCaseRecord.setFileName(fileName);
    testCaseRecord.setComplilationMessage(compilationMessage);
    testCaseRecord.setTestCasesSuccess(testCasesSuccess); // create new collection for testCasesRecord and save that
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

    // Old API implementation, updating password field with null
    // Need to discuss on this
    Student student = this.studentRepository.findById(studentId);
    student.setPassword(null);
    studentRepository.save(student);

    // new API implementation, Updating studentPercentage Field
    StudentTestDetail savedStudentDetail = this.studentTestDetailRepository.findByStudentId(studentId);
    System.out.println("StudentServiceImpl.updateStudentPercentage() " + savedStudentDetail.getId());
    savedStudentDetail.setPercentage(percentage);

    return this.studentTestDetailRepository.save(savedStudentDetail);
  }

  public List<ParticipantDTO> findByContestIdForMCQ(String contestId) {
    if (contestId == null)
      throw new NullPointerException();
    else if (contestId.isBlank())
      throw new IllegalArgumentException();

    log.info("findByContestId:: has started with contestId: " + contestId);
    List<Student> students = studentRepository.findByContestId(contestId);
    if (students == null || students.size() == 0) {
      throw new RecordNotFoundException("No Student Found in Contest with id ::" + contestId);
    }
    List<ParticipantDTO> studentDetails = new ArrayList<ParticipantDTO>();
    for (Student student : students) {
      ParticipantDTO participantDTO = new ParticipantDTO();
      participantDTO.setEmail(student.getEmail());
      participantDTO.setPercentage(student.getPercentage());
      if (!(student.getFinalMailSent().equals("SuccessFullSent"))) {
        studentDetails.add(participantDTO);
      }
    }
    log.info("findByContestId:: has been ended with studentDetails" + studentDetails.size());
    return studentDetails;
  }

  public List<String> findEmailByfinalMailSent() {
    List<Student> sentMail = studentRepository.findEmailByfinalMailSent("SuccessFullSent");
    return sentMail.stream().map(Student::getEmail).collect(Collectors.toList());
  }

  @Override
  public List<String> findAll() {
    List<Student> presentStudent = studentRepository.findEmailByStatus(false);
    List<String> emailList = presentStudent.stream().map(Student::getEmail).collect(Collectors.toList());
    if (emailList.isEmpty()) {
      throw new RecordNotFoundException("No Participator is in active state");
    }
    return emailList;
  }

  @Override
  public Map<String, Object> getParticipatorDetail(String studentId) throws IOException {
    log.info("getParticipatorDetail:: has started with studentId: " + studentId);
    Student student = this.findById(studentId);
    if (student.getQuestionId() == null) {
      throw new RecordNotFoundException("Participant did not submit a single Question");
    }
    log.info("getParticipatorDetail:: student :" + student.toString());
    List<TestCaseDTO> testCaseDTO = student.getTestCaseRecord();
    List<TestCaseDTO> testCaseDTOTemp = new ArrayList<>();
    for (TestCaseDTO editTestCaseDTO : testCaseDTO) {
      BufferedReader br = new BufferedReader(new FileReader(
          new File("src/main/resources/CodeSubmittedByCandidate/" + editTestCaseDTO.getFileName())));
      String line;
      String code = "";
      while ((line = br.readLine()) != null)
        code += line + "\n";
      editTestCaseDTO.setFileName(code);
      testCaseDTOTemp.add(editTestCaseDTO);
    }
    student.setTestCaseRecord(testCaseDTOTemp);
    Map<String, Object> mp = new HashedMap<>();
    List<Question> questionDetail = new ArrayList<>();
    for (String questionId : student.getQuestionId()) {
      Question question = questionService.findByQuestionId(questionId);
      Question questionTemp = new Question();
      questionTemp.setQuestionId(question.getQuestionId());
      questionTemp.setQuestion(question.getQuestion());
      questionTemp.setSampleTestCase(question.getSampleTestCase());
      questionDetail.add(questionTemp);

    }
    mp.put("studentDetail", student);
    mp.put("questionSubmitedByStudent", questionDetail);
    return mp;
  }

  @Override
  public List<String> filterParticipants(String filterByString) {
    List<String> totalParticipantsByFilter = new ArrayList<String>();
    if (filterByString == null || filterByString.isBlank()) {
      throw new NullPointerException("Method parameter should be Level 1, Level 2 or All only");
    }
    if (filterByString.equals("Level 1") || filterByString.equals("Level 2"))
      totalParticipantsByFilter = findByContestLevel(filterByString);
    else
      totalParticipantsByFilter = findAll();
    return totalParticipantsByFilter;
  }

  public List<String> findByContestLevel(String filterByString) {
    if (filterByString == null)
      throw new NullPointerException();
    else if (filterByString.isBlank())
      throw new IllegalArgumentException();
    List<Student> participants = studentRepository.findByContestLevelAndStatus(filterByString, false);
    return participants.stream().map(Student::getEmail).collect(Collectors.toList());
  }

  @Override
  public Student finalSubmitContest(String studentId, double percentage) {
    if (studentId == null)
      throw new NullPointerException();
    else if (studentId.isBlank())
      throw new IllegalArgumentException();
    Student student = this.studentRepository.findById(studentId);
    System.out.println("StudentServiceImpl.finalSubmitContest() " + student.getId());
    student.setPassword(null);
    student.setPercentage(percentage);
    return studentRepository.save(student);
  }

  public List<StudentFinalResponse> evaluateStudentTestResult(String contestId) {
    log.info("evaluateStudentTestResult() :: has started with contestId: " + contestId);
    List<StudentFinalResponse> studentsFinalResponse = new ArrayList<>();
    if (contestId == null || contestId.isBlank())
      throw new NullPointerException();

    List<StudentTestDetail> participatedStudents = this.studentTestDetailRepository.findByContestId(contestId);
    if (participatedStudents == null || participatedStudents.size() == 0) {
      log.info("evaluateStudentTestResult() :: participants not found for this contestId " + contestId);
      throw new RecordNotFoundException("No Student Found in Contest with id ::" + contestId);
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    for (StudentTestDetail studentTestDetail : participatedStudents) {
      StudentFinalResponse studentFinalResponse = new StudentFinalResponse();
      Future<StudentTestDetailDTO> studentTestDetailFutureResult = executorService
          .submit(new Callable<StudentTestDetailDTO>() {
            @Override
            public StudentTestDetailDTO call() throws Exception {
              return codeProcessingService.compileCode(studentTestDetail);
            }
          });
      try {
        StudentTestDetailDTO updatedStudentTestDetail = studentTestDetailFutureResult.get();
        studentFinalResponse.setStudentId(updatedStudentTestDetail.getStudentId());
        studentFinalResponse.setStudentPercentage(updatedStudentTestDetail.getStudentPercentage());
        Student student = this.studentRepository.findById(updatedStudentTestDetail.getStudentId());
        studentFinalResponse.setStudentEmail(student.getEmail());
        studentsFinalResponse.add(studentFinalResponse);
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException("Something went wrong, Please contact to HR " + e.getMessage());
      }
    }
    executorService.shutdownNow();
    log.info("evaluateStudentTestResult() :: has been ended with studentDetails" + studentsFinalResponse.size());
    return studentsFinalResponse;
  }
  
  public List<ParticipantDTO> findByContestIdForProgramming(String contestId){
	  if (contestId == null)
	      throw new NullPointerException();
	    else if (contestId.isBlank())
	      throw new IllegalArgumentException();

	    log.info("findByContestId:: has started with contestId: " + contestId);
	    List<StudentTestDetail> students = studentTestDetailRepository.findByContestId(contestId);
	    
	    List<ParticipantDTO> studentResult = students.stream().map(student -> {
	    	System.err.println("ID : "+student.getStudentId());
	        Student s = studentRepository.findById(student.getStudentId());
	        System.out.println("STUDENT : "+s);
	        ParticipantDTO participant = new ParticipantDTO();
	        if(s!=null) {
		        participant.setEmail(s.getEmail());
		        participant.setPercentage(student.getPercentage());
		        participant.setStatus(s.getStatus());
		        participant.setId(s.getId());
	        }
	        return participant;
	    }).collect(Collectors.toList());
	    
	    log.info("findByContestId:: has been ended with studentDetails" + studentResult.size());
	    return studentResult;
  }
}
