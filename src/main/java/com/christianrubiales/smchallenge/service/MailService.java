package com.christianrubiales.smchallenge.service;

import com.christianrubiales.smchallenge.model.Mail;

import java.net.http.HttpResponse;

public interface MailService {

    HttpResponse<String> sendMail(Mail mail);
}
