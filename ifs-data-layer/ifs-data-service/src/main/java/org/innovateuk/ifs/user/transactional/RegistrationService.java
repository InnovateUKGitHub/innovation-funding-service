package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
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
    ServiceResult<UserResource> createOrganisationUser(long organisationId, @P("user") UserResource userResource);

    @PreAuthorize("hasPermission(#user, 'VERIFY')")
    ServiceResult<Void> sendUserVerificationEmail(@P("user") final UserResource user, final Optional<Long> competitionId);

    @PreAuthorize("hasPermission(#user, 'VERIFY')")
    ServiceResult<Void> resendUserVerificationEmail(@P("user") final UserResource user);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateUser(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'DEACTIVATE')")
    ServiceResult<Void> deactivateUser(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateApplicantAndSendDiversitySurvey(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateAssessorAndSendDiversitySurvey(long userId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CREATE", securedType = InternalUserRegistrationResource.class, description = "A System Registration User can create new internal Users on behalf of non-logged in users with invite hash")
    ServiceResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationResource userRegistrationResource);

    @PreAuthorize("hasPermission(#userToEdit, 'EDIT_INTERNAL_USER')")
    ServiceResult<Void> editInternalUser(UserResource userToEdit, UserRoleType userRoleType);
}