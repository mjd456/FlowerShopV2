package il.cshaifasweng.OCSFMediatorExample.server;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        final String fromEmail = "noreplyflowershop2025@gmail.com"; // your Gmail address
        final String appPassword = "fhzd pchs lwlf ijcd";  // Gmail app password (not your Gmail password)

        // SMTP configuration for Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Authenticate
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        // Compose message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        // Send
        Transport.send(message);
    }
}
