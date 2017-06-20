package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;

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

    @Autowired
    private ApplicationRepository applicationRepository;

    @PermissionRule(value = "SEND", description = "lead applicant can invite to the application")
    public boolean leadApplicantCanInviteToTheApplication(final ApplicationInvite invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "SEND", description = "collaborator can invite to the application for thier organisation")
    public boolean collaboratorCanInviteToApplicationForTheirOrganisation(final ApplicationInvite invite, final UserResource user) {
        return applicationIsEditable(invite.getTarget()) && isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "SAVE", description = "lead applicant can save invite to the application")
    public boolean leadApplicantCanSaveInviteToTheApplication(final ApplicationInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "SAVE", description = "collaborator can save invite to the application for thier organisation")
    public boolean collaboratorCanSaveInviteToApplicationForTheirOrganisation(final ApplicationInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "READ", description = "collaborator can view an invite to the application on for their organisation")
    public boolean collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(final ApplicationInvite invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "READ", description = "lead applicant can view an invite to the application")
    public boolean leadApplicantReadInviteToTheApplication(final ApplicationInvite invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "DELETE", description = "lead applicant can delete an invite from the application and applicant can not delete his own invite from the application")
    public boolean leadApplicantAndNotDeleteOwnInviteToTheApplication(final ApplicationInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isNotOwnInvite(invite, user) && isLeadForInvite(invite, user);
    }

    private boolean isNotOwnInvite(final ApplicationInviteResource invite, final UserResource user) {
        if (invite.getUser() == null) {
            return true;
        }
        return !invite.getUser().equals(user.getId());
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
        return checkProcessRole(user, invite.getTarget().getId(), UserRoleType.LEADAPPLICANT, processRoleRepository);
    }

    private boolean isLeadForInvite(final ApplicationInviteResource invite, final UserResource user) {
        return checkProcessRole(user, invite.getApplication(), UserRoleType.LEADAPPLICANT, processRoleRepository);
    }

    private boolean applicationIsEditableById(final Long applicationId) {
        return applicationIsEditable(applicationRepository.findOne(applicationId));
    }

    private boolean applicationIsEditable(final Application application) {
        ApplicationProcess state = application.getApplicationProcess();
        return state.isInState(ApplicationState.CREATED) || state.isInState(ApplicationState.OPEN);
    }
}
