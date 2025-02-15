package com.christianrubiales.smchallenge.service.model;

import jakarta.validation.constraints.Email;

public record MailtrapAddress (

        @Email
        String email
) {}
