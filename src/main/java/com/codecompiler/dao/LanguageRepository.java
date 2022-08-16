package com.codecompiler.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.codecompiler.entity.Language;

@Repository
public interface LanguageRepository extends MongoRepository<Language, String> {

	public List<Language> findAll();
}
