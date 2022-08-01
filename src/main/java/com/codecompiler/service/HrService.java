package com.codecompiler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dao.HrRepository;
import com.codecompiler.entity.HrDetails;

@Service
public class HrService {
	@Autowired
	HrRepository hrRepository;
	  public HrDetails saveHrDetails(HrDetails hrDetails) {
		  HrDetails con = hrRepository.save(hrDetails);				
			return con;
		}
}
