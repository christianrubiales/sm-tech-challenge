package com.christianrubiales.smchallenge.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record MailtrapAddress (

        @Email
        @NotEmpty
        String email
) {}
