package com.example.myapplication.MAILSS;

import android.util.Log;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSs {
    private String pwdFromAddress = "sqxTXuZbmgA2Qtm6vFYb";
    private String fromAddress = "pargev.na@mail.ru";
    private String smtpAddress = "smtp.mail.ru";
    private int smtpPort = 587;

    public boolean sendMessage(String messageText, String messageHead, String toAddress) {
        boolean r_s_msg = false;

        // Установка свойств для SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", smtpAddress);
        properties.put("mail.smtp.port", smtpPort);

        // Создание сессии с аутентификацией
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAddress, pwdFromAddress);
            }
        });

        try {
            // Создание сообщения
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject(messageHead);
            message.setText(messageText);

            // Отправить сообщение
            Transport.send(message);
            r_s_msg = true;
            System.out.println("Send message successful");

        } catch (MessagingException e) {
            r_s_msg = false;
            Log.d("mail_msg",e.getMessage().toString());
            e.printStackTrace();
        }

        return r_s_msg;
    }

}