package com.codecompiler.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.Language;


public interface LanguageRepository extends MongoRepository<Language, String> {

	public List<Language> findAll();
	
	public Language findByLanguage(String language);
}
