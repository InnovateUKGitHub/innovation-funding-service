package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;


/**
 * Permission rules for {@link InviteOrganisationResource} permissioning
 */
@PermissionRules
public class InviteOrganisationPermissionRules {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @PermissionRule(value = "SEND", description = "lead applicant can send an organisation invite for the application")
    public boolean leadApplicantCanInviteAnOrganisationToTheApplication(InviteOrganisationResource inviteOrganisation, UserResource user) {
        return isLeadApplicantForAllApplications(inviteOrganisation, user);
    }

    @PermissionRule(value = "READ", description = "a consortium member can view the invites of their own organisation or if lead applicant")
    public boolean consortiumCanViewAnInviteOrganisation(InviteOrganisationResource inviteOrganisation, UserResource user) {
        if (inviteOrganisation.getOrganisation() == null) {
            // Organisation is not confirmed yet so only the lead can view it to perform an update
            return isLeadApplicantForAllApplications(inviteOrganisation, user);
        }
        return isAConsortiumMemberOrIsLeadApplicantForAllApplications(inviteOrganisation, user);
    }

    @PermissionRule(value = "SAVE", description = "lead applicant can save an organisation invite for the application")
    public boolean leadApplicantCanSaveInviteAnOrganisationToTheApplication(InviteOrganisationResource inviteOrganisation, UserResource user) {
        return isLeadApplicantForAllApplications(inviteOrganisation, user);
    }

    private boolean isLeadApplicantForAllApplications(InviteOrganisationResource inviteOrganisation, UserResource user) {
        List<ApplicationInviteResource> invites = inviteOrganisation.getInviteResources();
        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false;
        }
        return invites.stream().allMatch(applicationInviteResource -> isLeadApplicant(applicationInviteResource, user));
    }

    private boolean isAConsortiumMemberOrIsLeadApplicantForAllApplications(InviteOrganisationResource inviteOrganisation, UserResource user) {
        List<ApplicationInviteResource> invites = inviteOrganisation.getInviteResources();
        if (invites == null || invites.isEmpty()) {
            return false; // Unable to check the application so default to false;
        }
        return invites.stream().allMatch(applicationInviteResource -> isAConsortiumMemberOrIsLeadApplicant(
                inviteOrganisation, applicationInviteResource, user));
    }

    private boolean isAConsortiumMemberOrIsLeadApplicant(InviteOrganisationResource inviteOrganisationResource, ApplicationInviteResource applicationInviteResource, UserResource userResource) {
        return isLeadApplicant(applicationInviteResource, userResource) || isAConsortiumMember(
                inviteOrganisationResource, applicationInviteResource, userResource);
    }

    private boolean isAConsortiumMember(InviteOrganisationResource inviteOrganisationResource, ApplicationInviteResource applicationInviteResource, UserResource userResource) {
        return checkProcessRole(userResource, applicationInviteResource.getApplication(), inviteOrganisationResource.getOrganisation(), COLLABORATOR, roleRepository, processRoleRepository);
    }

    private boolean isLeadApplicant(ApplicationInviteResource applicationInviteResource, UserResource userResource) {
        return checkProcessRole(userResource, applicationInviteResource.getApplication(), LEADAPPLICANT, processRoleRepository);
    }
}
