package com.christianrubiales.smchallenge.api;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
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

    private final HttpServletRequest request;

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService, HttpServletRequest request) {
        this.mailService = mailService;
        this.request = request;
    }

    @PostMapping("/mail")
    public ResponseEntity<String> sendMail(@Valid @RequestBody Mail mail) {
        logger.info("sendMail Request from {}: {}", this.getUserIP(this.request), mail);

        mailService.sendMail(mail);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Mail sent");
    }

    protected String getUserIP(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
