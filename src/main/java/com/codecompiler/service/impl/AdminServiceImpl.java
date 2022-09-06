package com.codecompiler.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.entity.Admin;
import com.codecompiler.exception.UserAlreadyExistException;
import com.codecompiler.repository.AdminRepository;
import com.codecompiler.service.AdminService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService{

	@Autowired
	AdminRepository adminRepository;

	public Admin saveAdminDetails(Admin admin) {
		log.info("saveAdminDetails started admin email ::"+admin.getEmail());
		Admin existingAdmin = findByEmail(admin.getEmail().toLowerCase());
		if(existingAdmin == null ) {
			throw new UserAlreadyExistException("Admin with email :: "+admin.getEmail()+" already exist");
		}
		admin.sethId(UUID.randomUUID().toString());
		admin.setEmail(admin.getEmail().toLowerCase());
		admin.setRole("ROLE_ADMIN");
		return adminRepository.save(admin);				
	}
	
	public Admin findByEmail(String email) {
		return adminRepository.findByEmail(email);
	}
	

}
