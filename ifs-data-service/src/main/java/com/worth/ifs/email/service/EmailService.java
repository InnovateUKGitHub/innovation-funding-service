package com.worth.ifs.email.service;

import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.transactional.ServiceResult;

import java.util.List;

/**
 * A Service that sends out emails via some mechanism or other
 */
public interface EmailService {

    ServiceResult<List<EmailAddressResource>> sendEmail(EmailAddressResource from, List<EmailAddressResource> to, String subject, String plainTextBody, String htmlBody);
}
