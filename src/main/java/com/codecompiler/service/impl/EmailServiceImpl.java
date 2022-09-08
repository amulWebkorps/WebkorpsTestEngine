package com.codecompiler.service.impl;

import com.codecompiler.service.EmailService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Value("${application.base-url}")
    private String baseUrl;
	
	public static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);
	
	private static final String CANDIDATE_EMAIL_TEMPLATE = "Hello \"%s\",%nGreeting of the day!!!%n%n"
			+ "Please find your test link and credentials "
			+ "Click here \"%s\" to test portal.%n"
			+ "Username - \"%s\"%n"
			+ "Password - \"%s\"%n"
			+ "All the best for your test%n%n"
			+ "Regards,%nHr Team%n%n%n"
			+ "PS - This is an auto generated MAIL, please do not reply";
	
    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendMail(String contestId, String name, String to, String subject, String password) {
    	logger.info("Mail Sending Start");
    	String content = String.format(CANDIDATE_EMAIL_TEMPLATE, name, getLink(contestId), to, password);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);
        logger.info("Mail Sending end");
    }
    
    private String getLink(String contestId) {
        return baseUrl + contestId;
    }
}
