package com.worth.ifs.sil.email.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.worth.ifs.application.transactional.ServiceFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static com.worth.ifs.commons.service.ServiceResult.*;

/**
 * A simple logging implementation of the SIL email endpoint as opposed to a REST-based endpoint
 */
@Component
public class RestSilEmailEndpoint extends BaseRestService implements SilEmailEndpoint {

    private static final Log LOG = LogFactory.getLog(RestSilEmailEndpoint.class);

    @Value("${sil.rest.baseURL}")
    String silRestServiceUrl;

    @Value("${sil.rest.sendmail}")
    String silSendmailPath;

    @Override
    public ServiceResult<SilEmailMessage> sendEmail(SilEmailMessage message) {

        return handlingErrors(() -> {

            ResponseEntity<String> response = restPostWithEntity(silSendmailPath, message, String.class);

            if (!response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                LOG.warn("Failed when sending email to SIL: " + response.getBody());
                return serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE));
            }

            LOG.debug("Successfully sent email to SIL: " + message);
            return serviceSuccess(message);
        });
    }

    @Override
    protected String getDataRestServiceURL() {
        return silRestServiceUrl;
    }
}
