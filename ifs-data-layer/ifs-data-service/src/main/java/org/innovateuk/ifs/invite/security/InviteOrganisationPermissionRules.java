package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

import static org.innovateuk.ifs.competition.resource.CollaborationLevel.SINGLE;
import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSystemRegistrationUser;

/**
 * Permission rules for {@link InviteOrganisationResource} permissioning
 */
@PermissionRules
public class InviteOrganisationPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "SEND", description = "lead applicant can send an organisation invite for the application")
    public boolean leadApplicantCanInviteAnOrganisationToTheApplication(InviteOrganisationResource inviteOrganisation, UserResource user) {
        return isLeadApplicantForAllApplications(inviteOrganisation, user);
    }

    @PermissionRule(value = "CREATE_APPLICATION_INVITES", description = "Lead applicant or collaborator can create invites for the specified application if the application is still editable")
    public boolean leadApplicantCanCreateApplicationInvitesIfApplicationEditable(InviteOrganisationResource inviteOrganisation, UserResource user) {
        // This would never happen, unless someone calls REST directly. The Web layer ensures that at least one invite is present.
        if (inviteOrganisation.getInviteResources().isEmpty()) {
            throw new ForbiddenActionException("Missing Invite Resource");
        }

        if (!allInviteApplicationIdsMatch(inviteOrganisation)) {
            throw new ForbiddenActionException("Not all invite application ids match");
        }

        if (isApplicationCollaboratorOrIsLeadApplicant(inviteOrganisation, user)) {
            // Get the application id from the first invite, as application id is same for all invites.
            long applicationId = inviteOrganisation.getInviteResources().get(0).getApplication();

            Application application = getApplication(applicationId);

            return checkCollaborationLevel(inviteOrganisation, applicationId) && applicationIsEditable(application);
        }
        return false;
    }

    private boolean checkCollaborationLevel(InviteOrganisationResource inviteOrganisationResource, long applicationId) {
        Application application = getApplication(applicationId);

        boolean newCollaborationInvite = isNewCollaborationInvite(inviteOrganisationResource);
        boolean collaborationAllowed = isCollaborationAllowed(application);
        return !newCollaborationInvite || collaborationAllowed;
    }

    private boolean isNewCollaborationInvite(InviteOrganisationResource inviteOrganisationResource) {
        return inviteOrganisationResource.getOrganisation() == null;
    }

    private boolean isCollaborationAllowed(Application application) {
        CollaborationLevel collaborationLevel = application.getCompetition().getCollaborationLevel();
        return SINGLE != collaborationLevel;
    }

    private boolean applicationIsEditable(final Application application) {
        ApplicationProcess state = application.getApplicationProcess();
        return state.isInState(ApplicationState.CREATED) || state.isInState(ApplicationState.OPENED);
    }

    @PermissionRule(value = "READ", description = "a consortium member and the lead applicant can view the invites of all organisations")
    public boolean consortiumCanViewAnyInviteOrganisation(InviteOrganisationResource inviteOrganisation, UserResource user) {
        return isApplicationCollaboratorOrIsLeadApplicant(inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "a support user can view the invites of all organisations")
    public boolean supportCanViewAnyInviteOrganisation(InviteOrganisationResource inviteOrganisation, UserResource user) {
        return user.hasRole(SUPPORT);
    }

    @PermissionRule(value = "READ_FOR_UPDATE", description = "a consortium member can view the invites of their own organisation or if lead applicant")
    public boolean consortiumCanViewAnInviteOrganisation(InviteOrganisationResource inviteOrganisation, UserResource user) {
        if (inviteOrganisation.getOrganisation() == null) {
            // Organisation is not confirmed yet so only the lead can view it. There are no other users that are collaborators for this organisation
            return isLeadApplicantForAllApplications(inviteOrganisation, user);
        }
        return isApplicationCollaboratorForOrganisationOrIsLeadApplicant(inviteOrganisation, user);
    }

    @PermissionRule(value = "READ_FOR_UPDATE", description = "System Registration user can view or update an organisation invite to the application")
    public boolean systemRegistrarCanViewOrganisationInviteToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "SAVE", description = "lead applicant can save an organisation invite for the application")
    public boolean leadApplicantCanSaveInviteAnOrganisationToTheApplication(InviteOrganisationResource inviteOrganisation, UserResource user) {
        return isLeadApplicantForAllApplications(inviteOrganisation, user);
    }

    private boolean isLeadApplicantForAllApplications(InviteOrganisationResource inviteOrganisation, UserResource user) {
        List<ApplicationInviteResource> invites = inviteOrganisation.getInviteResources();
        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false
        }
        return invites.stream().allMatch(applicationInviteResource -> isLeadApplicant(applicationInviteResource, user));
    }

    private boolean isApplicationCollaboratorOrIsLeadApplicant(InviteOrganisationResource inviteOrganisationResource, UserResource userResource) {
        List<ApplicationInviteResource> invites = inviteOrganisationResource.getInviteResources();
        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false
        }
        return invites.stream().allMatch(applicationInviteResource ->
                isLeadApplicant(applicationInviteResource, userResource) ||
                        isApplicationCollaborator(applicationInviteResource, userResource)
        );
    }

    private boolean isApplicationCollaboratorForOrganisationOrIsLeadApplicant(InviteOrganisationResource inviteOrganisationResource, UserResource userResource) {
        List<ApplicationInviteResource> invites = inviteOrganisationResource.getInviteResources();
        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false
        }
        return invites.stream().allMatch(applicationInviteResource ->
                isLeadApplicant(applicationInviteResource, userResource) ||
                        isApplicationCollaboratorForOrganisation(inviteOrganisationResource, applicationInviteResource, userResource)
        );
    }

    private boolean isApplicationCollaborator(ApplicationInviteResource applicationInviteResource, UserResource userResource) {
        return checkProcessRole(userResource, applicationInviteResource.getApplication(), ProcessRoleType.COLLABORATOR, processRoleRepository);
    }

    private boolean isApplicationCollaboratorForOrganisation(InviteOrganisationResource inviteOrganisationResource, ApplicationInviteResource applicationInviteResource, UserResource userResource) {
        return checkProcessRole(userResource, applicationInviteResource.getApplication(), inviteOrganisationResource.getOrganisation(), ProcessRoleType.COLLABORATOR, processRoleRepository);
    }

    private boolean isLeadApplicant(ApplicationInviteResource applicationInviteResource, UserResource userResource) {
        return checkProcessRole(userResource, applicationInviteResource.getApplication(), ProcessRoleType.LEADAPPLICANT, processRoleRepository);
    }

    private boolean allInviteApplicationIdsMatch(InviteOrganisationResource inviteOrganisation) {
        return inviteOrganisation.getInviteResources().stream().allMatch(invite -> invite.getApplication().equals(inviteOrganisation.getInviteResources().get(0).getApplication()));
    }

    private Application getApplication(long applicationId){
        return applicationRepository.findById(applicationId).get();
    }
}
