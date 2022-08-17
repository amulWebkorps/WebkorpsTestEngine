package com.codecompiler.service;

import java.util.List;

import com.codecompiler.entity.Language;

public interface LanguageService {

	public List<Language> findAllLanguage();
	
	public Language findByLanguage(String language);
}
