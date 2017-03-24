package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.security.SecurityRuleUtil;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isSystemRegistrationUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;


/**
 * Permission rules for {@link InviteOrganisationResource} permissioning
 */
@PermissionRules
public class InviteOrganisationPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "SEND", description = "lead applicant can send an organisation invite for the application")
    public boolean leadApplicantCanInviteAnOrganisationToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return hasRoleForAllApplicationsInOrganisationInvite(LEADAPPLICANT, inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "a lead applicant can view an organisation invite to the application")
    public boolean leadApplicantCanViewOrganisationInviteToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return hasRoleForAllApplicationsInOrganisationInvite(LEADAPPLICANT, inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "a collaborator can view an organisation invite to the application")
    public boolean collaboratorCanViewOrganisationInviteToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return hasRoleForAllApplicationsInOrganisationInvite(COLLABORATOR, inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "System Registration user can view an organisation invite to the application")
    public boolean systemRegistrarCanViewOrganisationInviteToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "SAVE", description = "lead applicant can save an organisation invite for the application")
    public boolean leadApplicantCanSaveInviteAnOrganisationToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return hasRoleForAllApplicationsInOrganisationInvite(LEADAPPLICANT, inviteOrganisation, user);
    }

    private final boolean hasRoleForAllApplicationsInOrganisationInvite(final UserRoleType userRoleType, final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        final List<ApplicationInviteResource> invites = inviteOrganisation.getInviteResources();
        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false;
        }
        return invites.stream()
                .allMatch(inviteResource -> hasRoleOnApplication(userRoleType, user, inviteResource.getApplication()));
    }

    private boolean hasRoleOnApplication(final UserRoleType userRoleType, final UserResource user, final Long applicationId) {
        return SecurityRuleUtil.checkProcessRole(user, applicationId, userRoleType, processRoleRepository);
    }
}
