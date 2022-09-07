package com.codecompiler.service;

import java.io.IOException;
import java.util.Map;

import com.codecompiler.dto.CodeResponseDTO;

public interface CodeProcessingService {

	public CodeResponseDTO compileCode(Map<String, Object> data) throws IOException;

}
