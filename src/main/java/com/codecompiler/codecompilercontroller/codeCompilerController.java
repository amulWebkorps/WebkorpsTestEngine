package com.codecompiler.codecompilercontroller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codecompiler.entity.ResponseToFE;
import com.codecompiler.service.CodeProcessingService;

@Controller
public class codeCompilerController {

	@Autowired
	private CodeProcessingService codeProcessingService;

	@PostMapping(value = "/javacompiler")
	@ResponseBody
	public ResponseEntity<ResponseToFE> getCompiler(@RequestBody Map<String, Object> data)
			throws Exception {
		ResponseToFE responsef = codeProcessingService.compileCode(data);
		return ResponseEntity.ok(responsef);
	}
}
