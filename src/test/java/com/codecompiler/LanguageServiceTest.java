package com.codecompiler;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.codecompiler.entity.Language;
import com.codecompiler.service.LanguageService;

@SpringBootTest
public class LanguageServiceTest {

	@Autowired
	LanguageService languageService;
	
	String languageName = "Java";
	
	@Test
	public void findAllLanguageSuccessTest() {
		List<Language> languages = languageService.findAllLanguage();
		Assertions.assertTrue(languages.size() > 0);		
	}
		
	@Test
	public void findByLanguageSuccessTest() {
		Language language = languageService.findByLanguage(languageName);
		Assertions.assertNotNull(language);
		Assertions.assertEquals(languageName, language.getLanguage());
		Assertions.assertNotNull(language.getCodeBase());
	}
	
	@Test
	public void findByLanguageFailureTest() {
		Assertions.assertThrows(NullPointerException.class, ()-> languageService.findByLanguage(null));
		Assertions.assertThrows(IllegalArgumentException.class, ()-> languageService.findByLanguage(""));
		Assertions.assertThrows(IllegalArgumentException.class, ()-> languageService.findByLanguage(" "));
		//Assertions.assertThrows(RecordMisMatchedException.class, ()-> languageService.findByLanguage(languageName)); 
	}
	
}
