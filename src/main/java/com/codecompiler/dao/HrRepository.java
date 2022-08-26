package com.codecompiler.dao;



import com.codecompiler.entity.HrDetails;

public interface HrRepository extends  UserRepository<HrDetails>{

	HrDetails findByEmailAndPassword(String email, String password);
	
	HrDetails findByEmail(String email);
}
