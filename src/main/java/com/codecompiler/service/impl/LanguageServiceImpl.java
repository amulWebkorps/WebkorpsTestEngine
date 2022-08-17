package com.codecompiler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codecompiler.dao.LanguageRepository;
import com.codecompiler.entity.Language;
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
		return languageRepository.findByLanguage(language);
	}
}
