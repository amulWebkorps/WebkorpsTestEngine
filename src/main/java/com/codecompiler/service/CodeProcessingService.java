package com.codecompiler.service;

import java.io.IOException;

import com.codecompiler.dto.*;
import com.codecompiler.entity.StudentTestDetail;

public interface CodeProcessingService {

  StudentTestDetailDTO compileCode(StudentTestDetail studentTestDetail) throws IOException;

  CodeResponseDTO runORExecuteAllTestCases(ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IOException;

  StudentTestDetail saveStudentTestDetail(StudentTestDetailDTO studentTestDetailsDTO);
}
