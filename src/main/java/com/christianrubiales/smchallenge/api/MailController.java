package com.christianrubiales.smchallenge.api;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import jakarta.validation.Valid;
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

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/mail")
    public ResponseEntity<String> sendMail(@Valid @RequestBody Mail mail) {

        mailService.sendMail(mail);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Mail sent");
    }
}
