package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import com.christianrubiales.smchallenge.service.MailServiceException;
import org.springframework.stereotype.Service;

@Service
public class MockFailingMailServiceImpl implements MailService {

    @Override
    public void sendMail(Mail mail) {
        throw new MailServiceException("Mock mail service failed");
    }
}
