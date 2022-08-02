package com.codecompiler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dao.HrRepository;
import com.codecompiler.entity.HrDetails;
import com.codecompiler.entity.Student;

@Service
public class HrService {
	@Autowired
	HrRepository hrRepository;
	public HrDetails saveHrDetails(HrDetails hrDetails) {
		HrDetails con = hrRepository.save(hrDetails);				
		return con;
	}
	public HrDetails findByEmailAndPassword(String email, String password)
	{
		HrDetails s=null;
		try {
			s   =	hrRepository.findByEmailAndPassword(email, password);
			return s;
		}
		catch (Exception e) {
			e.printStackTrace();
			return s;

		}

	}
}
