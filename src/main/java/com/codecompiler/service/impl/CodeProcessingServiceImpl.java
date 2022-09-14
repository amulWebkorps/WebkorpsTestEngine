package com.codecompiler.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dto.CodeDetailsDTO;
import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.entity.TestCases;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.StudentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CodeProcessingServiceImpl implements CodeProcessingService {

	@Autowired
	private StudentService studentService;

	@Autowired
	private QuestionService questionService;
	
	private static String compilationCommand(String language) {
		String command = null;
		if (language.equalsIgnoreCase("java")) {
			command = "javac Main.java";
		} else if (language.equalsIgnoreCase("python")) {
			command = "src/main/resources/temp/HelloPython.py";
		} else if (language.equalsIgnoreCase("cpp")) {
			command = "g++ HelloCPP.cpp -o exeofCPP";
		} else if (language.equalsIgnoreCase("c")) {
			command = "gcc HelloC.c -o exeofc";
		}
		return command;
	}
	
	private static String interpretationCommand(String language) {
		String command = null;
		if (language.equalsIgnoreCase("java")) {
			command = "java Main ";
		} else if (language.equalsIgnoreCase("python")) {
			command = "py HelloPython.py ";
		} else if (language.equalsIgnoreCase("cpp")) {
			command = "src/main/resources/temp/" + "exeofCPP ";
		} else if (language.equalsIgnoreCase("c")) {
			command = "src/main/resources/temp/exeofc ";
		}
		return command;
	}
	
	private void saveCodeTemporary(String code, String language) throws IOException {
		log.info("saveCodeTemporary: started");
		FileWriter fl = null;
		if (language.equalsIgnoreCase("java")) {
			String fileNameInLocal = "Main.java";
			fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
		} else if (language.equalsIgnoreCase("python")) {
			String fileNameInLocal = "HelloPython";
			fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal + "." + "py");
		} else if (language.equalsIgnoreCase("cpp")) {
			String fileNameInLocal = "HelloCPP";
			fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal + "." + "cpp");
		} else if (language.equalsIgnoreCase("c")) {
			String fileNameInLocal = "HelloC";
			fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal + "." + "c");
		}
		PrintWriter pr = new PrintWriter(fl);
		pr.write(code);
		pr.flush();
		pr.close();
		log.info("saveCodeTemporary: ended");
	}

	private static String getMessagesFromProcessInputStream(InputStream inputStream) throws IOException {
		log.info("getMessagesFromProcessInputStream :: started");
		String message = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));		
		String line =null;
		while ((line = in.readLine()) != null) {
			message += line  + "\n";
		}
		log.info("getMessagesFromProcessInputStream :: end");
		return message;
	}
	

	@Override
	public CodeResponseDTO compileCode(CodeDetailsDTO codeDetailsDTO) throws IOException {
		log.info("compile code: started");
		CodeResponseDTO codeResponseDTO = new CodeResponseDTO();
		String studentId = codeDetailsDTO.getStudentId();
		ArrayList<Boolean> testCasesSuccess = new ArrayList<Boolean>();
		Process pro = null;
		BufferedReader in = null;
		String line = null;
		String language = codeDetailsDTO.getLanguage();
		String questionId = codeDetailsDTO.getQuestionId();
		int flag = codeDetailsDTO.getFlag();
		String submittedCodeFileName = questionId + "_" + studentId;
		saveCodeTemporary(codeDetailsDTO.getCode(), language);
		try {
			String compilationCommand = compilationCommand(language);
			pro = Runtime.getRuntime().exec(compilationCommand, null, new File("src/main/resources/temp/"));
			String complilationMessage = getMessagesFromProcessInputStream(pro.getErrorStream());
			if (!complilationMessage.isEmpty() && flag == 0) {
				codeResponseDTO.setComplilationMessage(complilationMessage);
				return codeResponseDTO;
			}
			
			List<TestCases> testCases = questionService.getTestCase(questionId);
			String interpretationCommand = interpretationCommand(language);
			for (TestCases testCase : testCases) {
				String input = testCase.getInput();
				pro = Runtime.getRuntime().exec(interpretationCommand + input, null, new File("src/main/resources/temp/"));
				String interpretationMessage = getMessagesFromProcessInputStream(pro.getInputStream());
				interpretationMessage = interpretationMessage.substring(0, interpretationMessage.length() - 1);
				if (interpretationMessage.contains(testCase.getOutput()) || interpretationMessage.equals(testCase.getOutput())) {
					testCasesSuccess.add(true);
				} else {
					testCasesSuccess.add(false);
				}
			}			
			if (flag == 1) {
				List<String> questionIds = new ArrayList<>();
				questionIds.add(questionId);
				FileWriter flSubmitted = new FileWriter(
						"src/main/resources/CodeSubmittedByCandidate/" + submittedCodeFileName);
				PrintWriter prSubmitted = new PrintWriter(flSubmitted);
				prSubmitted.write(codeDetailsDTO.getCode());
				studentService.updateStudentDetails(studentId, codeDetailsDTO.getContestId(), questionIds,
						testCasesSuccess, complilationMessage,submittedCodeFileName);
				prSubmitted.flush();
				prSubmitted.close();
				codeResponseDTO.setTestCasesSuccess(testCasesSuccess);
				codeResponseDTO.setSuccessMessage("Code Submitted Successfully");
				log.info("Code Submitted Successfully");
				return codeResponseDTO;
			}	
			codeResponseDTO.setTestCasesSuccess(testCasesSuccess);
		} catch (IOException e) {
			log.error("Object is null "+e.getMessage());
		}
		log.info("compile code: ended");
		
		return codeResponseDTO;
	}
}
