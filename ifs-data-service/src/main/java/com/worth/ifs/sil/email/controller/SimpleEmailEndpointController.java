package com.worth.ifs.sil.email.controller;

import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.worth.ifs.util.CollectionFunctions.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for non-integration test environments
 */
@RestController
@RequestMapping("/silstub")
public class SimpleEmailEndpointController {

    private static final Log LOG = LogFactory.getLog(SimpleEmailEndpointController.class);

    @Value("${sil.stub.send.mail.from.ifs:false}")
    private Boolean sendMailFromSilStub;

    @Value("${sil.stub.smtp.host:localhost}")
    private String smtpServer;

    @Value("${sil.stub.smtp.port:25}")
    private Integer smtpPort;

    @Value("${sil.stub.smtp.timeout.millis:5000}")
    private Integer timeoutMillis;

    @Value("${sil.stub.smtp.password:}")
    private String password;

    @Value("${sil.stub.smtp.user:}")
    private String user;

    @Value("${sil.stub.smtp.auth:false}")
    private Boolean auth;

    @Value("${sil.stub.smtp.starttls.enable:false}")
    private Boolean tlsEnabled;

    @RequestMapping(value="/sendmail", method = POST)
    public void sendMail(@RequestBody SilEmailMessage message, HttpServletResponse response) {

        SilEmailBody plainTextBody = simpleFilter(message.getBody(), body -> body.getContentType().equals("text/plain")).get(0);
        SilEmailBody htmlBody = simpleFilter(message.getBody(), body -> body.getContentType().equals("text/html")).get(0);

        LOG.info("Stubbing out SIL outbound email:\n\n" +
                "From: " + message.getFrom().getEmail() + "\n" +
                "To: " + simpleJoiner(simpleMap(message.getTo(), SilEmailAddress::getEmail), ", ") + "\n" +
                "Subject: " + message.getSubject() + "\n" +
                "Plain text body: " +  plainTextBody.getContent() + "\n" +
                "HTML body: " + htmlBody.getContent());

        if (sendMailFromSilStub) {

            Properties props = new Properties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.connectiontimeout", timeoutMillis);
            props.put("mail.smtp.timeout", timeoutMillis);

            if (auth) {
                props.put("mail.smtp.user", user);
                props.put("mail.smtp.password", password);
                props.put("mail.smtp.auth", auth);
                props.put("mail.smtp.starttls.enable", true);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }

            try {

                Session session = Session.getDefaultInstance(props);
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setSubject(message.getSubject(), "UTF-8");

                List<InternetAddress> recipientAddresses = simpleMap(message.getTo(), recipient -> {
                    try {
                        return new InternetAddress(recipient.getEmail(), recipient.getName());
                    } catch (UnsupportedEncodingException e) {
                        LOG.error("Unable to construct recipient email address for recipient " + recipient, e);
                        throw new IllegalArgumentException("Unable to construct recipient email address for recipient " + recipient, e);
                    }
                });

                mimeMessage.setFrom(new InternetAddress(message.getFrom().getEmail(), message.getFrom().getName()));
                mimeMessage.setReplyTo(new Address[]{new InternetAddress(message.getFrom().getEmail(), message.getFrom().getName())});
                mimeMessage.setRecipients(Message.RecipientType.TO, recipientAddresses.toArray(new InternetAddress[recipientAddresses.size()]));

                // COVER WRAP
                MimeBodyPart wrap = new MimeBodyPart();

                // ALTERNATIVE TEXT/HTML CONTENT
                MimeMultipart cover = new MimeMultipart("alternative");
                MimeBodyPart html = new MimeBodyPart();
                MimeBodyPart text = new MimeBodyPart();
                cover.addBodyPart(html);
                cover.addBodyPart(text);

                wrap.setContent(cover);

                MimeMultipart content = new MimeMultipart("related");
                content.addBodyPart(wrap);
                mimeMessage.setContent(content);

                text.setText(plainTextBody.getContent());
                html.setText(htmlBody.getContent());

                // SEND THE MESSAGE
                mimeMessage.setSentDate(new Date());
                mimeMessage.saveChanges();

                Transport transport = session.getTransport("smtp");

                try {
                    if (auth) {
                        transport.connect(smtpServer, smtpPort, user, password);
                        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                    } else {
                        transport.connect();
                        transport.send(mimeMessage);
                    }
                } finally {
                    transport.close();
                }

            } catch (UnsupportedEncodingException | MessagingException e) {

                // warn about the exception but don't fail - this local mail sending is just a placeholder for the
                // SIL functionality
                LOG.warn("Unable to send email from SIL stub endpoint", e);
            }
        }

        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}
