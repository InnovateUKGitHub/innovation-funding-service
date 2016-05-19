package com.worth.ifs.invite.security;

import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;


/**
 * Permission rules for {@link InviteOrganisationResource} permissioning
 */
@Component
@PermissionRules
public class InviteOrganisationPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "SEND", description = "consortium can send an organisation invite to the application")
    public boolean leadApplicantCanInviteAnOrganisationToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return isApplicantTypeForInviteOrganisation(LEADAPPLICANT, inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "a lead applicant can view an organisation invite to the application")
    public boolean leadApplicantCanViewOrganisationInviteToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return isApplicantTypeForInviteOrganisation(LEADAPPLICANT, inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "a collaborator can view an organisation invite to the application")
    public boolean collaboratorCanViewOrganisationInviteToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return isApplicantTypeForInviteOrganisation(COLLABORATOR, inviteOrganisation, user);
    }

    private final boolean isApplicantTypeForInviteOrganisation(final UserRoleType userRoleType, final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        final List<InviteResource> invites = inviteOrganisation.getInviteResources();

        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false;
        }

        return invites.stream()
                .allMatch(inviteResource -> isApplicantTypeOnApplication(userRoleType, user, inviteResource.getApplication()));
    }

    private boolean isApplicantTypeOnApplication(final UserRoleType userRoleType, final UserResource user, final Long applicationId) {
        return checkRole(user, applicationId, userRoleType, processRoleRepository);
    }
}
