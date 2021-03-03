package org.innovateuk.ifs.project.invite.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.domain.ProjectPartnerInvite;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.invite.resource.SendProjectPartnerInviteResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_PARTNER;

@Component
public class ProjectInviteValidator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Autowired
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public ServiceResult<Void> validate(ProjectUserInviteResource invite) {
        return validateProjectInviteResource(invite).andOnSuccess(() ->
                validateUserNotAlreadyInvited(invite.getProject(), invite.getEmail()).andOnSuccess(() ->
                        validateTargetUserIsValid(invite.getProject(), invite.getEmail())));
    }

    public ServiceResult<Void> validate(long projectId, SendProjectPartnerInviteResource invite) {
        return validateProjectPartnerInviteResource(invite).andOnSuccess(() ->
                validateUserNotAlreadyInvited(projectId, invite.getEmail()).andOnSuccess(() ->
                        validateTargetUserIsValid(projectId, invite.getEmail())));
    }

    private ServiceResult<Void> validateProjectInviteResource(ProjectUserInviteResource projectUserInviteResource) {
        if (StringUtils.isEmpty(projectUserInviteResource.getEmail()) || StringUtils.isEmpty(projectUserInviteResource.getName())
                || projectUserInviteResource.getProject() == null || projectUserInviteResource.getOrganisation() == null) {
            return serviceFailure(PROJECT_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateProjectPartnerInviteResource(SendProjectPartnerInviteResource invite) {
        if (StringUtils.isEmpty(invite.getEmail()) || StringUtils.isEmpty(invite.getUserName())
                || StringUtils.isEmpty(invite.getOrganisationName())) {
            return serviceFailure(PROJECT_INVITE_INVALID);
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> validateTargetUserIsValid(long projectId, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        return existingUser.map(user ->
                validateUserIsNotAlreadyOnProject(projectId, user)).
                orElse(serviceSuccess());
    }

    private ServiceResult<Void> validateUserNotAlreadyInvited(long projectId, String email) {
        List<ProjectUserInvite> projectUserInvites = projectUserInviteRepository.findByProjectIdAndEmail(projectId, email);
        List<ProjectPartnerInvite> projectPartnerInvites = projectPartnerInviteRepository.findByProjectIdAndEmail(projectId, email);
        if(projectUserInvites.isEmpty() && projectPartnerInvites.isEmpty()) {
            return serviceSuccess();
        } else {
            return serviceFailure(PROJECT_SETUP_INVITE_TARGET_USER_ALREADY_INVITED_ON_PROJECT);
        }
    }

    private ServiceResult<Void> validateUserIsNotAlreadyOnProject(long projectId, User user) {
        List<ProjectUser> existingUserEntryForProject = projectUserRepository.findByProjectIdAndUserIdAndRole(projectId, user.getId(), PROJECT_PARTNER);

        return existingUserEntryForProject.isEmpty() ? serviceSuccess() :
                serviceFailure(PROJECT_SETUP_INVITE_TARGET_USER_ALREADY_EXISTS_ON_PROJECT);
    }

}
