package com.worth.ifs.invite.security;

import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;



 /**
 * Permission rules for {@link InviteOrganisationResource} permissioning
 */
@Component
@PermissionRules
public class InviteOrganisationPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    @PermissionRule(value = "SEND", description = "consortium can send an organisation invite to the application")
    public boolean leadApplicantCanInviteAnOrganisationToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return isLeadApplicantForInviteOrganiation(inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "a lead applicant can view an organisation invite to the application")
    public boolean leadApplicantCanViewOrganisationInviteToTheApplication(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        return isLeadApplicantForInviteOrganiation(inviteOrganisation, user);
    }

    private final boolean isLeadApplicantForInviteOrganiation(final InviteOrganisationResource inviteOrganisation, final UserResource user) {
        final List<InviteResource> invites = inviteOrganisation.getInviteResources();
        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false;
        }

        for (final InviteResource invite : invites) {
            final long applicationId = invite.getApplication();
            final boolean isLead = checkRole(user, applicationId, LEADAPPLICANT, processRoleRepository);
            if (!isLead) {
                return false;
            }
        }
        return true;
    }
}
