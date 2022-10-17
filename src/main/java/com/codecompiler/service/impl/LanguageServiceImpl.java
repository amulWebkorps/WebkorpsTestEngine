package com.codecompiler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.entity.Language;
import com.codecompiler.exception.RecordMisMatchedException;
import com.codecompiler.repository.LanguageRepository;
import com.codecompiler.service.LanguageService;

@Service
public class LanguageServiceImpl implements LanguageService{

	@Autowired
	private LanguageRepository languageRepository;
	
	@Override
	public List<Language> findAllLanguage() {
		return languageRepository.findAll();
	}

	public Language findByLanguage(String language) {
		if(language == null) 
			throw new NullPointerException();
		else if (language.isBlank()) 
			throw new IllegalArgumentException();
		Language languageData = languageRepository.findByLanguage(language);
		if(!languageData.getLanguage().equals(language))
			throw new RecordMisMatchedException("received data is not related to " + language);
		return languageData;
	}
}
