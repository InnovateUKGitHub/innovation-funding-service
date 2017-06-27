package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.InviteRoleRepository;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.project.transactional.EmailService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * TODO - desc here
 */
public class InviteUserServiceImpl implements InviteUserService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InviteRoleRepository inviteRoleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    private static final Log LOG = LogFactory.getLog(InviteUserServiceImpl.class);

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    public static final String WEB_CONTEXT = "/internal-user";

    enum Notifications {
        INVITE_INTERNAL_USER
    }

    @Override
    public ServiceResult<Void> saveUserInvite(UserResource inviteUser, UserRoleType userRoleType) {

        return validateInvite(inviteUser, userRoleType)
                .andOnSuccess(() -> getRole(userRoleType))
                .andOnSuccess((Role role) -> validateUserNotAlreadyInvited(inviteUser, role)
                        .andOnSuccess(() -> saveInvite(inviteUser, role))
                        .andOnSuccess((i) -> inviteInternalUser(i))
                );
    }

    private ServiceResult<Void> validateInvite(UserResource inviteUser, UserRoleType userRoleType) {

        if (StringUtils.isEmpty(inviteUser.getEmail()) || StringUtils.isEmpty(inviteUser.getFirstName())
                || StringUtils.isEmpty(inviteUser.getLastName()) || userRoleType == null) {
            return serviceFailure(USER_ROLE_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotAlreadyInvited(UserResource inviteUser, Role role) {

        List<RoleInvite> existingInvites = inviteRoleRepository.findByRoleIdAndEmail(role.getId(), inviteUser.getEmail());
        return existingInvites.isEmpty() ? serviceSuccess() : serviceFailure(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED);
    }

    private ServiceResult<Role> getRole(UserRoleType userRoleType) {
        return find(roleRepository.findOneByName(userRoleType.getName()), notFoundError(Role.class, userRoleType.getName()));
    }

    private ServiceResult<RoleInvite> saveInvite(UserResource inviteUser, Role role) {
        RoleInvite roleInvite = new RoleInvite(inviteUser.getFirstName() + " " + inviteUser.getLastName(),
                inviteUser.getEmail(),
                generateInviteHash(),
                role,
                InviteStatus.CREATED);

        RoleInvite invite = inviteRoleRepository.save(roleInvite);

        return serviceSuccess(invite);
    }

    private ServiceResult<Void> inviteInternalUser(RoleInvite roleInvite) {

        ServiceResult<Void> inviteContactEmailSendResult = emailService.sendEmail(
                Collections.singletonList(createInviteInternalUserNotificationTarget(roleInvite)),
                createGlobalArgsForInternalUserInvite(roleInvite),
                Notifications.INVITE_INTERNAL_USER);

        inviteContactEmailSendResult.handleSuccessOrFailure(
                failure -> handleInviteError(roleInvite, failure),
                success -> handleInviteSuccess(roleInvite)
        );
        return inviteContactEmailSendResult;
    }

    private NotificationTarget createInviteInternalUserNotificationTarget(RoleInvite roleInvite) {
        return new ExternalUserNotificationTarget(roleInvite.getName(), roleInvite.getEmail());
    }

    private Map<String, Object> createGlobalArgsForInternalUserInvite(RoleInvite roleInvite) {
        Map<String, Object> globalArguments = new HashMap<>();
        globalArguments.put("role", roleInvite.getTarget().getName());
        globalArguments.put("inviteUrl", getInviteUrl(webBaseUrl + WEB_CONTEXT, roleInvite));
        return globalArguments;
    }

    private String getInviteUrl(String baseUrl, RoleInvite inviteResource) {
        return String.format("%s/accept-invite/%s", baseUrl, inviteResource.getHash());
    }

    private ServiceResult<Boolean> handleInviteError(RoleInvite i, ServiceFailure failure) {
        LOG.error(String.format("Invite failed %s , %s (error count: %s)", i.getId(), i.getEmail(), failure.getErrors().size()));
        List<Error> errors = failure.getErrors();
        return serviceFailure(errors);
    }

    private boolean handleInviteSuccess(RoleInvite roleInvite) {
        inviteRoleRepository.save(roleInvite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
        return true;
    }
}
