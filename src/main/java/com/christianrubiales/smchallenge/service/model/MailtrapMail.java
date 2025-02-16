package com.christianrubiales.smchallenge.service.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MailtrapMail(

        @NotNull(message = "Sender email address is required.")
        MailtrapAddress from,

        @Size(min = 1, message = "There should be at least one recipient")
        List<MailtrapAddress> to,

        List<MailtrapAddress> cc,

        List<MailtrapAddress> bcc,

        @NotEmpty(message = "Subject is required and should not be empty.")
        String subject,

        @NotEmpty(message = "Body is required and should not be empty")
        String text
) {}

