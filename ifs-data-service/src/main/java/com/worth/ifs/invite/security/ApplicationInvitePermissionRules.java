package com.worth.ifs.invite.security;

import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.repository.InviteOrganisationRepository;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.checkProcessRole;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;

/**
 * Permission rules for {@link ApplicationInvite} and {@link ApplicationInviteResource} for permissioning
 */
@Component
@PermissionRules
public class ApplicationInvitePermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @PermissionRule(value = "SEND", description = "lead applicant can invite to the application")
    public boolean leadApplicantCanInviteToTheApplication(final ApplicationInvite invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "SEND", description = "collaborator can invite to the application for thier organisation")
    public boolean collaboratorCanInviteToApplicationForTheirOrganisation(final ApplicationInvite invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }


    @PermissionRule(value = "SAVE", description = "lead applicant can save invite to the application")
    public boolean leadApplicantCanSaveInviteToTheApplication(final ApplicationInviteResource invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "SAVE", description = "collaborator can save invite to the application for thier organisation")
    public boolean collaboratorCanSaveInviteToApplicationForTheirOrganisation(final ApplicationInviteResource invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "READ", description = "collaborator can view an invite to the application on for their organisation")
    public boolean collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(final ApplicationInvite invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "READ", description = "lead applicant can view an invite to the application")
    public boolean leadApplicantReadInviteToTheApplication(final ApplicationInvite invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    private boolean isCollaboratorOnInvite(final ApplicationInvite invite, final UserResource user) {
        final long applicationId = invite.getTarget().getId();
        final InviteOrganisation inviteOrganisation = invite.getInviteOrganisation();
        if (inviteOrganisation != null && inviteOrganisation.getOrganisation() != null) {
            long organisationId = inviteOrganisation.getOrganisation().getId();
            final boolean isCollaborator = checkProcessRole(user, applicationId, organisationId, COLLABORATOR, roleRepository, processRoleRepository);
            return isCollaborator;
        }
        return false;
    }

    private boolean isCollaboratorOnInvite(final ApplicationInviteResource invite, final UserResource user) {
        if (invite.getApplication() != null && invite.getInviteOrganisation() != null) {
            final InviteOrganisation inviteOrganisation = inviteOrganisationRepository.findOne(invite.getInviteOrganisation());
            if (inviteOrganisation != null && inviteOrganisation.getOrganisation() != null) {
                return checkProcessRole(user, invite.getApplication(), inviteOrganisation.getOrganisation().getId(), COLLABORATOR, roleRepository, processRoleRepository);
            }
        }
        return false;
    }

    private boolean isLeadForInvite(final ApplicationInvite invite, final UserResource user) {
        final long applicationId = invite.getTarget().getId();
        return checkProcessRole(user, invite.getTarget().getId(), UserRoleType.LEADAPPLICANT, processRoleRepository);
    }

    private boolean isLeadForInvite(final ApplicationInviteResource invite, final UserResource user) {
        return checkProcessRole(user, invite.getApplication(), UserRoleType.LEADAPPLICANT, processRoleRepository);
    }
}
