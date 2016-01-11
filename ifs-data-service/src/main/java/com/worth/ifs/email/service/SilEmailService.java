package com.worth.ifs.email.service;

import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.sil.email.service.SilEmailEndpoint;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Uses the Single Integration Layer (SIL) to send out emails, using a Single Integration Layer endpoint to do the actual communication
 */
public class SilEmailService implements EmailService {

    @Autowired
    private SilEmailEndpoint endpoint;

    @Override
    public void sendEmail(EmailAddressResource from, List<EmailAddressResource> to, String subject, String plainTextBodyContent, String htmlBodyContent) {

        SilEmailAddress fromEmail = new SilEmailAddress(from.getName(), from.getEmailAddress());
        List<SilEmailAddress> toEmails = simpleMap(to, recipient -> new SilEmailAddress(recipient.getName(), recipient.getEmailAddress()));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", plainTextBodyContent);
        SilEmailBody htmlBody = new SilEmailBody("text/html", htmlBodyContent);

        endpoint.sendEmail(new SilEmailMessage(fromEmail, toEmails, subject, plainTextBody, htmlBody));
    }
}
