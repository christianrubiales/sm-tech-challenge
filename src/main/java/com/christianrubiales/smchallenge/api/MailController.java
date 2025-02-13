package com.christianrubiales.smchallenge.api;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class MailController {

    private static final Logger logger = LoggerFactory.getLogger(MailController.class);

    @Autowired
    private MailService mailService;

    @PostMapping("/mail")
    public ResponseEntity<String> sendMail(@Valid @RequestBody Mail mail) {

        mailService.sendMail(mail);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Mail sent");
    }
}
