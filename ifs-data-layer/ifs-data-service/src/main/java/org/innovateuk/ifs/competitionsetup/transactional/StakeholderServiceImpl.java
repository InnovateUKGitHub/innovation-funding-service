package org.innovateuk.ifs.competitionsetup.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.STAKEHOLDER_INVITE_INVALID_EMAIL;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_EMAIL_TAKEN;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;

/**
 * Transactional and secured service implementation providing operations around stakeholders.
 */
@Service
public class StakeholderServiceImpl extends BaseTransactionalService implements StakeholderService {

    @Autowired
    private RoleInviteRepository roleInviteRepository;

    private static final String DEFAULT_INTERNAL_USER_EMAIL_DOMAIN = "innovateuk.gov.uk";

    @Value("${ifs.system.internal.user.email.domain}")
    private String internalUserEmailDomain;

    @Override
    @Transactional
    public ServiceResult<Void> saveStakeholderInvite(UserResource invitedUser) {

        return validateInvite(invitedUser)
                .andOnSuccess(() -> validateEmail(invitedUser.getEmail()))
                .andOnSuccess(() -> validateUserEmailAvailable(invitedUser))
                .andOnSuccess(() -> validateUserNotAlreadyInvited(invitedUser))
                .andOnSuccess(() -> saveInvite(invitedUser));
    }

    private ServiceResult<Void> validateInvite(UserResource invitedUser) {

        if (StringUtils.isEmpty(invitedUser.getEmail()) || StringUtils.isEmpty(invitedUser.getFirstName())
                || StringUtils.isEmpty(invitedUser.getLastName())){
            return serviceFailure(USER_ROLE_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateEmail(String email) {

        internalUserEmailDomain = StringUtils.defaultIfBlank(internalUserEmailDomain, DEFAULT_INTERNAL_USER_EMAIL_DOMAIN);

        String domain = StringUtils.substringAfter(email, "@");

        if (internalUserEmailDomain.equalsIgnoreCase(domain)) {
            return serviceFailure(STAKEHOLDER_INVITE_INVALID_EMAIL);
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateUserEmailAvailable(UserResource invitedUser) {
        return userRepository.findByEmail(invitedUser.getEmail()).isPresent() ? serviceFailure(USER_ROLE_INVITE_EMAIL_TAKEN) : serviceSuccess() ;
    }

    private ServiceResult<Void> validateUserNotAlreadyInvited(UserResource invitedUser) {

        List<RoleInvite> existingInvites = roleInviteRepository.findByEmail(invitedUser.getEmail());
        return existingInvites.isEmpty() ? serviceSuccess() : serviceFailure(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED);
    }

    private ServiceResult<Void> saveInvite(UserResource invitedUser) {
        RoleInvite roleInvite = new RoleInvite(invitedUser.getFirstName() + " " + invitedUser.getLastName(),
                invitedUser.getEmail(),
                generateInviteHash(),
                Role.MONITORING_OFFICER, // TODO - XXX - Change this to STAKEHOLDER once 2994 is merged
                CREATED);

        roleInviteRepository.save(roleInvite);

        return serviceSuccess();
    }
}
