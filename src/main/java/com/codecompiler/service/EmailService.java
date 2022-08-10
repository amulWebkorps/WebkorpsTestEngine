package com.codecompiler.service;

public interface EmailService {
    void sendMail(String contestId, String name, String to, String subject, String password);
}
