package com.codecompiler.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.codecompiler.entity.User;

@NoRepositoryBean
public interface UserRepository<T extends User>  extends MongoRepository<T, Integer> {
	User findByEmail(String email);
}
