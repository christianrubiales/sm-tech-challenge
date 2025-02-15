package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import com.christianrubiales.smchallenge.service.MailServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Uses a chain of MailService implementations, sends the mail using the services one at a time,
 * failing over to the next service in the chain.
 * Once a service in the chain succeeds, the remaining services in the chain are no longer tried.
 */
@Primary
@Service
public class ChainedMailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(ChainedMailServiceImpl.class);

    private final MailtrapServiceImpl mailtrapService;

    private final ResendServiceImpl resendService;

    @Autowired
    public ChainedMailServiceImpl(MailtrapServiceImpl mailtrapService, ResendServiceImpl resendService) {
        this.mailtrapService = mailtrapService;
        this.resendService = resendService;
    }

    @Override
    public void sendMail(Mail mail) {
        List<MailService> services = List.of(mailtrapService, resendService);

        MailServiceException exception = null;
        for (MailService service : services) {
            try {
                exception = null;
                logger.info("Trying to send mail using {}", service.getClass().getName());
                service.sendMail(mail);
            } catch (MailServiceException e) {
                logger.info("Mail sending failed using {}", service.getClass().getName(), e);
                exception = e;
            }
        }

        if (exception != null) {
            throw exception;
        }
    }
}
