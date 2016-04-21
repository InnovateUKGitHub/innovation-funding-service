package com.worth.ifs.invite.security;

import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.invite.repository.InviteRepository;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;


/**
 * TODO
 */
@Component
@PermissionRules
public class InvitePermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private InviteRepository inviteRepository;

    @PermissionRule(value = "SEND", description = "lead applicant can invite to the application")
    public boolean leadApplicantCanInviteToTheApplication(final Invite invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "SEND", description = "collaborator can invite to the application for thier organisation")
    public boolean collaboratorCanInviteToApplicantForTheirOrganisation(final Invite invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }


    @PermissionRule(value = "SAVE", description = "lead applicant can save invite to the application")
    public boolean leadApplicantCanSaveInviteToTheApplication(final InviteResource invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "SAVE", description = "collaborator can save invite to the application for thier organisation")
    public boolean collaboratorCanSaveInviteToApplicantForTheirOrganisation(final InviteResource invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "READ", description = "collaborator can view an invite to the application on for their organisation")
    public boolean collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(final Invite invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "READ", description = "lead applicant can view an invite to the application")
    public boolean leadApplicantReadInviteToTheApplication(final Invite invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    private boolean isCollaboratorOnInvite(final Invite invite, final UserResource user) {
        final long applicationId = invite.getApplication().getId();
        final InviteOrganisation inviteOrganisation = invite.getInviteOrganisation(); // Not an actual organisation.
        if (inviteOrganisation != null && inviteOrganisation.getOrganisation() != null) {
            long organisationId = inviteOrganisation.getOrganisation().getId();
            final boolean isCollaborator = checkRole(user, applicationId, organisationId, UserRoleType.LEADAPPLICANT, roleRepository, processRoleRepository);
            return isCollaborator;
        }
        return false;
    }

    private boolean isCollaboratorOnInvite(final InviteResource invite, final UserResource user) {
        return isCollaboratorOnInvite(inviteRepository.findOne(invite.getId()), user);
    }

    private boolean isLeadForInvite(final Invite invite, final UserResource user) {
        final long applicationId = invite.getApplication().getId();
        return checkRole(user, invite.getApplication().getId(), UserRoleType.LEADAPPLICANT, processRoleRepository);
    }

    private boolean isLeadForInvite(final InviteResource invite, final UserResource user) {
        return isLeadForInvite(inviteRepository.findOne(invite.getId()), user);
    }
}
