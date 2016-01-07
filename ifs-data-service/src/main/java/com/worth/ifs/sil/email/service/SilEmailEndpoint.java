package com.worth.ifs.sil.email.service;

import com.worth.ifs.sil.email.resource.SilEmailMessage;

/**
 * Represents the communication with the SIL endpoint for sending outbound emails from the application
 */
public interface SilEmailEndpoint {

    void sendEmail(SilEmailMessage message);
}
