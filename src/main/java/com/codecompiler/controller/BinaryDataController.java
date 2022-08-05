package com.codecompiler.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
//import com.mongodb.gridfs.GridFSDBFile;

@RestController
public class BinaryDataController {

//	@Autowired
//	private GridFsOperations gridFsOperations;
	
	String fileId = "";

//	public String saveFile(String studentId, String fileName, String questionId) throws Exception { // define metadata
//		DBObject metaData = new BasicDBObject();																			// DBObject
//		metaData = new BasicDBObject();
//		metaData.put("organization", "Java Techie");
//		metaData.put("type", "data");
//		String fileNameInDB = questionId+"_"+studentId;
//		fileId = gridFsOperations.store(new FileInputStream("src/main/resources/temp/" + fileName),
//				fileNameInDB + ".txt", "text/plain", metaData).get().toString();
//		System.out.println("File id stored : " + fileId);
//		return "File stored successfully...";
//	}

	/*
	 * public String saveFile(String studentId, String fileNameInLocal, String
	 * questionId) throws FileNotFoundException { DBObject metaData = new
	 * BasicDBObject(); metaData.put("studentId", studentId);
	 * metaData.put("questionId", questionId); String fileNameInDB =
	 * fileNameInLocal; metaData.put("type", "data"); fileId =
	 * gridFsOperations.store(new
	 * FileInputStream("src/main/resources/temp/"+fileNameInLocal),
	 * fileNameInDB+".txt","text/plain", metaData).get().toString();
	 * System.out.println("File id stored : " + fileId); return
	 * "File stored successfully..."; }
	 */


//	@GetMapping("/retrive/text")
//	public String retriveImageFile() throws IOException {
//		GridFSDBFile dbFile = (GridFSDBFile) gridFsOperations.find(new Query(Criteria.where("filename").is("output.txt")));
//		dbFile.writeTo("C:\\Users\\Public\\Montrix\\CodeCompiler\\src\\main\\resources\\participantsData\\myData.txt");
//		System.out.println("File name : " + dbFile.getFilename());
//		return "Image File retrived with name : " + dbFile.getFilename();
//	}
	
	
//	@GetMapping("/retrive/text")
//	public String retriveImageFile(String Fname) throws IOException {
//		GridFSFile dbFile = gridFsOperations.findOne(new Query(Criteria.where("filename").is("output.txt")));
//		
//		// Document document = 
//		System.out.println(dbFile.toString());
//		// document.
//		//.writeTo("C:/Users/bahota/Desktop/Local-Disk/reactive-logo.png");
//		System.out.println("File name : " + dbFile.getFilename());
//		return "Image File retrived with name : " + dbFile.getFilename();
//	}



	@DeleteMapping("/delete/text")
	public String deleteTextFile(String fileName) throws IOException {
		File f= new File("C:\\Users\\Public\\Montrix\\CodeCompiler\\src\\main\\resources\\temp\\fileName");
		f.delete();
		return "Text File Deleted";
	}
	
}
