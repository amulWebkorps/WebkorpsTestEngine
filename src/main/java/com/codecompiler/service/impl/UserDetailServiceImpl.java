package com.codecompiler.service.impl;

import org.springframework.stereotype.Service;

import com.codecompiler.dao.HrRepository;
import com.codecompiler.dao.StudentRepository;
import com.codecompiler.entity.ApplicationUserDetails;
import com.codecompiler.entity.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Service
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private HrRepository hrRepository;

	@Autowired
	private StudentRepository studentRepository;

	public static final Logger logger = LogManager.getLogger(UserDetailServiceImpl.class);

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("loadUserByUsername: started");
		User user = studentRepository.findByEmail(username);
		if (user == null) {
			user = hrRepository.findByEmail(username);
		}
		if (user == null) {
			throw new UsernameNotFoundException("No user found with Email : " + username);
		}
		return new ApplicationUserDetails(user);
	}

}
