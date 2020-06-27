package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.grant.domain.GrantProcess;

import java.util.List;
import java.util.Optional;

public interface GrantProcessService {
    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    List<GrantProcess> findReadyToSend();

    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    Optional<GrantProcess> findOneReadyToSend();

    @NotSecured(value = "Service is called from other services always")
    void createGrantProcess(long applicationId);

    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    void sendSucceeded(long applicationId);

    @NotSecured(value = "Only called by scheduled process", mustBeSecuredByOtherServices = false)
    void sendFailed(long applicationId, String message);

    @NotSecured(value = "Service is called from other services always")
    Optional<GrantProcess> findByApplicationId(Long id);
}
