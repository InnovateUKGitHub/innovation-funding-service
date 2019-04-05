package org.innovateuk.ifs.email.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.email.resource.EmailAddress;

import java.util.List;

/**
 * A Service that sends out emails via some mechanism or other
 */
public interface EmailService {

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    ServiceResult<List<EmailAddress>> sendEmail(EmailAddress from, List<EmailAddress> to, String subject, String plainTextBody, String htmlBody);
}
