package com.christianrubiales.smchallenge.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.christianrubiales.smchallenge.TestConstants.BODY;
import static com.christianrubiales.smchallenge.TestConstants.EMAIL;
import static com.christianrubiales.smchallenge.TestConstants.SUBJECT;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailServiceException;
import com.christianrubiales.smchallenge.service.model.MailtrapAddress;
import com.christianrubiales.smchallenge.service.model.MailtrapMail;
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

@ExtendWith(MockitoExtension.class)
class MailtrapServiceImplTests {

    @Mock
    private Validator validator;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private MailtrapServiceImpl service;

    @BeforeEach
    void setup() {
        service = new MailtrapServiceImpl(new ObjectMapper(), validator) {
            @Override
            protected HttpClient getHttpClient() {
                return httpClient;
            }
        };
        ReflectionTestUtils.setField(service, "apiUrl", "https://test.com");
        ReflectionTestUtils.setField(service, "apiToken", "TOKEN");
    }

    @Test
    void convert_allInputFieldsPopulated() {
        Mail mail = new Mail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY);

        MailtrapMail mailtrapMail = service.convert(mail);

        assertEquals(EMAIL, mailtrapMail.from().email());
        assertEquals(EMAIL, mailtrapMail.to().getFirst().email());
        assertEquals(EMAIL, mailtrapMail.cc().getFirst().email());
        assertEquals(EMAIL, mailtrapMail.bcc().getFirst().email());
        assertEquals(SUBJECT, mailtrapMail.subject());
        assertEquals(BODY, mailtrapMail.text());
    }

    @Test
    void convert_allRecipientListsNotPopulated() {
        Mail mail = new Mail(EMAIL, null, null, null, SUBJECT, BODY);

        MailtrapMail mailtrapMail = service.convert(mail);

        assertEquals(EMAIL, mailtrapMail.from().email());
        assertNull(mailtrapMail.to());
        assertNull(mailtrapMail.cc());
        assertNull(mailtrapMail.bcc());
        assertEquals(SUBJECT, mailtrapMail.subject());
        assertEquals(BODY, mailtrapMail.text());
    }

    @Test
    void buildJsonPayload_noValidatorViolations() throws JsonProcessingException {
        MailtrapAddress address = new MailtrapAddress(EMAIL);
        MailtrapMail mailtrapMail = new MailtrapMail(address, List.of(address), null, null, SUBJECT, BODY);

        String json = service.buildJsonPayload(mailtrapMail);

        assertEquals(
                "{'from':{'email':'a@b.c'},'to':[{'email':'a@b.c'}],'cc':null,'bcc':null,'subject':'subject','text':'body'}",
                json.replace("\"", "'"));
    }

    @Test
    void buildJsonPayload_noValidatorViolationsFull() throws JsonProcessingException {
        MailtrapAddress address = new MailtrapAddress(EMAIL);
        MailtrapMail mailtrapMail = new MailtrapMail(address, List.of(address), List.of(address), List.of(address), SUBJECT, BODY);

        String json = service.buildJsonPayload(mailtrapMail);

        assertEquals(
                "{'from':{'email':'a@b.c'},'to':[{'email':'a@b.c'}],'cc':[{'email':'a@b.c'}],'bcc':[{'email':'a@b.c'}],'subject':'subject','text':'body'}",
                json.replace("\"", "'"));
    }

    @Test
    void buildJsonPayload_withValidatorViolations_exceptionThrown() {
        MailtrapAddress address = new MailtrapAddress(EMAIL);
        MailtrapMail mailtrapMail = new MailtrapMail(address, List.of(address), null, null, SUBJECT, BODY);

        ConstraintViolation<MailtrapMail> violation = mock(ConstraintViolation.class);
        when(validator.validate(any(MailtrapMail.class)))
                .thenReturn(Set.of(violation));

        assertThrows(MailServiceException.class, () -> service.buildJsonPayload(mailtrapMail));
    }

    @Test
    void buildHttpRequest() throws JsonProcessingException {
        MailtrapAddress address = new MailtrapAddress(EMAIL);
        MailtrapMail mailtrapMail = new MailtrapMail(address, List.of(address), List.of(address), List.of(address), SUBJECT, BODY);
        String payload = service.buildJsonPayload(mailtrapMail);

        HttpRequest request = service.buildHttpRequest(payload);

        assertNotNull(request);
    }

    @Test
    void sendMail_responseCodeOk() throws IOException, InterruptedException {
        MailtrapServiceImpl service = mock(MailtrapServiceImpl.class);
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
        MailtrapServiceImpl service = mock(MailtrapServiceImpl.class);
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
