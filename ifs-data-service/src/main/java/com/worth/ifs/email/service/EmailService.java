package com.worth.ifs.email.service;

import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.transactional.ServiceResult;

import java.util.List;

/**
 * A Service that sends out emails via some mechanism or other
 */
public interface EmailService {

    @NotSecured("TODO")
    ServiceResult<List<EmailAddress>> sendEmail(EmailAddress from, List<EmailAddress> to, String subject, String plainTextBody, String htmlBody);
}
