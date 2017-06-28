package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.InviteRoleRepository;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service implementation providing operations around invites for users.
 */
@Service
public class InviteUserServiceImpl implements InviteUserService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InviteRoleRepository inviteRoleRepository;

    @Override
    public ServiceResult<Void> saveUserInvite(UserResource invitedUser, AdminRoleType adminRoleType) {

        return validateInvite(invitedUser, adminRoleType)
                .andOnSuccess(() -> getRole(adminRoleType))
                .andOnSuccess((Role role) -> validateUserNotAlreadyInvited(invitedUser, role)
                        .andOnSuccess(() -> saveInvite(invitedUser, role))
                );
    }

    private ServiceResult<Void> validateInvite(UserResource invitedUser, AdminRoleType adminRoleType) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName()) || adminRoleType == null){
            return serviceFailure(USER_ROLE_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotAlreadyInvited(UserResource invitedUser, Role role) {

        List<RoleInvite> existingInvites = inviteRoleRepository.findByRoleIdAndEmail(role.getId(), invitedUser.getEmail());
        return existingInvites.isEmpty() ? serviceSuccess() : serviceFailure(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED);
    }

    private ServiceResult<Role> getRole(AdminRoleType adminRoleType) {
        return find(roleRepository.findOneByName(userRoleType.getName()), notFoundError(Role.class, userRoleType.getName()));
    }

    private ServiceResult<Void> saveInvite(UserResource invitedUser, Role role) {
        RoleInvite roleInvite = new RoleInvite(invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                role,
                InviteStatus.CREATED);

        inviteRoleRepository.save(roleInvite);

        return serviceSuccess();
    }
}
