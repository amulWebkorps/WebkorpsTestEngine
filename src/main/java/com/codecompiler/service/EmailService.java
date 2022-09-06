package com.codecompiler.service;

import java.util.List;
import java.util.Map;

public interface EmailService {
    void sendMail(String contestId, String name, String to, String subject, String password);

	void sendMailToStudents(Map<String, List<String>> sendEmailDetails);
}
