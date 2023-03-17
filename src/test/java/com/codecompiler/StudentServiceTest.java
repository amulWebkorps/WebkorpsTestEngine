package com.codecompiler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.codecompiler.dto.StudentFinalResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.dto.StudentDTO;
import com.codecompiler.entity.Contest;
import com.codecompiler.entity.Student;
import com.codecompiler.exception.RecordNotFoundException;
import com.codecompiler.exception.UserNotFoundException;
import com.codecompiler.service.ContestService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.StudentService;

@SpringBootTest
public class StudentServiceTest {

  @Autowired
  private StudentService studentService;

  @Autowired
  private QuestionService questionService;

  static Student student = new Student();
  Student savedStudent;

  @Autowired
  private ContestService contestService;

  static Contest contest = new Contest();
  Contest afterSaveContest;

  @BeforeAll
  static void intailizeObject() {
    student.setName("Sarthak Kothari");
    student.setEmail("Sarthak@mail.com");
    student.setMobileNumber("9754635882");
    student.setContestLevel("Level 2");
    student.setStatus(false);
    String characters = "ABCDEFGHLMNOPQRSTUVWXYZabcdghijklmnopqrstuvwxyz0123456789@#$*";
    String pwd = RandomStringUtils.random(7, characters);
    student.setPassword(pwd);
    student.setParticipateDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));

    contest.setContestName("beforeAll");
    contest.setContestDescription("Testing for deployment");
    contest.setContestLevel("Level 1");
    contest.setContestTime("60");
    contest.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
  }

//  @AfterEach
  @Disabled
  void deletObject() {
    studentService.deleteByEmail(savedStudent.getEmail());
  }

  @Test
  public void saveStudentSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertNotNull(savedStudent);
    Assertions.assertNotNull(savedStudent.getId());
    Assertions.assertNotNull(savedStudent.getEmail());
    Assertions.assertTrue(studentService.findById(savedStudent.getId()) != null);
  }

  @Test
  public void saveStudentFailureTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.saveStudent(null));
    Student studentTest = new Student();
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.saveStudent(studentTest));
  }

  @Test
  public void findByIdSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertNotNull(studentService.findById(savedStudent.getId()));
    Assertions.assertEquals(savedStudent.getId(), studentService.findById(savedStudent.getId()).getId());
  }

  @Test
  public void findByIdFailureTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.findById(null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findById(""));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findById(" "));
    Assertions.assertThrows(UserNotFoundException.class,
        () -> studentService.findById(savedStudent.getId() + "-4tfed4"));
  }

  @Test
  public void findByEmailAndPasswordSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertNotNull(
        studentService.findByEmailAndPassword(savedStudent.getEmail(), savedStudent.getPassword()));
    Assertions.assertEquals(savedStudent.getId(),
        studentService.findByEmailAndPassword(savedStudent.getEmail(), savedStudent.getPassword()).getId());
  }

  @Test
  public void findByEmailAndPasswordFailureTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.findByEmailAndPassword(null, null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findByEmailAndPassword("", ""));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findByEmailAndPassword(" ", " "));
    Assertions.assertThrows(UserNotFoundException.class, () -> studentService
        .findByEmailAndPassword(savedStudent.getEmail(), savedStudent.getPassword() + "-4tfed4"));
  }

  @Test
  public void findByEmailSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertNotNull(studentService.findByEmail(savedStudent.getEmail()));
    Assertions.assertEquals(savedStudent.getId(), studentService.findByEmail(savedStudent.getEmail()).getId());
  }

  @Test
  public void findByEmailFailureTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.findByEmail(null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findByEmail(""));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findByEmail(" "));
    Assertions.assertThrows(UserNotFoundException.class,
        () -> studentService.findByEmail(savedStudent.getId() + "-4tfed4"));
  }

  @Test
  public void findByContestIdSuccessTest() {
    afterSaveContest = contestService.saveContest(contest);
    student.setContestId(afterSaveContest.getContestId());
    savedStudent = studentService.saveStudent(student);
    Assertions.assertNotNull(studentService.findByContestId(afterSaveContest.getContestId()));
    List<StudentDTO> studentDTO = studentService.findByContestId(savedStudent.getContestId());
    for (StudentDTO student : studentDTO) {
      if (student.getId().equals(savedStudent.getId())) {
        Assertions.assertEquals(savedStudent.getId(), student.getId());
        break;
      }
    }
    contestService.deleteContest(afterSaveContest.getContestId());
  }

  @Test
  public void findByContestIdFailureTest() {
    savedStudent = studentService.saveStudent(student);
    afterSaveContest = contestService.saveContest(contest);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.findByContestId(null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findByContestId(""));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.findByContestId(" "));
    Assertions.assertThrows(RecordNotFoundException.class,
        () -> studentService.findByContestId(savedStudent.getEmail() + "-4tfed4"));
    contestService.deleteContest(afterSaveContest.getContestId());
  }

  @Test
  public void findEmailByStatusSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertNotNull(studentService.findEmailByStatus(savedStudent.getStatus()));
    Assertions.assertEquals(savedStudent.getId(), studentService.findByEmail(savedStudent.getEmail()).getId());
  }

  @Test
  public void findEmailByStatusFailureTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.findEmailByStatus(null));

  }

  @Test
  public void deleteByEmailSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    Student student = studentService.deleteByEmail(savedStudent.getEmail());
    Assertions.assertNotNull(savedStudent);
    Assertions.assertEquals(savedStudent.getId(), student.getId());
    savedStudent = studentService.saveStudent(student);
  }

  @Test
  public void deleteByEmailFailureTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.deleteByEmail(null));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.deleteByEmail(""));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.deleteByEmail(" "));
    Assertions.assertThrows(UserNotFoundException.class, () -> studentService.deleteByEmail(savedStudent.getEmail() + "test"));
  }

  @Test
  public void finalSubmitContestSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    System.out.println("StudentServiceTest.finalSubmitContestSuccessTest() " + savedStudent.getId());
    Student student = studentService.finalSubmitContest(savedStudent.getId(), 50.96);
    Assertions.assertNotNull(student);
    Assertions.assertEquals(null, student.getPassword());
  }

  @Test
  public void finalSubmitContestFailureTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertThrows(NullPointerException.class, () -> studentService.finalSubmitContest(null, 0.0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.finalSubmitContest("", 0.0));
    Assertions.assertThrows(IllegalArgumentException.class, () -> studentService.finalSubmitContest(" ", 0.0));
//		Assertions.assertThrows(RecordMisMatchedException.class, () -> studentService.finalSubmitContest(savedStudent.getEmail()));
//		Assertions.assertThrows(NullPointerException.class, () -> studentService.finalSubmitContest(savedStudent.getEmail()));
  }

  @Test
  public void findAllSuccessTest() {
    savedStudent = studentService.saveStudent(student);
    Assertions.assertNotNull(savedStudent);
    Assertions.assertTrue(studentService.findAll().size() > 0);
  }

  @Test
  public void evaluateStudentTestResultSuccessTest() {
    String contestId = "62f3ba1e9c7ec130d623e47f"; //"633468d1abd43a63776b303b";
    final List<StudentFinalResponse> studentFinalResponses = this.studentService.evaluateStudentTestResult(contestId);
    Assertions.assertNotNull(studentFinalResponses);
  }
}
