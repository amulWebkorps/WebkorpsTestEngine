package com.codecompiler.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.entity.MCQ;

public interface MCQService {

	List<MCQ> saveFileForBulkMCQQuestion(MultipartFile file, String contestId) throws IOException;
	
	

}
