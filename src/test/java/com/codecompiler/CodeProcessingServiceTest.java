package com.codecompiler;

import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.dto.ExecuteAllTestCasesDTO;
import com.codecompiler.dto.QuestionDetailDTO;
import com.codecompiler.dto.StudentTestDetailDTO;
import com.codecompiler.entity.StudentTestDetail;
import com.codecompiler.exception.StudentNotFoundException;
import com.codecompiler.service.CodeProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@Slf4j
public class CodeProcessingServiceTest {

  @Autowired
  private CodeProcessingService codeProcessingService;
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO = new ExecuteAllTestCasesDTO();
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO1 = new ExecuteAllTestCasesDTO();
  private static final StudentTestDetailDTO studentTestDetailDTO = new StudentTestDetailDTO();

  @BeforeAll
  public static void initializeObject() {
    executeAllTestCasesDTO.setStudentId("4150cb93-7973-4996-ad68-d5cc85debef4");
    executeAllTestCasesDTO.setQuestionId("70825abb-5f57-4400-a656-c8627f5ebe46"); // ad79957b-75c0-4ec8-abff-77bcd580dc25
    executeAllTestCasesDTO.setLanguage("Java");
    executeAllTestCasesDTO.setFlag(1);
    executeAllTestCasesDTO.setCode("public class Main{\n  public static void main(String args[]){\n// \tint n = Integer.parseInt(args[0]); // taking parameter from command line and convert to int\n \n \t//write your code here\n       \n    String str = \"\";\n    String s = args[0];\n    \n    for(int i=s.length()-1; i>=0; i--)\n      str+=s.charAt(i);\n      \n    System.out.println(str);\n  }\n}");

    executeAllTestCasesDTO1.setStudentId("522376b5-5dd2-4fe1-9de8-eeef5f9cfe04");
    executeAllTestCasesDTO1.setQuestionId("baa50b79-f39d-4358-bf68-c2552b82b406");
    executeAllTestCasesDTO1.setLanguage("Java");
    executeAllTestCasesDTO1.setFlag(1);
    executeAllTestCasesDTO1.setCode("public class Main{\n public static void main(String args[]){\n int input = Integer.parseInt(args[0]); \n int factorial = 0; \n\n	if(input == 0) \nthrow new RuntimeException('invalid input');\n \n	for(int number = 1; number <=input; number++) \n factorial *= number;\n	System.out.println(factorial); \n }\n}");

//preparing data for saveStudentTestDetailSuccessTest()
    Set<QuestionDetailDTO> questionDetailDTOS = new HashSet<>();
    //preparing QuestionDetailDTO
    QuestionDetailDTO questionDetailDTO = new QuestionDetailDTO();
    questionDetailDTO.setQuestionId("baa50b79-f39d-4358-bf68-c2552b82b406");
    questionDetailDTO.setCode("public class Main{\n public static void main(String args[]){\n int input = Integer.parseInt(args[0]); \n int factorial = 0; \n\n	if(input == 0) \nthrow new RuntimeException('invalid input');\n \n	for(int number = 1; number <=input; number++) \n factorial *= number;\n	System.out.println(factorial); \n }\n}");

    QuestionDetailDTO questionDetailDTO1 = new QuestionDetailDTO();
    questionDetailDTO1.setQuestionId("70825abb-5f57-4400-a656-c8627f5ebe46");
    questionDetailDTO1.setCode("public class Main{\n  public static void main(String args[]){\n// \tint n = Integer.parseInt(args[0]); // taking parameter from command line and convert to int\n \n \t//write your code here\n       \n    String str = \"\";\n    String s = args[0];\n    \n    for(int i=s.length()-1; i>=0; i--)\n      str+=s.charAt(i);\n      \n    System.out.println(str);\n  }\n}");

    QuestionDetailDTO questionDetailDTO2 = new QuestionDetailDTO();
    questionDetailDTO2.setQuestionId("bcc50b79-f39d-4358-bf68-c2552b82b406");
    questionDetailDTO2.setCode("public class Main{\n public static void main(String args[]){\n int input = Integer.parseInt(args[0]); \n int factorial = 0; \n\n	if(input == 0) \nthrow new RuntimeException('invalid input');\n \n	for(int number = 1; number <=input; number++) \n factorial *= number;\n	System.out.println(factorial); \n }\n}");

    questionDetailDTOS.add(questionDetailDTO);
    questionDetailDTOS.add(questionDetailDTO1);
    questionDetailDTOS.add(questionDetailDTO2);

// preparing StudentTestDetailDTO
    studentTestDetailDTO.setStudentId("522376b5-5dd2-4fe1-9de8-eeef5f9cfe04");
    studentTestDetailDTO.setContestId("62f3ba1e9c7ec130d623e47f");
    studentTestDetailDTO.setCodeLanguage("Java");
    studentTestDetailDTO.setQuestionDetails(questionDetailDTOS);
  }

  @Test
  public void compileCodeSuccessTest() throws IOException {
    //This test case is created for load testing
    for (int i = 0; i < 1; i++) {
      CodeResponseDTO codeResponse = codeProcessingService.runORExecuteAllTestCases(executeAllTestCasesDTO);
      Assertions.assertNotNull(codeResponse.getTestCasesSuccess());
    }
  }

  @Test
  public void saveStudentTestDetailSuccessTest() {
    final StudentTestDetail savedStudentTestDetail = codeProcessingService.saveStudentTestDetail(studentTestDetailDTO);
    Assertions.assertNotNull(savedStudentTestDetail);
    Assertions.assertFalse(savedStudentTestDetail.getContestId().isBlank());
    Assertions.assertEquals(studentTestDetailDTO.getStudentId(), savedStudentTestDetail.getStudentId());
  }

  @Test
  public void saveStudentTestDetailFailureTest() {
    StudentTestDetailDTO studentTestDetailDTO1 = new StudentTestDetailDTO();
    studentTestDetailDTO1.setStudentId("");
    Assertions.assertThrows(StudentNotFoundException.class, () -> codeProcessingService.saveStudentTestDetail(studentTestDetailDTO1));
    Assertions.assertThrows(NullPointerException.class, () -> codeProcessingService.saveStudentTestDetail(new StudentTestDetailDTO()));
  }
}
