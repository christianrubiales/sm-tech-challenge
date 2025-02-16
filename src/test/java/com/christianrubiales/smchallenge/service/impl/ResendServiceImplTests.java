package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailServiceException;
import com.christianrubiales.smchallenge.service.model.ResendMail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.christianrubiales.smchallenge.TestConstants.BODY;
import static com.christianrubiales.smchallenge.TestConstants.EMAIL;
import static com.christianrubiales.smchallenge.TestConstants.SUBJECT;

@ExtendWith(MockitoExtension.class)
class ResendServiceImplTests {

    @Mock
    private Validator validator;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private ResendServiceImpl service;

    @BeforeEach
    void setup() {
        service = new ResendServiceImpl(new ObjectMapper(), validator) {
            @Override
            protected HttpClient getHttpClient() {
                return httpClient;
            }
        };
        ReflectionTestUtils.setField(service, "apiUrl", "https://test.com");
        ReflectionTestUtils.setField(service, "apiToken", "TOKEN");
    }

    @Test
    void buildHttpRequest() throws JsonProcessingException {
        ResendMail resendMail = new ResendMail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY);
        String payload = service.buildJsonPayload(resendMail);

        HttpRequest request = service.buildHttpRequest(payload);

        assertNotNull(request);
    }

    @Test
    void buildJsonPayload_noValidatorViolations() throws JsonProcessingException {
        ResendMail resendMail = new ResendMail(EMAIL, List.of(EMAIL), null, null, SUBJECT, BODY);

        String json = service.buildJsonPayload(resendMail);

        assertEquals(
                "{'from':'a@b.c','to':['a@b.c'],'cc':null,'bcc':null,'subject':'subject','text':'body'}",
                json.replace("\"", "'"));
    }

    @Test
    void buildJsonPayload_noValidatorViolationsFull() throws JsonProcessingException {
        ResendMail resendMail = new ResendMail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY);

        String json = service.buildJsonPayload(resendMail);

        assertEquals(
                "{'from':'a@b.c','to':['a@b.c'],'cc':['a@b.c'],'bcc':['a@b.c'],'subject':'subject','text':'body'}",
                json.replace("\"", "'"));
    }

    @Test
    void buildJsonPayload_withValidatorViolations_exceptionThrown() {
        ConstraintViolation<ResendMail> violation = mock(ConstraintViolation.class);
        when(validator.validate(any(ResendMail.class)))
                .thenReturn(Set.of(violation));

        assertThrows(MailServiceException.class,
                () -> service.buildJsonPayload(new ResendMail(null, null, null, null, null, null)));
    }

    @Test
    void sendMail_responseCodeOk() throws IOException, InterruptedException {
        ResendServiceImpl service = mock(ResendServiceImpl.class);
        HttpClient client = mock(HttpClient.class);
        HttpResponse response = mock(HttpResponse.class);

        when(response.statusCode())
                .thenReturn(HttpStatus.OK.value());
        when(client.send(any(), any()))
                .thenReturn(response);
        when(service.getHttpClient())
                .thenReturn(client);
        when(service.sendMail(any()))
                .thenCallRealMethod();

        service.sendMail(new Mail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY));
    }

    @Test
    void sendMail_responseCodeNotOk() throws IOException, InterruptedException {
        ResendServiceImpl service = mock(ResendServiceImpl.class);
        HttpClient client = mock(HttpClient.class);
        HttpResponse response = mock(HttpResponse.class);

        when(response.statusCode())
                .thenReturn(HttpStatus.BAD_REQUEST.value());
        when(client.send(any(), any()))
                .thenReturn(response);
        when(service.getHttpClient())
                .thenReturn(client);
        when(service.sendMail(any()))
                .thenCallRealMethod();

        assertThrows(MailServiceException.class,
                () -> service.sendMail(new Mail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY)));
    }
}
