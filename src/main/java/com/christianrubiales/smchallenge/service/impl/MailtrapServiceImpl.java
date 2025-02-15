package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailService;
import com.christianrubiales.smchallenge.service.MailServiceException;
import com.christianrubiales.smchallenge.service.model.MailtrapAddress;
import com.christianrubiales.smchallenge.service.model.MailtrapMail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Implementation using the Mailtrap API.
 */
@Service
public class MailtrapServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailtrapServiceImpl.class);

    @Value("${mailtrap.apiUrl}")
    private String apiUrl;

    @Value("${mailtrap.apiToken}")
    private String apiToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void sendMail(Mail mail) {
        try (HttpClient client = HttpClient.newHttpClient()) {

            String payload = this.buildJsonPayload(mail);
            logger.debug("Mailtrap API payload: {}", payload);

            HttpRequest request = this.buildHttpRequest(payload);

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Mailtrap API Response Code: {}", response.statusCode());
            logger.info("Mailtrap API Response Body: {}", response.body());

            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new MailServiceException(response.body());
            }
        } catch (InterruptedException ie) {
            // Suggested by CodeScan
            Thread.currentThread().interrupt();
            throw new MailServiceException(ie);
        } catch (Exception e) {
            throw new MailServiceException(e);
        }
    }

    protected String buildJsonPayload(Mail mail) throws JsonProcessingException {
        MailtrapMail mailtrapMail = new MailtrapMail(
                new MailtrapAddress(mail.from()),
                mail.recipients().stream().map(MailtrapAddress::new).toList(),
                mail.cc().stream().map(MailtrapAddress::new).toList(),
                mail.bcc().stream().map(MailtrapAddress::new).toList(),
                mail.subject(),
                mail.body());
        return objectMapper.writeValueAsString(mailtrapMail);
    }

    protected HttpRequest buildHttpRequest(String payload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Api-Token", apiToken)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
    }
}
