package com.worth.ifs.email.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.sil.email.service.SilEmailEndpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Uses the Single Integration Layer (SIL) to send out emails, using a Single Integration Layer endpoint to do the actual communication
 */
@Service
public class SilEmailService implements EmailService {

    private static final Log LOG = LogFactory.getLog(SilEmailService.class);

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
                LOG.error("About to send email to: " + (silEmailAddress != null ? silEmailAddress.getEmail() : " no email address ") + " with subject " + subject);
            }
        } else {
            LOG.error("toEmails is null");
        }
        ServiceResult<List<EmailAddress>> listServiceResult = endpoint.sendEmail(new SilEmailMessage(fromEmail, toEmails, subject, plainTextBody, htmlBody)).andOnSuccessReturn(successfullySent -> to);
        // TODO improve the logging and change the logging levels to INFO and control whether they are output on a environmental basis
        if (toEmails != null) {
            for (SilEmailAddress silEmailAddress : toEmails) {
                LOG.error("Email sent to: " + (silEmailAddress != null ? silEmailAddress.getEmail() : " no email address ") + " with subject " + subject);
            }
        } else {
            LOG.error("toEmails is null");
        }
        return  listServiceResult;
    }
}
