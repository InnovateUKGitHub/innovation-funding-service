package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;
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

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateUser(Long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateUserAndSendDiversitySurvey(Long userId);
}
