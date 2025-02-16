package com.christianrubiales.smchallenge.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ResendMail(

        @Email
        @NotEmpty(message = "Sender email address is required.")
        String from,

        @Size(min = 1, message = "At least one recipient is required.")
        List<@Email String> to,

        List<@Email String> cc,

        List<@Email String> bcc,

        @NotNull(message = "Subject is required.")
        String subject,

        @Size(min = 1, message = "Body content is required.")
        String text
) {}
