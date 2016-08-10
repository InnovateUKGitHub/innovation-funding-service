package com.worth.ifs.invite.security;

import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.repository.OrganisationRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.security.SecurityRuleUtil.checkProcessRole;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;

/**
 * Permission rules for ProjectInvite Service
 */
@Component
@PermissionRules
public class ProjectInvitePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_PROJECT_INVITE", description = "A user can view a project invite that they are partners on")
    public boolean partnersOnProjectCanViewInvite(final InviteProjectResource invite, UserResource user) {
        return isUserMemberOfProjectTeam(invite, user);
    }


    @PermissionRule(value = "SEND_PROJECT_INVITE", description = "A user can send a project invite that they are partners on and belong to same organisation")
    public boolean partnersOnProjectCanSendInvite(final InviteProjectResource invite, UserResource user) {
        return isUserPartnerOnProjectWithinSameOrganisation(invite, user);
    }

    @PermissionRule(value = "SAVE_PROJECT_INVITE", description = "A user can save a project invite that they are partners on and belong to same organisation")
    public boolean partnersOnProjectCanSaveInvite(final InviteProjectResource invite, UserResource user) {
        return isUserPartnerOnProjectWithinSameOrganisation(invite, user);
    }



    private boolean isUserPartnerOnProjectWithinSameOrganisation(final InviteProjectResource invite, UserResource user){
        if (invite.getProject() != null && invite.getOrganisation() != null) {
            final Project project = projectRepository.findOneByApplicationId(invite.getProject());
            Role partnerRole = roleRepository.findOneByName(PARTNER.getName());
            final ProjectUser projectUser = projectUserRepository.findByProjectIdAndRoleIdAndUserId(project.getId(),partnerRole.getId(),user.getId());
            final Organisation inviteOrganisation = organisationRepository.findOne(projectUser.getOrganisation().getId());
            if (inviteOrganisation != null && inviteOrganisation.getId() != null) {
                return partnerBelongsToOrganisation(project.getId(), user.getId(),inviteOrganisation.getId());
            }
        }
        return false;
    }

    private boolean isUserMemberOfProjectTeam(final InviteProjectResource invite, UserResource user) {
        if (invite.getProject() != null) {
            final Project project = projectRepository.findOne(invite.getProject());
            return isPartner(project.getId(), user.getId());
        }
        return false;
    }
}
