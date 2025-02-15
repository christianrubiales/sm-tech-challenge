package com.christianrubiales.smchallenge.model;

import jakarta.validation.constraints.Email;

import java.util.List;

public record Mail(

        @Email
        String from,

        List<@Email String> recipients,

        List<@Email String> cc,

        List<@Email String> bcc,

        String subject,

        String body
) {}
