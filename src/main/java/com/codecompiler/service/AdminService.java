package com.codecompiler.service;

import com.codecompiler.entity.HrDetails;

public interface AdminService {

	public HrDetails findByEmailAndPassword(String email, String password);

	public HrDetails saveHrDetails(HrDetails hrDetails);
	
	public HrDetails findByEmail(String email);
}
