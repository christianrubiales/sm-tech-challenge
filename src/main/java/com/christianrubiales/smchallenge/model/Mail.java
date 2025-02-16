package com.christianrubiales.smchallenge.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record Mail(

        @Email
        @NotEmpty(message = "Sender email address is required.")
        String from,

        @NotEmpty(message = "At least one recipient is required.")
        List<@Email @NotEmpty String> recipients,

        List<@Email @NotEmpty String> cc,

        List<@Email @NotEmpty String> bcc,

        String subject,

        @NotEmpty(message = "Body content is required.")
        String body
) {}
