package com.hotel.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

public class EmailReportUtil {

    public static void sendReport(String toEmail, String pdfPath) {

        final String fromEmail = EnvConfig.getEmailUser();
        final String password = EnvConfig.getEmailPass();

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("Bao cao doanh thu khach san");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Bao cao duoc dinh kem file PDF.");

            MimeBodyPart filePart = new MimeBodyPart();
            filePart.attachFile(new File(pdfPath));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(filePart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Gui email thanh cong!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}