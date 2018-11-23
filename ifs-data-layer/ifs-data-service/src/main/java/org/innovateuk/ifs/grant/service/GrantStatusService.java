package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.grant.domain.GrantStatus;

import java.util.List;

public interface GrantStatusService {
    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    List<GrantStatus> findReadyToSend();

    @NotSecured(value = "Service is called from other services always")
    void sendRequested(long applicationId);

    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    void sendSucceeded(long applicationId);
}
