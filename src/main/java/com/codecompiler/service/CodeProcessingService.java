package com.codecompiler.service;

import java.io.IOException;

import com.codecompiler.dto.*;
import com.codecompiler.entity.StudentTestDetail;

public interface CodeProcessingService {

  public CodeResponseDTO
  compileCode(CodeDetailsDTO codeDetailsDTO) throws IOException;

  public CodeResponseDTO runORExecuteAllTestCases(ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IOException;


  StudentTestDetail saveStudentTestDetail(StudentTestDetailDTO studentTestDetailsDTO);
}
