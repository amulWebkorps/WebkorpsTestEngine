package com.codecompiler;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.dto.CodeDetailsDTO;
import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.dto.ExecuteAllTestCasesDTO;
import com.codecompiler.service.CodeProcessingService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class CodeProcessingServiceTest {

  @Autowired
  private CodeProcessingService codeProcessingService;
  private static ExecuteAllTestCasesDTO executeAllTestCasesDTO = new ExecuteAllTestCasesDTO();
 private static  ExecuteAllTestCasesDTO executeAllTestCasesDTO1 = new ExecuteAllTestCasesDTO();
  @BeforeEach
  void initializeObject() {
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

  }
  @Test
  public void compileCodeSuccessTest() throws IOException {
    for (int i=0; i<50; i++) {
      CodeResponseDTO codeResponse = codeProcessingService.runORExecuteAllTestCases(executeAllTestCasesDTO);
      Assertions.assertNotNull(codeResponse.getTestCasesSuccess()!=null);
    }
  }
}
