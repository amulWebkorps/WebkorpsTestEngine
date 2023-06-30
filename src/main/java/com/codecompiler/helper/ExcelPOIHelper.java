package com.codecompiler.helper;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.codecompiler.dto.MyCellDTO;

public class ExcelPOIHelper {

	public Map<Integer, List<MyCellDTO>> readExcel(InputStream inputStream, String fileLocation) throws IOException {

        Map<Integer, List<MyCellDTO>> data = new HashMap<>();

        if (fileLocation.endsWith(".xls")) {
            data = readHSSFWorkbook(inputStream);
        } else if (fileLocation.endsWith(".xlsx")) {
            data = readXSSFWorkbook(inputStream);
        }

        int maxNrCols = data.values().stream()
          .mapToInt(List::size)
          .max()
          .orElse(0);

        data.values().stream()
          .filter(ls -> ls.size() < maxNrCols)
          .forEach(ls -> {
              IntStream.range(ls.size(), maxNrCols)
                .forEach(i -> ls.add(new MyCellDTO("")));
          });

        return data;
    }

	private String readCellContent(CellValue cellValue) {
		String content;
        switch (cellValue.getCellType()) {
        case STRING:
            content = cellValue.getStringValue();
            break;
        case NUMERIC:  
           content = cellValue.getNumberValue() + "";
            break;
        case BOOLEAN:
            content = cellValue.getBooleanValue() + "";
            break;
        default:
            content = "";
        }
        return content;
    }

    private Map<Integer, List<MyCellDTO>> readHSSFWorkbook(InputStream inputStream) throws IOException {
        Map<Integer, List<MyCellDTO>> data = new HashMap<>();
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            
            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                data.put(i, new ArrayList<>());
                if (row != null) {
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        HSSFCell cell = row.getCell(j);
                        CellValue cellValue= evaluator.evaluate(cell);
                        if (cellValue != null) {
                            MyCellDTO myCell = new MyCellDTO();
                            myCell.setContent(readCellContent(cellValue));
                            data.get(i).add(myCell);
                        }
                    }
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        
        return data;
    }

    private Map<Integer, List<MyCellDTO>> readXSSFWorkbook(InputStream inputStream) throws IOException {
        XSSFWorkbook workbook = null;
        Map<Integer, List<MyCellDTO>> data = new HashMap<>();
        try {
            workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            
            for (int i = sheet.getFirstRowNum(); i <sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                data.put(i, new ArrayList<>());
                if (row != null) {
                    for (int j = 0; j <=row.getLastCellNum(); j++) {
                        XSSFCell cell = row.getCell(j);
                        CellValue cellValue= evaluator.evaluate(cell);
                        if (cellValue != null) {
                        	MyCellDTO myCell = new MyCellDTO();
                        	if(cellValue.getCellType() == CellType.NUMERIC) {
                        		myCell.setContent(formatNumericCellValue(cellValue.getNumberValue()));
                                
                        	}else {
                        		myCell.setContent(readCellContent(cellValue));
                        	}
                        	data.get(i).add(myCell);   
                        }
                    }
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return data;
    }
    private String formatNumericCellValue(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.########");
        return decimalFormat.format(value);
    }
}
