package com.codecompiler.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codecompiler.entity.HrDetails;
import com.codecompiler.service.HrService;
@Controller
public class HrController {
	@Autowired 
	HrService hrService;
	HrDetails con1= null;
	@RequestMapping("/hrregistration") 
	private ResponseEntity<?> addHrDetails(@RequestBody HrDetails hrDetails) {
		try {
		
		
		HrDetails hr = new HrDetails();
		hr.sethId(UUID.randomUUID().toString());
		hr.setEmail(hrDetails.getEmail());
		hr.sethName(hrDetails.gethName());
		hr.sethNumber(hrDetails.gethNumber());
		hr.setPassword(hrDetails.getPassword());
		 con1 = hrService.saveHrDetails(hr);
		}
		catch (Exception e) {
		e.printStackTrace();
		}
		return ResponseEntity.ok(con1);
		
	}
	@RequestMapping("/hrregistrationpage") 
	private String addHrDetails(Model model) {
		
		model.addAttribute("hrdetails", con1);
		
		return "HrLogin.html";
		
	}
	@RequestMapping("/loginbyhr")
	public ResponseEntity<?> doLogin(@RequestHeader String email, @RequestHeader String password) {

	 con1= hrService.findByEmailAndPassword(email, password) ;
		if(con1==null)
		{
			System.out.println("email and password does not match");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and password does not match");
		}
		else {
			return ResponseEntity.ok(con1);
		}

	}
	@RequestMapping("/hrloginpage")
	public String doLogin(Model model)
	{
		model.addAttribute("HrDetails", con1);
		return "adminHome";
		
	}
	
	
}
