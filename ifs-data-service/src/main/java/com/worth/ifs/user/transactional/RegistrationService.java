package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Transactional service around User operations
 */
public interface RegistrationService {

    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<UserResource> createApplicantUser(Long organisationId, @P("user") UserResource userResource);

    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<UserResource> createApplicantUser(Long organisationId, Optional<Long> competitionId, @P("user") UserResource userResource);

    @PreAuthorize("hasPermission(#userId, 'com.worth.ifs.user.resource.UserResource', 'CREATE')")
    ServiceResult<Void> activateUser(Long userId);
}
