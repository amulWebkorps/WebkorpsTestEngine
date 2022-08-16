package com.codecompiler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dao.HrRepository;
import com.codecompiler.entity.HrDetails;
import com.codecompiler.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	HrRepository hrRepository;
	
	public HrDetails findByEmailAndPassword(String email, String password) {
		return hrRepository.findByEmailAndPassword(email, password);
	}
	
	public HrDetails saveHrDetails(HrDetails hrDetails) {
		return hrRepository.save(hrDetails);				
	}
	
	public HrDetails findByEmail(String email) {
		return hrRepository.findByEmail(email);
	}
}
