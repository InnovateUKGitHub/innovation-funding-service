package com.worth.ifs.sil.email.service;

import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.transactional.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.worth.ifs.sil.email.service.RestSilEmailEndpoint.ServiceFailures.UNABLE_TO_SEND_MAIL;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.success;

/**
 * A simple logging implementation of the SIL email endpoint as opposed to a REST-based endpoint
 */
@Component
public class RestSilEmailEndpoint extends BaseRestService implements SilEmailEndpoint {

    private static final Log LOG = LogFactory.getLog(RestSilEmailEndpoint.class);

    enum ServiceFailures {
        UNABLE_TO_SEND_MAIL
    }

    @Value("${sil.rest.sendmail}")
    private String silSendmailPath;

    @Override
    public ServiceResult<SilEmailMessage> sendEmail(SilEmailMessage message) {

        ResponseEntity<String> response = restPostWithEntity(silSendmailPath, message, String.class);

        if (!response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            LOG.warn("Failed when sending email to SIL: " + response.getBody());
            return failure(UNABLE_TO_SEND_MAIL);
        }

        LOG.debug("Successfully sent email to SIL: " + message);
        return success(message);
    }
}
