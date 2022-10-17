package com.codecompiler.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.entity.Admin;
import com.codecompiler.entity.Student;
import com.codecompiler.exception.UserAlreadyExistException;
import com.codecompiler.repository.AdminRepository;
import com.codecompiler.repository.StudentRepository;
import com.codecompiler.service.AdminService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService{

	@Autowired
	AdminRepository adminRepository;
	
	@Autowired
	StudentRepository studentRepository;

	public Admin saveAdminDetails(Admin admin) {
		log.info("saveAdminDetails started admin email ::"+admin.getEmail());
		Admin existingAdmin = findByEmail(admin.getEmail().toLowerCase());
		Student existingStudent = studentRepository.findByEmail(admin.getEmail().toLowerCase());
		if(existingAdmin != null || existingStudent != null) {
			throw new UserAlreadyExistException("Admin with email :: "+admin.getEmail()+" already exist");
		}
		admin.sethId(UUID.randomUUID().toString());
		admin.setEmail(admin.getEmail().toLowerCase());
		admin.setRole("ROLE_ADMIN");
		admin.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));		
		return adminRepository.save(admin);				
	}
	
	public Admin findByEmail(String email) {
		return adminRepository.findByEmail(email);
	}
	

}
