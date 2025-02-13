package com.christianrubiales.smchallenge.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ResendMail(

        @Email
        @NotEmpty(message = "Sender address is required.")
        String from,

        List<@Email String> to,

        List<@Email String> cc,

        List<@Email String> bcc,

        @NotEmpty(message = "Subject is required.")
        String subject,

        @NotNull
        String text
) {}
