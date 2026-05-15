package com.financewallet.auth.infrastructure.adapters.out;

import com.financewallet.auth.application.dto.Email;
import com.financewallet.auth.infrastructure.exceptions.SendEmailException;

import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImpTests {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailGatewayImp emailGatewayImp;

    @Test
    @DisplayName("Should send email successfully")
    void shouldSendEmailSuccessfully() {
        Email email = new Email("user@example.com", "Test Subject", "<h1>Hello</h1>");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> emailGatewayImp.send(email));

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should throw SendEmailException when MessagingException occurs")
    void shouldThrowSendEmailExceptionWhenMessagingExceptionOccurs() throws Exception {
        Email email = new Email("user@example.com", "Test Subject", "<h1>Hello</h1>");

        MimeMessage faultyMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(faultyMessage);
        doThrow(new MessagingException("Setup failed"))
                .when(faultyMessage).setContent(any(Multipart.class));

        assertThrows(SendEmailException.class, () -> emailGatewayImp.send(email));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
