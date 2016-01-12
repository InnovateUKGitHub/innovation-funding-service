package com.worth.ifs.sil.email.service;

import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.transactional.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.worth.ifs.transactional.ServiceResult.success;

/**
 * A simple logging implementation of the SIL email endpoint as opposed to a REST-based endpoint
 */
public class LoggingSilEmailEndpoint implements SilEmailEndpoint {

    private static final Log LOG = LogFactory.getLog(LoggingSilEmailEndpoint.class);

    @Override
    public ServiceResult<SilEmailMessage> sendEmail(SilEmailMessage message) {
        LOG.debug("Logging email being sent to SIL - " + message);
        return success(message);
    }
}
