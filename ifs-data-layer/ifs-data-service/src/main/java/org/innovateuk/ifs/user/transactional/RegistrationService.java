package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserCreationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

/**
 * Transactional service around User operations
 */
public interface RegistrationService {

    @PreAuthorize("hasPermission(#user, 'CREATE')")
    ServiceResult<UserResource> createUser(UserCreationResource user);

    @SecuredBySpring(value = "CREATE", securedType = User.class,
            description = "A System Registration User can activate new monitoring officer users on behalf of non-logged in users with invite hash")
    @PreAuthorize("hasAuthority('system_registrar')")
    ServiceResult<User> activatePendingUser(User user, String password, String hash);

    @PreAuthorize("hasPermission(#user, 'VERIFY')")
    ServiceResult<Void> resendUserVerificationEmail(@P("user") final UserResource user);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<UserResource> activateUser(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'DEACTIVATE')")
    ServiceResult<UserResource> deactivateUser(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateApplicantAndSendDiversitySurvey(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'ACTIVATE')")
    ServiceResult<Void> activateAssessorAndSendDiversitySurvey(long userId);

    @PreAuthorize("hasPermission(#userToEdit, 'EDIT_INTERNAL_USER')")
    @SecuredBySpring(value = "CREATE", securedType = StakeholderRegistrationResource.class, description = "A System Registration User can create new Stakeholders on behalf of non-logged in users with invite hash")
    ServiceResult<UserResource> editInternalUser(UserResource userToEdit, Role userRoleType);

}