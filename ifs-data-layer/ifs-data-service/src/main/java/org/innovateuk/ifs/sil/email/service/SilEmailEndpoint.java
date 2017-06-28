package org.innovateuk.ifs.sil.email.service;

import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * Represents the communication with the SIL endpoint for sending outbound emails from the application
 */
public interface SilEmailEndpoint {

    ServiceResult<SilEmailMessage> sendEmail(SilEmailMessage message);
}
