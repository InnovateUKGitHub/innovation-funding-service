package org.innovateuk.ifs.email.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Stub (always succeeding) implementation of EmailService, available when we are not reliant on a SIL being present.
 * Used as part of the integration tests when we don't rely on an HTTP server to be running.
 */
@Component
@ConditionalOnProperty(name = "sil.available", havingValue = "false")
public class StubEmailService implements EmailService {

    @Override
    public ServiceResult<List<EmailAddress>> sendEmail(EmailAddress from, List<EmailAddress> to, String subject, String plainTextBody, String htmlBody) {
        return serviceSuccess(emptyList());
    }
}
