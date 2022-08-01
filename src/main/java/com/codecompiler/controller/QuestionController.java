package com.codecompiler.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.codecompiler.helper.Helper;
import com.codecompiler.service.QuestionService;

@Controller
public class QuestionController {
	@Autowired
	QuestionService qs;
	   @RequestMapping("/question/upload")
	    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
	        if (Helper.checkExcelFormat(file)) {
	            //true
try {
	            this.qs.save(file);
}
catch (Exception e) {
e.printStackTrace();
}
	            return ResponseEntity.ok(Map.of("message", "File is uploaded and data is saved to db"));


	        }
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload excel file ");
	    }

}
