package com.christianrubiales.smchallenge.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Mail(

        @Email
        @NotEmpty(message = "Sender email address is required.")
        String from,

        @NotEmpty(message = "There should be at least one recipient.")
        List<@Email String> recipients,

        List<@Email String> cc,

        List<@Email String> bcc,

        @NotNull
        String subject,

        String body
) {}
