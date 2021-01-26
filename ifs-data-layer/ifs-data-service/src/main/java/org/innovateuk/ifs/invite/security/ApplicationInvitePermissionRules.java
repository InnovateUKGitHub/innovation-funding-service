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
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.*;

/**
 * Permission rules for {@link ApplicationInvite} and {@link ApplicationInviteResource} for permissioning
 */
@Component
@PermissionRules
public class ApplicationInvitePermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

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

    @PermissionRule(value = "SAVE", description = "collaborator can save invite to the application for their organisation")
    public boolean collaboratorCanSaveInviteToApplicationForTheirOrganisation(final ApplicationInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "SAVE", description = "lead applicant can save invite kta to the application")
    public boolean leadApplicantCanSaveKtaInviteToTheApplication(final ApplicationKtaInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "DELETE", description = "lead applicant can remove kta invite to the application")
    public boolean leadApplicantCanRemoveKtaInviteToTheApplication(final ApplicationKtaInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "ACCEPT", description = "Kta user can accept the invite to the application")
    public boolean ktaCanAcceptAnInviteAddressedToThem(final ApplicationKtaInviteResource invite, final UserResource user) {
        return invite.getEmail().equals(user.getEmail());
    }

    @PermissionRule(value = "READ", description = "collaborator can view an invite to the application on for their organisation")
    public boolean collaboratorCanReadInviteForTheirApplicationForTheirOrganisation(final ApplicationInvite invite, final UserResource user) {
        return isCollaboratorOnInvite(invite, user);
    }

    @PermissionRule(value = "READ", description = "lead applicant can view an invite to the application")
    public boolean leadApplicantReadInviteToTheApplication(final ApplicationInvite invite, final UserResource user) {
        return isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "DELETE", description = "lead applicant can delete as long as its not their own.")
    public boolean leadCanDeleteNotOwnInvite(final ApplicationInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isNotOwnInvite(invite, user) && isLeadForInvite(invite, user);
    }

    @PermissionRule(value = "DELETE", description = "collaborator can delete invite as long as its not their own.")
    public boolean collaboratorCanDeleteNotOwnInvite(final ApplicationInviteResource invite, final UserResource user) {
        return applicationIsEditableById(invite.getApplication()) && isNotOwnInvite(invite, user) && isCollaboratorOnInvite(invite, user);
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
            final boolean isCollaborator = checkProcessRole(user, applicationId, organisationId, COLLABORATOR, processRoleRepository);
            return isCollaborator;
        }
        return false;
    }

    private boolean isCollaboratorOnInvite(final ApplicationInviteResource invite, final UserResource user) {
        if (invite.getApplication() != null && invite.getInviteOrganisation() != null) {
            final InviteOrganisation inviteOrganisation = inviteOrganisationRepository.findById(invite.getInviteOrganisation()).orElse(null);
            if (inviteOrganisation != null && inviteOrganisation.getOrganisation() != null) {
                return checkProcessRole(user, invite.getApplication(), inviteOrganisation.getOrganisation().getId(), COLLABORATOR, processRoleRepository);
            }
        }
        return false;
    }

    private boolean isLeadForInvite(final ApplicationInvite invite, final UserResource user) {
        return checkProcessRole(user, invite.getTarget().getId(), LEADAPPLICANT, processRoleRepository);
    }

    private boolean isLeadForInvite(final ApplicationInviteResource invite, final UserResource user) {
        return checkProcessRole(user, invite.getApplication(), LEADAPPLICANT, processRoleRepository);
    }

    private boolean isLeadForInvite(final ApplicationKtaInviteResource invite, final UserResource user) {
        return checkProcessRole(user, invite.getApplication(), LEADAPPLICANT, processRoleRepository);
    }

    private boolean applicationIsEditableById(final Long applicationId) {
        return applicationIsEditable(applicationRepository.findById(applicationId).orElse(null));
    }

    private boolean applicationIsEditable(final Application application) {
        ApplicationProcess state = application.getApplicationProcess();
        return state.isInState(ApplicationState.CREATED) || state.isInState(ApplicationState.OPENED);
    }
}
