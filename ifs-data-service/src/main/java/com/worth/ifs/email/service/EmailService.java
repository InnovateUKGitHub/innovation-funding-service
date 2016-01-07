package com.worth.ifs.email.service;

import com.worth.ifs.email.resource.EmailAddressResource;

import java.util.List;

/**
 * A Service that sends out emails via some mechanism or other
 */
public interface EmailService {

    void sendEmail(EmailAddressResource from, String subject, String plainTextBody, String htmlBody, EmailAddressResource... recipients);

    void sendEmail(EmailAddressResource from, String subject, String plainTextBody, String htmlBody, List<EmailAddressResource>... recipients);
}
