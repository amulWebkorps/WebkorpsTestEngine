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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Slf4j
public class CodeProcessingServiceTest {

  @Autowired
  private CodeProcessingService codeProcessingService;
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO = new ExecuteAllTestCasesDTO();
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO1 = new ExecuteAllTestCasesDTO();
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO2 = new ExecuteAllTestCasesDTO();
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO3 = new ExecuteAllTestCasesDTO();
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO4 = new ExecuteAllTestCasesDTO();
  private static final ExecuteAllTestCasesDTO executeAllTestCasesDTO5 = new ExecuteAllTestCasesDTO();


  private static final StudentTestDetailDTO studentTestDetailDTO = new StudentTestDetailDTO();

  @BeforeAll
  public static void initializeObject() {
    executeAllTestCasesDTO.setStudentId("4150cb93-7973-4996-ad68-d5cc85debef4");
    executeAllTestCasesDTO.setQuestionId("70825abb-5f57-4400-a656-c8627f5ebe46");
    executeAllTestCasesDTO.setLanguage("Java");
    executeAllTestCasesDTO.setFlag(1);
    executeAllTestCasesDTO.setCode("public class Main{\n  public static void main(String args[]){\n// \tint n = Integer.parseInt(args[0]); // taking parameter from command line and convert to int\n \n \t//write your code here\n       \n    String str = \"\";\n    String s = args[0];\n    \n    for(int i=s.length()-1; i>=0; i--)\n      str+=s.charAt(i);\n      \n    System.out.println(str);\n  }\n}");

    executeAllTestCasesDTO1.setStudentId("522376b5-5dd2-4fe1-9de8-eeef5f9cfe04");
    executeAllTestCasesDTO1.setQuestionId("baa50b79-f39d-4358-bf68-c2552b82b406");
    executeAllTestCasesDTO1.setLanguage("Java");
    executeAllTestCasesDTO1.setFlag(1);
    executeAllTestCasesDTO1.setCode("public class Main{\n public static void main(String args[]){\n int input = Integer.parseInt(args[0]); \n int factorial = 0; \n\n	if(input <= 0) \nthrow new RuntimeException('invalid input');\n \n	for(int number = input; number >=1; number--) \n factorial *= number;\n	System.out.println(factorial); \n }\n}");

    executeAllTestCasesDTO2.setStudentId("522376b5-5dd2-4fe1-9de8-eeef5f9cfe04");
    executeAllTestCasesDTO2.setQuestionId("ad79957b-75c0-4ec8-abff-77bcd580dc25");
    executeAllTestCasesDTO2.setLanguage("Java");
    executeAllTestCasesDTO2.setFlag(1);
    executeAllTestCasesDTO2.setCode("public class Main{\n  public static void main(String args[]){\n// \tint n = Integer.parseInt(args[0]); // taking parameter from command line and convert to int\n \n \t//write your code here\n       \n    String str = \"\";\n    String s = args[0];\n    \n    for(int i=s.length()-1; i>=0; i--)\n      str+=s.charAt(i);\n      \n    System.out.println(str);\n  }\n}");

    executeAllTestCasesDTO3.setStudentId("37ea734d-5ac8-4bf2-9c09-71fca66eeae1");
    executeAllTestCasesDTO3.setQuestionId("8abc27ec-6ba5-46da-9ae1-1e8f3c8e519d");
    executeAllTestCasesDTO3.setLanguage("Java");
    executeAllTestCasesDTO3.setFlag(1);
    executeAllTestCasesDTO3.setCode("public class Main {\n public static void main(String[] args){  \n String s = args[0]; \n String updatedString = str.replaceAll('[aeiuoAEIUO]','');\n System.out.println(updatedString); \n }\n}");

    executeAllTestCasesDTO4.setStudentId("37ea734d-5ac8-4bf2-9c09-71fca66eeae1");
    executeAllTestCasesDTO4.setQuestionId("d365ee7b-3754-45ce-b68a-85c69eea9866");
    executeAllTestCasesDTO4.setLanguage("Java");
    executeAllTestCasesDTO4.setFlag(1);
    executeAllTestCasesDTO4.setCode("public class Main { \n public static void main(String args[]){ \n int number = Integer.parseInt(args[0]); \n for(int i=1; i<=number; i++) {\n for(int j=i; j>=1; j--) {\n System.out.print('*'); \n } \n System.out.println(); \n } \n }\n}");

    executeAllTestCasesDTO5.setStudentId("4047534a-5f46-4f51-8378-43f3b3644068");
    executeAllTestCasesDTO5.setQuestionId("eb568d9d-59d9-4411-8a22-93fc60594c4a");
    executeAllTestCasesDTO5.setLanguage("Java");
    executeAllTestCasesDTO5.setFlag(1);
    executeAllTestCasesDTO5.setCode("public class Main { \n public static void main(String args[]){ \n String str = args[0];\n int middleFinger = str.length()/2; \n System.out.println(str.charAt(middleFinger)); }\n}");


//preparing data for saveStudentTestDetailSuccessTest()
    List<QuestionDetailDTO> questionDetailDTOS = new ArrayList<>();
    //preparing QuestionDetailDTO
    QuestionDetailDTO questionDetailDTO = new QuestionDetailDTO();
    questionDetailDTO.setQuestionId("eb568d9d-59d9-4411-8a22-93fc60594c4a");
    questionDetailDTO.setCode("public class Main { \n public static void main(String args[]){ \n String str = args[0];\n int middleFinger = str.length()/2; \n System.out.println(str.charAt(middleFinger)); }\n}");

    QuestionDetailDTO questionDetailDTO1 = new QuestionDetailDTO();
    questionDetailDTO1.setQuestionId("d365ee7b-3754-45ce-b68a-85c69eea9866");
    questionDetailDTO1.setCode("public class Main { \n public static void main(String args[]){ \n int number = Integer.parseInt(args[0]); \n for(int i=1; i<=number; i++) {\n for(int j=i; j>=1; j--) {\n System.out.print('*'); \n } \n System.out.println(); \n } \n }\n}");

    QuestionDetailDTO questionDetailDTO2 = new QuestionDetailDTO();
    questionDetailDTO2.setQuestionId("ad79957b-75c0-4ec8-abff-77bcd580dc25");
    questionDetailDTO2.setCode("public class Main{\n  public static void main(String args[]){\n// \tint n = Integer.parseInt(args[0]); // taking parameter from command line and convert to int\n \n \t//write your code here\n       \n    String str = \"\";\n    String s = args[0];\n    \n    for(int i=s.length()-1; i>=0; i--)\n      str+=s.charAt(i);\n      \n    System.out.println(str);\n  }\n}");

    questionDetailDTOS.add(questionDetailDTO);
    questionDetailDTOS.add(questionDetailDTO1);
    questionDetailDTOS.add(questionDetailDTO2);

// preparing StudentTestDetailDTO
    studentTestDetailDTO.setStudentId("4150cb93-7973-4996-ad68-d5cc85debef4");
    studentTestDetailDTO.setContestId("633468d1abd43a63776b303b");
//    studentTestDetailDTO.setCodeLanguage("Java");
//    studentTestDetailDTO.setQuestionDetails(questionDetailDTOS);
  }

  @Test
  public void compileCodeSuccessTest() throws IOException {
    //This test case is created for load testing
//    for (int i = 1; i <=1; i++) {
//      CodeResponseDTO codeResponse = codeProcessingService.runORExecuteAllTestCases(executeAllTestCasesDTO1);
//      Assertions.assertNotNull(codeResponse.getTestCasesSuccess());
//    }

    List<ExecuteAllTestCasesDTO> executeAllTestCasesDTOS = new ArrayList<>();
    executeAllTestCasesDTOS.add(executeAllTestCasesDTO);
    executeAllTestCasesDTOS.add(executeAllTestCasesDTO1);
    executeAllTestCasesDTOS.add(executeAllTestCasesDTO2);
    executeAllTestCasesDTOS.add(executeAllTestCasesDTO3);
    executeAllTestCasesDTOS.add(executeAllTestCasesDTO4);
    executeAllTestCasesDTOS.add(executeAllTestCasesDTO5);
    for (ExecuteAllTestCasesDTO object:executeAllTestCasesDTOS) {
      CodeResponseDTO codeResponse = codeProcessingService.runORExecuteAllTestCases(object);
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
