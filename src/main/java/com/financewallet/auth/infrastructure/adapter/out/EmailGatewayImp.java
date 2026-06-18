package com.financewallet.auth.infrastructure.adapter.out;

import com.financewallet.auth.application.dto.Email;
import com.financewallet.auth.application.gateway.EmailGateway;
import com.financewallet.auth.infrastructure.exception.SendEmailException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailGatewayImp implements EmailGateway {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(Email email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new SendEmailException("Failed to send email");
        }
    }
}
