package com.worth.ifs.email.service;

import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailBody;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.sil.email.service.SilEmailEndpoint;
import com.worth.ifs.transactional.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.transactional.ServiceResult.handlingErrors;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Uses the Single Integration Layer (SIL) to send out emails, using a Single Integration Layer endpoint to do the actual communication
 */
@Service
public class SilEmailService implements EmailService {

    @Autowired
    private SilEmailEndpoint endpoint;

    @Override
    public ServiceResult<List<EmailAddress>> sendEmail(EmailAddress from, List<EmailAddress> to, String subject, String plainTextBodyContent, String htmlBodyContent) {

        return handlingErrors(() -> {

            SilEmailAddress fromEmail = new SilEmailAddress(from.getName(), from.getEmailAddress());
            List<SilEmailAddress> toEmails = simpleMap(to, recipient -> new SilEmailAddress(recipient.getName(), recipient.getEmailAddress()));
            SilEmailBody plainTextBody = new SilEmailBody("text/plain", plainTextBodyContent);
            SilEmailBody htmlBody = new SilEmailBody("text/html", htmlBodyContent);

            return endpoint.sendEmail(new SilEmailMessage(fromEmail, toEmails, subject, plainTextBody, htmlBody)).map(successfullySent -> success(to));
        });
    }
}
