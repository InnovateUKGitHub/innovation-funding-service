package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.resource.UserResource;

import java.util.Optional;

/**
 * Transactional service around User operations
 */
public interface RegistrationService {

    @NotSecured("TODO - implement when permissions matrix defined")
    ServiceResult<Void> createApplicantUser(Long organisationId, UserResource userResource);

    @NotSecured("TODO - implement when permissions matrix defined")
    ServiceResult<Void> createApplicantUser(Long organisationId, Optional<Long> competitionId, UserResource userResource);

    @NotSecured("TODO - implement when permissions matrix defined")
    ServiceResult<Void> activateUser(Long userId);
}
