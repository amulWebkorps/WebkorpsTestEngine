package com.codecompiler.service;

import java.io.IOException;

import com.codecompiler.dto.CodeDetailsDTO;
import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.dto.ExecuteAllTestCasesDTO;

public interface CodeProcessingService {

	public CodeResponseDTO compileCode(CodeDetailsDTO codeDetailsDTO) throws IOException;

	public CodeResponseDTO executeAllTestCases(ExecuteAllTestCasesDTO executeAllTestCasesDTO) throws IOException;

}
