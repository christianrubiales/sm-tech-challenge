package com.christianrubiales.smchallenge.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MailtrapMail(

        @Email
        @NotEmpty(message = "Sender address is required.")
        MailtrapAddress from,

        List<@Email MailtrapAddress> to,

        List<@Email MailtrapAddress> cc,

        List<@Email MailtrapAddress> bcc,

        @NotEmpty(message = "Subject is required.")
        String subject,

        @NotNull
        String text
) {}

