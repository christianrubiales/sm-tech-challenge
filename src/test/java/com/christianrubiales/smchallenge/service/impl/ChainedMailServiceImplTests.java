package com.christianrubiales.smchallenge.service.impl;

import com.christianrubiales.smchallenge.model.Mail;
import com.christianrubiales.smchallenge.service.MailServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpResponse;
import java.util.List;

import static com.christianrubiales.smchallenge.TestConstants.BODY;
import static com.christianrubiales.smchallenge.TestConstants.EMAIL;
import static com.christianrubiales.smchallenge.TestConstants.SUBJECT;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChainedMailServiceImplTests {

    @Mock
    private MailtrapServiceImpl mailtrapService;

    @Mock
    private ResendServiceImpl resendService;

    @InjectMocks
    private ChainedMailServiceImpl service;


    @BeforeEach
    void setup() {
        service = new ChainedMailServiceImpl(mailtrapService, resendService);
    }

    @Test
    void mailtrapSuccess_noFailoverToResend() {
        Mail mail = new Mail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY);

        when(mailtrapService.sendMail(any()))
                .thenReturn(mock(HttpResponse.class));

        HttpResponse<String> response = service.sendMail(mail);

        assertNotNull(response);
    }

    @Test
    void mailtrapNotSuccess_failoverToResend() {
        Mail mail = new Mail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY);

        when(mailtrapService.sendMail(any()))
                .thenThrow(new MailServiceException("fake mailtrap exception"));

        when(resendService.sendMail(any()))
                .thenReturn(mock(HttpResponse.class));

        HttpResponse<String> response = service.sendMail(mail);

        assertNotNull(response);
    }

    @Test
    void mailtrapNotSuccess_resendNotSuccess() {
        Mail mail = new Mail(EMAIL, List.of(EMAIL), List.of(EMAIL), List.of(EMAIL), SUBJECT, BODY);

        when(mailtrapService.sendMail(any()))
                .thenThrow(new MailServiceException("fake mailtrap exception"));

        when(resendService.sendMail(any()))
                .thenThrow(new MailServiceException("fake resend exception"));

        assertThrows(MailServiceException.class, () -> service.sendMail(mail));
    }
}
