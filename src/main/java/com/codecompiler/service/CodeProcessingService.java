package com.codecompiler.service;

import java.io.IOException;

import com.codecompiler.dto.CodeDetailsDTO;
import com.codecompiler.dto.CodeResponseDTO;

public interface CodeProcessingService {

	public CodeResponseDTO compileCode(CodeDetailsDTO codeDetailsDTO) throws IOException;

}
