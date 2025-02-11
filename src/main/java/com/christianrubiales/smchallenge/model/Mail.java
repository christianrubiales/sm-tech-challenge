package com.christianrubiales.smchallenge.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record Mail(

        @NotEmpty(message = "There should be at least one recipient.")
        List<@Email String> recipients,

        List<@Email String> cc,

        List<@Email String> bcc,

        String body
) {}
