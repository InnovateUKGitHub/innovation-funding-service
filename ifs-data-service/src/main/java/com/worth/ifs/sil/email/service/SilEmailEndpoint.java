package com.worth.ifs.sil.email.service;

import com.worth.ifs.sil.email.resource.SilEmailMessage;
import com.worth.ifs.transactional.ServiceResult;

/**
 * Represents the communication with the SIL endpoint for sending outbound emails from the application
 */
public interface SilEmailEndpoint {

    ServiceResult<SilEmailMessage> sendEmail(SilEmailMessage message);
}
