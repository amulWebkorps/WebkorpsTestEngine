package com.codecompiler.service.impl;

import org.springframework.stereotype.Service;

import com.codecompiler.entity.ApplicationUserDetails;
import com.codecompiler.entity.User;
import com.codecompiler.repository.HrRepository;
import com.codecompiler.repository.StudentRepository;

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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = studentRepository.findByEmail(username);
		if(user==null) {
		user = hrRepository.findByEmail(username);
		}
        if(user==null){
            throw new UsernameNotFoundException("No user found with Email : "+username);
        }
        return new ApplicationUserDetails(user);
	}
	

	
}
