package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import com.christianrubiales.smchallenge.service.MailServiceException;
import org.springframework.stereotype.Service;

/**
 * Implementation of MailService that will always fail.
 * Used to check failover to another MailService.
 */
@Service
public class MockFailingMailServiceImpl implements MailService {

    @Override
    public void sendMail(Mail mail) {
        throw new MailServiceException("Mock mail service failed");
    }
}
