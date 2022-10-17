package com.codecompiler.service;

import com.codecompiler.entity.Admin;

public interface AdminService {

	public Admin saveAdminDetails(Admin admin);
	
	public Admin findByEmail(String email);
}
