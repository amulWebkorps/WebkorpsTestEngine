package com.codecompiler.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@RestController
public class BinaryDataController {

	@Autowired
	private GridFsOperations gridFsOperations;

	String fileId = "";

	public String saveFile(String studentId, String fileName, String questionId) throws FileNotFoundException { // define metadata
		DBObject metaData = new BasicDBObject();																			// DBObject
		metaData = new BasicDBObject();
		metaData.put("organization", "Java Techie");
		metaData.put("type", "data");
		String fileNameInDB = questionId+"_"+studentId;
		fileId = gridFsOperations.store(new FileInputStream("src/main/resources/temp/" + fileName),
				fileNameInDB + ".txt", "text/plain", metaData).get().toString();
		System.out.println("File id stored : " + fileId);
		return "File stored successfully...";
	}

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
	/*
	 * @GetMapping("/retrive/text") public String retriveTextFileFromDB(String
	 * participantId) throws IOException { GridFSDBFile dbFile =
	 * gridFsOperations.findOne(new
	 * Query(Criteria.where("filename").is(participantId))); dbFile.writeTo(
	 * "C:\\Users\\Public\\Montrix\\CodeCompiler\\src\\main\\resources\\participantsData\\myData.txt"
	 * ); System.out.println("File name : " + dbFile.getFilename()); return
	 * "Text File retrived with name : " + dbFile.getFilename(); }
	 */


	@DeleteMapping("/delete/text")
	public String deleteTextFile(String fileName) throws IOException {
		File f= new File("C:\\Users\\Public\\Montrix\\CodeCompiler\\src\\main\\resources\\temp\\fileName");
		f.delete();
		return "Text File Deleted";
	}
	
}
