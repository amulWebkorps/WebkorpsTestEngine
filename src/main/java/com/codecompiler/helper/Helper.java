package  com.codecompiler.helper;
import com.codecompiler.entity.Question;
import com.codecompiler.entity.SampleTestCase;
import com.codecompiler.entity.Student;
import com.codecompiler.entity.TestCases;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Helper {


	//check that file is of excel type or not
	public static boolean checkExcelFormat(MultipartFile file) {

		String contentType = file.getContentType();

		if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			return true;
		} else {
			return false;
		}

	}


	//convert excel to list of products

	public static List<Student> convertExcelToListOfStudent(InputStream is) {
		List<Student> list = new ArrayList<>();

		try {


			XSSFWorkbook workbook = new XSSFWorkbook(is);

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

				Student p = new Student();

				while (cells.hasNext()) {
					Cell cell = cells.next();

					switch (cid) {
					case 0:
						p.setId((int) cell.getNumericCellValue());
						break;
					case 1:
						if(cell.getCellType() == CellType.STRING)
						p.setName(cell.getStringCellValue());
						break;
					case 2:
						if(cell.getCellType() == CellType.STRING)
						p.setEmail(cell.getStringCellValue());
						break;
					case 3:
						if(cell.getCellType() == CellType.NUMERIC)
						p.setMobileNumber((int)cell.getNumericCellValue());
						break;

					case 4:
						if(cell.getCellType() == CellType.STRING)
						p.setContestLevel(cell.getStringCellValue());
						break;
					default:
						break;
					}
					cid++;

				}
				String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
						+"lmnopqrstuvwxyz!@#$%&";
				Random rnd = new Random();
				StringBuilder sb = new StringBuilder(6);
				for (int i = 0; i < 6; i++)
					sb.append(chars.charAt(rnd.nextInt(chars.length())));
				p.setPassword(sb.toString());
				list.add(p);

			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}
	public static List<Question> convertExcelToListOfOestions(InputStream is) {
		List<Question> list = new ArrayList<>();

		try {


			XSSFWorkbook workbook = new XSSFWorkbook(is);

			XSSFSheet sheet = workbook.getSheetAt(0);

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

				Question p = new Question();
				SampleTestCase  s=new SampleTestCase();
				
				List<SampleTestCase> sampleTestCase = new ArrayList();
				List<TestCases> testCases = new ArrayList();
				TestCases t=new TestCases();
				String tempQid = UUID.randomUUID().toString();
				
		     
				while (cells.hasNext()) {
					Cell cell = cells.next();
					
					switch (cid) {
					case 0:
						p.setQuestionId(tempQid);
						break;
					case 1:
						if(cell.getCellType() == CellType.STRING)
						p.setQuestion(cell.getStringCellValue());
						break;
					case 2:
						if(cell.getCellType() == CellType.STRING)
						p.setContestLevel(cell.getStringCellValue());
						break;
					case 3:
						if(cell.getCellType() == CellType.STRING)
						s.setInput(cell.getStringCellValue());
						break;

					case 4:
						if(cell.getCellType() == CellType.STRING)
						s.setOutput(cell.getStringCellValue());  
						sampleTestCase.add(s);
						break;
					case 5:
						if(cell.getCellType() == CellType.STRING)
						t.setInput(cell.getStringCellValue());
						break;
					case 6:
						if(cell.getCellType() == CellType.STRING)
						t.setOutput(cell.getStringCellValue());
						testCases.add(t);
						t=new TestCases();
						break;
					case 7:
						if(cell.getCellType() == CellType.STRING)
						t.setInput(cell.getStringCellValue());
						break;

					case 8:
						if(cell.getCellType() == CellType.STRING)
						t.setOutput(cell.getStringCellValue());
						testCases.add(t);
						t=new TestCases();
						break;	
					case 9:
						if(cell.getCellType() == CellType.STRING)
						t.setInput(cell.getStringCellValue());
						break;

					case 10:
						if(cell.getCellType() == CellType.STRING)
						t.setOutput(cell.getStringCellValue());
						testCases.add(t);
						break;	
					default:
						break;
					}
					cid++;
					p.setTestcases(testCases);
				
					p.setSampleTestCase(sampleTestCase);
					
				}
				list.add(p);


			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}


}
