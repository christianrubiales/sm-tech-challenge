package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import com.christianrubiales.smchallenge.service.MailServiceException;
import com.christianrubiales.smchallenge.service.model.ResendMail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

/**
 * Implementation using the Resend API.
 */
@Service
public class ResendServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(ResendServiceImpl.class);

    @Value("${resend.apiUrl}")
    private String apiUrl;

    @Value("${resend.apiToken}")
    private String apiToken;

    private final ObjectMapper objectMapper;

    private final Validator validator;

    public ResendServiceImpl(ObjectMapper objectMapper, Validator validator) {
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @Override
    public HttpResponse<String> sendMail(Mail mail) {
        try (HttpClient client = this.getHttpClient()) {

            // build JSON payload
            ResendMail resendMail = new ResendMail(mail.from(), mail.recipients(), mail.cc(), mail.bcc(), mail.subject(), mail.body());
            String payload = this.buildJsonPayload(resendMail);
            logger.debug("Resend API payload: {}", payload);

            // POST to the Resend API
            HttpRequest request = this.buildHttpRequest(payload);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Resend API Response Code: {}", response.statusCode());
            logger.info("Resend API Response Body: {}", response.body());

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new MailServiceException(response.body());
            }

            return response;
        } catch (InterruptedException ie) {
            // Suggested by CodeScan
            Thread.currentThread().interrupt();
            throw new MailServiceException(ie);
        } catch (Exception e) {
            throw new MailServiceException(e);
        }
    }

    protected HttpRequest buildHttpRequest(String payload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
    }

    protected HttpClient getHttpClient() {
        return HttpClient.newHttpClient();
    }

    protected String buildJsonPayload(ResendMail resendMail) throws JsonProcessingException {
        Set<ConstraintViolation<ResendMail>> violations  = validator.validate(resendMail);
        if (!violations.isEmpty()) {
            logger.info("ResendMail validator violations: {}", violations);
            throw new MailServiceException("ResendMail validator violations: " + violations);
        }
        return objectMapper.writeValueAsString(resendMail);
    }
}
