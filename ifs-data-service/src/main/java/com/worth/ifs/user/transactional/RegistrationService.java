package com.worth.ifs.user.transactional;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * Transactional service around User operations
 */
public interface RegistrationService {

    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<UserResource> createUser(@P("user") UserRegistrationResource userResource);

    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<UserResource> createOrganisationUser(Long organisationId, @P("user") UserResource userResource);

    @PreAuthorize("hasPermission(#user, 'VERIFY')")
    ServiceResult<Void> sendUserVerificationEmail(@P("user") final UserResource user, final Optional<Long> competitionId);

    @PreAuthorize("hasPermission(#user, 'VERIFY')")
    ServiceResult<Void> resendUserVerificationEmail(@P("user") final UserResource user);

    @PreAuthorize("hasPermission(#userId, 'com.worth.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateUser(Long userId);
}
