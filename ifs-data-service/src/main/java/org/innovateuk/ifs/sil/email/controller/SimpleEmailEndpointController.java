package org.innovateuk.ifs.sil.email.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.email.resource.SilEmailAddress;
import org.innovateuk.ifs.sil.email.resource.SilEmailBody;
import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.springframework.http.HttpStatus.ACCEPTED;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for non-integration test environments
 */
@RestController
@RequestMapping("/silstub/sendmail")
@Profile({"local", "integration-test"})
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

    @PostMapping
    public RestResult<Void> sendMail(@RequestBody SilEmailMessage message) {

        SilEmailBody plainTextBody = simpleFilter(message.getBody(), body -> "text/plain".equals(body.getContentType())).get(0);
        SilEmailBody htmlBody = simpleFilter(message.getBody(), body -> "text/html".equals(body.getContentType())).get(0);

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

                cover.addBodyPart(text);
                cover.addBodyPart(html);

                wrap.setContent(cover);

                MimeMultipart content = new MimeMultipart("related");
                content.addBodyPart(wrap);
                mimeMessage.setContent(content);

                text.setText(plainTextBody.getContent());
                html.setContent(htmlBody.getContent(), "text/html; charset=utf-8");

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

        return restSuccess(ACCEPTED);
    }
}
