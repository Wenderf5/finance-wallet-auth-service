package com.financewallet.services;

import java.util.Properties;
import com.financewallet.DTOs.EmailDTO;
import com.financewallet.exceptions.SendEmailException;

import jakarta.mail.PasswordAuthentication;
import jakarta.enterprise.context.RequestScoped;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@RequestScoped
public class EmailService {
    private Session session;
    private Message message;

    public EmailService getSession(Properties props, String userName, String password) {
        this.session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        return this;
    }

    public EmailService setEmail(EmailDTO email) {
        try {
            Message message = new MimeMessage(this.session);
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email.getTO()));
            message.setSubject(email.getSubject());
            message.setText(email.getContent());
            this.message = message;

            return this;
        } catch (Exception e) {
            throw new SendEmailException(e.getMessage());
        }
    }

    public void send() {
        try {
            Transport.send(message);
        } catch (Exception e) {
            throw new SendEmailException(e.getMessage());
        }
    }
}
