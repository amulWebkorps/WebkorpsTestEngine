package com.codecompiler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.codecompiler.entity.Admin;
import com.codecompiler.response.ResponseHandler;
import com.codecompiler.service.AdminService;
import com.codecompiler.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Controller
@CrossOrigin(origins = "*")
@Slf4j
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("public/admin/signIn")
	public ResponseEntity<Object> doLogin(@RequestBody Admin admin) {
		log.info("doLogin started user email :: {}", admin.getEmail());

		Authentication authObj = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(admin.getEmail().toLowerCase(), admin.getPassword()));

		String token = jwtUtil.generateToken(authObj.getName());

		log.info("doLogin ended token generated successfully");

		return ResponseHandler.generateResponse("success", HttpStatus.OK, token);
	}


	@PostMapping("public/adminRegistration")
	private ResponseEntity<Object> adminRegistration(@RequestBody Admin admin) {
		log.info("adminRegistration started admin email :: {}", admin.getEmail());
		adminService.saveAdminDetails(admin);
		log.info("Admin details saved successfully");
		return ResponseHandler.generateResponse("success", HttpStatus.OK, "Admin registered successfully");
	}

}
