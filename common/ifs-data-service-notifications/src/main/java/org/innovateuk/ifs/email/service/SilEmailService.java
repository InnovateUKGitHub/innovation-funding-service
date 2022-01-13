package org.innovateuk.ifs.email.service;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.sil.email.resource.SilEmailAddress;
import org.innovateuk.ifs.sil.email.resource.SilEmailBody;
import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.innovateuk.ifs.sil.email.service.SilEmailEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Uses the Single Integration Layer (SIL) to send out emails, using a Single Integration Layer endpoint to do the actual communication
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "sil.available", havingValue = "true", matchIfMissing = true)
public class SilEmailService implements EmailService {

    @Autowired
    private SilEmailEndpoint endpoint;

    @Override
    public ServiceResult<List<EmailAddress>> sendEmail(EmailAddress from, List<EmailAddress> to, String subject, String plainTextBodyContent, String htmlBodyContent) {

        SilEmailAddress fromEmail = new SilEmailAddress(from.getName(), from.getEmailAddress());
        List<SilEmailAddress> toEmails = simpleMap(to, recipient -> new SilEmailAddress(recipient.getName(), recipient.getEmailAddress()));
        SilEmailBody plainTextBody = new SilEmailBody("text/plain", plainTextBodyContent);
        SilEmailBody htmlBody = new SilEmailBody("text/html", htmlBodyContent);

        // TODO improve the logging and change the logging levels to INFO and control whether they are output on a environmental basis
        if (toEmails != null) {
            for (SilEmailAddress silEmailAddress : toEmails) {
                log.info("About to send email to: " + (silEmailAddress != null ? silEmailAddress.getEmail() : " no email address ") + " with subject " + subject);
            }
        } else {
            log.error("toEmails is null");
        }
        ServiceResult<List<EmailAddress>> emailsSentResult = endpoint.sendEmail(new SilEmailMessage(fromEmail, toEmails, subject, plainTextBody, htmlBody)).andOnSuccessReturn(successfullySent -> to);
        // TODO improve the logging and change the logging levels to INFO and control whether they are output on a environmental basis
        if (toEmails != null) {
            for (SilEmailAddress silEmailAddress : toEmails) {
                log.info("Email sent to: " + (silEmailAddress != null ? silEmailAddress.getEmail() : " no email address ") + " with subject " + subject);
            }
        } else {
            log.error("toEmails is null");
        }
        return emailsSentResult;
    }
}
