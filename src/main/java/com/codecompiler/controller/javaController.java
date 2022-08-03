package com.codecompiler.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codecompiler.entity.ResponseToFE;
import com.codecompiler.entity.TestCases;
import com.codecompiler.service.CommonService;
import com.codecompiler.service.StudentService;

@Controller
public class javaController {

	@Autowired
	private CommonService commonService;
	@Autowired
	private BinaryDataController binaryDataController;
	@Autowired
	private StudentService studentService;

	@PostMapping(value = "/javacompiler")
	@ResponseBody
	public ResponseEntity<ResponseToFE> getCompiler(@RequestBody Map<String, Object> data, Model model)
			throws IOException {
		ResponseToFE responsef = new ResponseToFE();
		ArrayList<String> testCasesSuccess = new ArrayList<String>();
		String studentId = (String) data.get("studentId");
		String complilationMessage = "";
		Process pro = null;
		BufferedReader in = null;
		String line = null;
		String language = (String) data.get("language");
		String questionId = (String) data.get("questionId");
		String flag = (String) data.get("flag");
		if (language.equalsIgnoreCase("java")) {
			String fileNameInLocal = "Main.java";
			FileWriter fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal);
			PrintWriter pr = new PrintWriter(fl);
			pr.write((String) data.get("code"));
			pr.flush();
			pr.close();
			try {
				pro = Runtime.getRuntime().exec("javac.exe Main.java", null, new File("src/main/resources/temp/"));
				in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
				while ((line = in.readLine()) != null) {
					complilationMessage += line;
				}
				if (!complilationMessage.isEmpty() && flag.equalsIgnoreCase("run")) {
					responsef.setComplilationMessage(complilationMessage);
					responsef.setTestCasesSuccess(testCasesSuccess);
					return ResponseEntity.ok(responsef);
				}
				binaryDataController.saveFile(studentId, fileNameInLocal, questionId);
				List<TestCases> testCases = commonService.getTestCase(questionId);
				for (TestCases testCase : testCases) {
					String input = testCase.getInput();
					pro = Runtime.getRuntime().exec("java.exe Main " + input, null,
							new File("src/main/resources/temp/"));
					in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
					System.out.println("complilationMessage." + in.toString());
					String output = "";
					while ((line = in.readLine()) != null) {
						output += line + "\n";
					}
					output = output.substring(0, output.length() - 1);
					if (output.contains(testCase.getOutput()) || output.equals(testCase.getOutput())) {
						testCasesSuccess.add("Pass");
					} else {
						testCasesSuccess.add("Fail");
					}
					complilationMessage += line + "\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (language.equalsIgnoreCase("python")) {
			String fileNameInLocal = "HelloPython";
			FileWriter fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal + "." + "py");
			PrintWriter pr = new PrintWriter(fl);
			pr.write((String) data.get("code"));
			pr.flush();
			pr.close();
			try {
				pro = Runtime.getRuntime().exec("src/main/resources/temp/HelloPython.py");
				System.out.println("Compilation done");
				in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
				while ((line = in.readLine()) != null) {
					complilationMessage += line;
				}
				if (!complilationMessage.isEmpty() && flag.equalsIgnoreCase("run")) {
					responsef.setComplilationMessage(complilationMessage);
					responsef.setTestCasesSuccess(testCasesSuccess);
					return ResponseEntity.ok(responsef);
				}
				//binaryDataController.saveFile(studentId, fileNameInLocal, questionId);
				List<TestCases> testCases = commonService.getTestCase(questionId);
				for (TestCases testCase : testCases) {
					String input = testCase.getInput();
					pro = Runtime.getRuntime().exec("py HelloPython.py " + input, null,
							new File("src/main/resources/temp/"));
					in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
					System.out.println("complilationMessage." + in.toString());
					String output = "";
					while ((line = in.readLine()) != null) {
						output += line + "\n";
					}
					output = output.substring(0, output.length() - 1);
					if (output.contains(testCase.getOutput()) || output.equals(testCase.getOutput())) {
						testCasesSuccess.add("Pass");
					} else {
						testCasesSuccess.add("Fail");
					}
					complilationMessage += line + "\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (language.equalsIgnoreCase("cpp")) {
			String fileNameInLocal = "HelloCPP";
			FileWriter fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal + "." + "cpp");
			PrintWriter pr = new PrintWriter(fl);
			pr.write((String) data.get("code"));
			pr.flush();
			pr.close();
			try {
				pro = Runtime.getRuntime().exec("g++.exe HelloCPP.cpp -o exeofCPP", null,
						new File("src/main/resources/temp/"));
				in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
				while ((line = in.readLine()) != null) {
					complilationMessage += line;
				}
				if (!complilationMessage.isEmpty() && flag.equalsIgnoreCase("run")) {
					responsef.setComplilationMessage(complilationMessage);
					responsef.setTestCasesSuccess(testCasesSuccess);
					return ResponseEntity.ok(responsef);
				}
				//binaryDataController.saveFile(studentId, fileNameInLocal, questionId);
				List<TestCases> testCases = commonService.getTestCase(questionId);
				for (TestCases testCase : testCases) {
					String input = testCase.getInput();
					pro = Runtime.getRuntime().exec("src/main/resources/temp/" + "exeofCPP.exe " + input, null,
							new File("src/main/resources/temp/"));
					in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
					System.out.println("complilationMessage." + in.toString());
					String output = "";
					while ((line = in.readLine()) != null) {
						output += line + "\n";
					}
					output = output.substring(0, output.length() - 1);
					if (output.contains(testCase.getOutput()) || output.equals(testCase.getOutput())) {
						testCasesSuccess.add("Pass");
					} else {
						testCasesSuccess.add("Fail");
					}
					complilationMessage += line + "\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (language.equalsIgnoreCase("c")) {
			String fileNameInLocal = "HelloC";
			FileWriter fl = new FileWriter("src/main/resources/temp/" + fileNameInLocal + "." + "c");
			PrintWriter pr = new PrintWriter(fl);
			pr.write((String) data.get("code"));
			pr.flush();
			pr.close();
			try {
				pro = Runtime.getRuntime().exec("gcc.exe HelloC.c -o exeofc", null,
						new File("src/main/resources/temp/"));
				in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
				while ((line = in.readLine()) != null) {
					complilationMessage += line;
				}
				if (!complilationMessage.isEmpty() && flag.equalsIgnoreCase("run")) {
					responsef.setComplilationMessage(complilationMessage);
					responsef.setTestCasesSuccess(testCasesSuccess);
					return ResponseEntity.ok(responsef);
				}
				//binaryDataController.saveFile(studentId, fileNameInLocal, questionId);
				List<TestCases> testCases = commonService.getTestCase(questionId);
				for (TestCases testCase : testCases) {
					String input = testCase.getInput();
					pro = Runtime.getRuntime().exec("src/main/resources/temp/exeofc.exe "+input, null,
							new File("src/main/resources/temp/"));
					in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
					System.out.println("complilationMessage." + in.toString());
					String output = "";
					while ((line = in.readLine()) != null) {
						output += line + "\n";
					}
					output = output.substring(0, output.length() - 1);
					if (output.contains(testCase.getOutput()) || output.equals(testCase.getOutput())) {
						testCasesSuccess.add("Pass");
					} else {
						testCasesSuccess.add("Fail");
					}
					complilationMessage += line + "\n";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (flag.equalsIgnoreCase("submit")) {
			List<String> questionIds = new ArrayList<>();
			questionIds.add(questionId);
			studentService.updateStudentDetails(studentId, (String) data.get("contestId"),
					questionIds, testCasesSuccess, complilationMessage);
		}
		responsef.setTestCasesSuccess(testCasesSuccess);
		return ResponseEntity.ok(responsef);
	}

}
