package com.worth.ifs.application.security;

import com.worth.ifs.application.resource.ApplicationOrganisationResourceId;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.security.SecurityRuleUtil.isCompAdmin;
import static com.worth.ifs.user.resource.UserRoleType.COLLABORATOR;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;

@Component
@PermissionRules
public class SectionPermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PermissionRule(value = "READ", description = "everyone can read sections")
    public boolean userCanReadSection(SectionResource section, UserResource user){
        return true;
    }

    @PermissionRule(value = "UPDATE", description ="no one can update sections yet")
    public boolean userCanUpdateSection(SectionResource section, UserResource user){
        return false;
    }

    @PermissionRule(value = "READ_SECTION_COMPLETE", description = "a lead applicant can see if a section is complete for an organisation")
    public boolean aLeadApplicantCanSeeIfASectionIsCompleteForAnOrganisation(final ApplicationOrganisationResourceId applicationOrganisationResourceId, final UserResource user){
        final long applicationId = applicationOrganisationResourceId.getApplicationId();
        return checkRole(user, applicationId, LEADAPPLICANT, processRoleRepository);
    }

    @PermissionRule(value = "READ_SECTION_COMPLETE", description = "a comp admin can see if a section is complete for an organisation")
    public boolean aCompAdminCanSeeIfASectionIsCompleteForAnOrganisation(final ApplicationOrganisationResourceId applicationOrganisationResourceId, final UserResource user){
        return isCompAdmin(user);
    }

    @PermissionRule(value = "READ_SECTION_COMPLETE", description = "an applicant can see if a section is complete for their organisation")
    public boolean anApplicantCanSeeIfASectionIsCompleteForTheirOrganisation(final ApplicationOrganisationResourceId applicationOrganisationResourceId, final UserResource user){
        final long applicationId = applicationOrganisationResourceId.getApplicationId();
        final long organisationId = applicationOrganisationResourceId.getOrganisationId();
        return checkRole(user, applicationId, organisationId, COLLABORATOR, roleRepository, processRoleRepository);
    }
}
