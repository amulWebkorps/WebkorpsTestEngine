package com.codecompiler.repository;

import com.codecompiler.entity.Admin;

public interface AdminRepository extends  UserRepository<Admin>{

	Admin findByEmailAndPassword(String email, String password);
	
	Admin findByEmail(String email);
}
