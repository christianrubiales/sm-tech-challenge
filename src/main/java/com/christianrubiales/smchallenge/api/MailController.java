package com.christianrubiales.smchallenge.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    @PostMapping("/mail")
    public ResponseEntity<String> sendMail() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Mail sent");
    }
}
