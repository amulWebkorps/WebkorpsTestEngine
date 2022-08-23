package com.codecompiler.helper;

import com.codecompiler.dao.QuestionRepository;
import com.codecompiler.entity.MyCell;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.SampleTestCase;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.TestCases;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Helper {

	@Autowired
	private QuestionRepository questionRepository;
	
	// check that file inputStreamFileQuestion of excel type or not
	public static boolean checkExcelFormat(MultipartFile file) {

		String contentType = file.getContentType();

		if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			return true;
		} else {
			return false;
		}

	}

	// convert excel to list of products

	public static List<Student> convertExcelToListOfStudent(InputStream inputStreamFileStudent) {
		List<Student> list = new ArrayList<>();

		try {

			XSSFWorkbook workbook = new XSSFWorkbook(inputStreamFileStudent);

			XSSFSheet sheet = workbook.getSheet("Sheet1");

			int rowNumber = 0;
			Iterator<Row> iterator = sheet.iterator();

			while (iterator.hasNext()) {
				Row row = iterator.next();

				if (rowNumber == 0) {
					rowNumber++;
					continue;
				}

				Iterator<Cell> cells = row.iterator();

				int cid = 0;

				Student question = new Student();

				while (cells.hasNext()) {
					Cell cell = cells.next();

					switch (cid) {
					case 0:
						if (cell.getCellType() == CellType.STRING)
							question.setName(cell.getStringCellValue());
						break;
					case 1:
						if (cell.getCellType() == CellType.STRING)
							question.setEmail(cell.getStringCellValue());
						break;
					case 2:
						if (cell.getCellType() == CellType.NUMERIC)
							question.setMobileNumber((int) cell.getNumericCellValue());
						break;

					case 3:
						if (cell.getCellType() == CellType.STRING)
							question.setContestLevel(cell.getStringCellValue());
						break;
					default:
						break;
					}
					cid++;

				}
				question.setId(UUID.randomUUID().toString());
				String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk" + "lmnopqrstuvwxyz!@#$%&";
				Random rnd = new Random();
				StringBuilder sb = new StringBuilder(6);
				for (int i = 0; i < 6; i++)
					sb.append(chars.charAt(rnd.nextInt(chars.length())));
				question.setPassword(sb.toString());
				list.add(question);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public static List<Question> convertExcelToListOfQuestions(Map<Integer, List<MyCell>> data) {
		List<Question> questionList = new ArrayList<>();
		try {
			List<MyCell> headerRow = data.get(0);
			for (int i = 1; i < data.size(); i++) {
				Question question = new Question();
				SampleTestCase sampleTestCases = new SampleTestCase();
				List<SampleTestCase> ListSampleTestCase = new ArrayList();
				List<TestCases> listTestCases = new ArrayList();
				TestCases testCases = new TestCases();
				String tempQid = UUID.randomUUID().toString();
				List<MyCell> row = data.get(i);
				question.setContestLevel(row.get(0).getContent());
				question.setQuestion(row.get(1).getContent());
				sampleTestCases.setConstraints(row.get(2).getContent());
				sampleTestCases.setInput(row.get(3).getContent());
				sampleTestCases.setOutput(row.get(4).getContent());
				for (int k = 5; k < headerRow.size(); k++) {
					int flag = 0;
					if (k % 2 != 0) {						
						testCases.setInput(row.get(k).getContent());
						flag = 1;
					} else {
						testCases.setOutput(row.get(k).getContent());
						flag = 2;
					}
					if (flag == 2) {
						listTestCases.add(testCases);
						testCases = new TestCases();
					}
				}				
				ListSampleTestCase.add(sampleTestCases);
				question.setQuestionId(tempQid);
				question.setQuestionStatus("true");
				question.setTestcases(listTestCases);
				question.setSampleTestCase(ListSampleTestCase);
				questionList.add(question);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return questionList;

	}

}
