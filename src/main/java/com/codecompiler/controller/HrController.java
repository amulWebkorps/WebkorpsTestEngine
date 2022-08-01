package com.codecompiler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.codecompiler.entity.HrDetails;
import com.codecompiler.service.HrService;
@Controller
public class HrController {
	@Autowired 
	HrService hrService;
	
	@RequestMapping("/hrRegistration") 
	private String addHrDetails(@RequestBody HrDetails hrDetails,Model model) {
		try {
		System.out.println(hrDetails.gethId());
		HrDetails hr = new HrDetails();
		hr.sethId(hrDetails.gethId());
		hr.setEmail(hrDetails.getEmail());
		hr.sethName(hrDetails.gethName());
		hr.sethNumber(hrDetails.gethNumber());
		HrDetails con1 = hrService.saveHrDetails(hr);	
		System.out.println("con : "+con1);
		}
		catch (Exception e) {
		e.printStackTrace();
		}
		return "";
		
	}
}
