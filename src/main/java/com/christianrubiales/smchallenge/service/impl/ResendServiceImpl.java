package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import com.christianrubiales.smchallenge.service.MailServiceException;
import com.christianrubiales.smchallenge.service.model.ResendMail;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Primary
@Service
public class ResendServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(ResendServiceImpl.class);

    @Value("${resend.apiUrl}")
    private String apiUrl;

    @Value("${resend.apiToken}")
    private String apiToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendMail(Mail mail) {
        try (HttpClient client = HttpClient.newHttpClient()) {

            ResendMail resendMail =
                    new ResendMail(mail.from(), mail.recipients(), mail.cc(), mail.bcc(), mail.subject(), mail.body());
            String payload = objectMapper.writeValueAsString(resendMail);

            logger.debug("Resend API payload: " + payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Resend API Response Code: " + response.statusCode());
            logger.info("Resend API Response Body: " + response.body());

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new MailServiceException(response.body());
            }
        }  catch (Exception e) {
            throw new MailServiceException(e);
        }
    }
}
