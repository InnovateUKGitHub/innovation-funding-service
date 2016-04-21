package com.worth.ifs.invite.security;

import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.domain.InviteOrganisation;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * TODO
 */
@Component
@PermissionRules
public class InviteOrganisationPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;
    @Autowired
    private RoleRepository roleRepository;

    @PermissionRule(value = "SEND", description = "consortium can invite to the application")
    public boolean leadApplicantCanInviteToTheApplication(final Invite invite, final UserResource user) {
        final long applicationId = invite.getApplication().getId();
        return SecurityRuleUtil.checkRole(user, invite.getApplication().getId(), UserRoleType.LEADAPPLICANT, processRoleRepository);
    }

    @PermissionRule(value = "SEND", description = "consortium can invite to the application")
    public boolean collaboratorCanInviteToApplicantForTheirOrganisation(final Invite invite, final UserResource user) {
        final long applicationId = invite.getApplication().getId();
        final InviteOrganisation inviteOrganisation = invite.getInviteOrganisation(); // Not an actual organisation.
        if (inviteOrganisation != null && inviteOrganisation.getOrganisation() != null) {
            long organisationId = inviteOrganisation.getOrganisation().getId();
            final boolean isCollaborator = SecurityRuleUtil.checkRole(user, applicationId, organisationId, UserRoleType.LEADAPPLICANT, roleRepository, processRoleRepository);
            return isCollaborator;
        }
        return false;
    }


//
//    @PermissionRule(value = "DELETE", description = "The consortium can update the cost for their application and organisation")
//    public boolean consortiumCanDeleteACostForTheirApplicationAndOrganisation(final Cost cost, final UserResource user) {
//        return isCollaborator(cost, user);
//    }
//
//    private boolean isCollaborator(final Cost cost, final UserResource user) {
//        final ApplicationFinance applicationFinance = cost.getApplicationFinance();
//        final Long applicationId = applicationFinance.getApplication().getId();
//        final Long organisationId = applicationFinance.getOrganisation().getId();
//        final boolean isLead = checkRole(user, applicationId, organisationId, LEADAPPLICANT, roleRepository, processRoleRepository);
//        final boolean isCollaborator = checkRole(user, applicationId, organisationId, COLLABORATOR, roleRepository, processRoleRepository);
//        return isLead || isCollaborator;
//    }

}
