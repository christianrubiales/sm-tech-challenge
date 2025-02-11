package com.christianrubiales.smchallenge.api;

import com.christianrubiales.smchallenge.model.Mail;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping("/mail")
    public ResponseEntity<String> sendMail(@Valid @RequestBody Mail mail) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Mail sent");
    }
}
