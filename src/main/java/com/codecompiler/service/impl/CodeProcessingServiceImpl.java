package com.codecompiler.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dto.CodeResponseDTO;
import com.codecompiler.entity.TestCases;
import com.codecompiler.service.CodeProcessingService;
import com.codecompiler.service.QuestionService;
import com.codecompiler.service.StudentService;

@Service
public class CodeProcessingServiceImpl implements CodeProcessingService {

	@Autowired
	private StudentService studentService;

	@Autowired
	private QuestionService questionService;

	private PrintWriter processCode(String code, String language) throws IOException {
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
		return pr;
	}

	@Override
	public CodeResponseDTO compileCode(Map<String, Object> data) throws IOException {
		CodeResponseDTO responsef = new CodeResponseDTO();
		String studentId = (String) data.get("studentId");
		ArrayList<Boolean> testCasesSuccess = new ArrayList<Boolean>();
		String complilationMessage = "";
		Process pro = null;
		BufferedReader in = null;
		String line = null;
		String language = (String) data.get("language");
		String questionId = (String) data.get("questionId");
		int flag = Integer.parseInt((String) data.get("flag"));
		String SubmittedCodeFileName = questionId + "_" + studentId;
		String code = (String) data.get("code");
		String command = null;
		PrintWriter pr = processCode(code, language);
		try {
			if (language.equalsIgnoreCase("java")) {
				command = "javac.exe Main.java";
			} else if (language.equalsIgnoreCase("python")) {
				command = "src/main/resources/temp/HelloPython.py";
			} else if (language.equalsIgnoreCase("cpp")) {
				command = "g++.exe HelloCPP.cpp -o exeofCPP";
			} else if (language.equalsIgnoreCase("c")) {
				command = "gcc.exe HelloC.c -o exeofc";
			}
			pro = Runtime.getRuntime().exec(command, null, new File("src/main/resources/temp/"));

			in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
			while ((line = in.readLine()) != null) {
				complilationMessage += line;
			}
			if (!complilationMessage.isEmpty() && flag == 0) {
				responsef.setComplilationMessage(complilationMessage);
				return responsef;
			}
			if (language.equalsIgnoreCase("java")) {
				command = "java.exe Main ";
			} else if (language.equalsIgnoreCase("python")) {
				command = "py HelloPython.py ";
			} else if (language.equalsIgnoreCase("cpp")) {
				command = "src/main/resources/temp/" + "exeofCPP.exe ";
			} else if (language.equalsIgnoreCase("c")) {
				command = "src/main/resources/temp/exeofc.exe ";
			}
			List<TestCases> testCases = questionService.getTestCase(questionId);

			for (TestCases testCase : testCases) {
				String input = testCase.getInput();
				pro = Runtime.getRuntime().exec(command + input, null, new File("src/main/resources/temp/"));
				in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
				String output = "";
				while ((line = in.readLine()) != null) {
					output += line + "\n";
				}
				output = output.substring(0, output.length() - 1);
				if (output.contains(testCase.getOutput()) || output.equals(testCase.getOutput())) {
					testCasesSuccess.add(true);
				} else {
					testCasesSuccess.add(false);
				}
				complilationMessage += line + "\n";
			}			
			if (flag == 1) {
				List<String> questionIds = new ArrayList<>();
				questionIds.add(questionId);
				FileWriter flSubmitted = new FileWriter(
						"src/main/resources/CodeSubmittedByCandidate/" + SubmittedCodeFileName);
				PrintWriter prSubmitted = new PrintWriter(flSubmitted);
				prSubmitted.write((String) data.get("code"));
				studentService.updateStudentDetails(studentId, (String) data.get("contestId"), questionIds,
						testCasesSuccess, complilationMessage);
				prSubmitted.flush();
				prSubmitted.close();
				responsef.setSuccessMessage("Code Submitted Successfully");
				return responsef;
			}	
			responsef.setTestCasesSuccess(testCasesSuccess);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responsef;
	}
}
