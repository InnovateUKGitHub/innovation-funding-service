package com.worth.ifs.finance.security;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.security.SecurityRuleUtil;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.RoleRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.worth.ifs.security.SecurityRuleUtil.checkRole;
import static com.worth.ifs.user.domain.UserRoleType.*;

/**
 *  ApplicationFinancePermissionRules are applying rules for seeing / updating the application
 */

@Component
@PermissionRules
public class ApplicationFinancePermissionRules {

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PermissionRule(value = "READ", description = "The consortium can see the application finances of their own organisation")
    public boolean consortiumCanSeeTheApplicationFinancesForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMember(applicationFinanceResource, user);
    }

    @PermissionRule(value = "READ", description = "An assessor can see the application finances for organisations in the applications they assess")
    public boolean assessorCanSeeTheApplicationFinanceForOrganisationsInApplicationsTheyAssess(final ApplicationFinanceResource applicationFinanceResource, final UserResource user){
        final boolean isAssessor = checkRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), ASSESSOR, roleRepository, processRoleRepository);
        return isAssessor;
    }

    @PermissionRule(value = "READ", description = "A comp admin can see application finances for organisations")
    public boolean compAdminCanSeeApplicationFinancesForOrganisations(final ApplicationFinanceResource applicationFinanceResource, final UserResource user){
        return SecurityRuleUtil.isCompAdmin(user);
    }

    @PermissionRule(value = "ADD_COST", description = "The consortium can add a cost to the application finances of their own organisation")
    public boolean consortiumCanAddACostToApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user){
        return isAConsortiumMember(applicationFinanceResource, user);
    }

    @PermissionRule(value = "UPDATE_COST", description = "The consortium can update a cost to the application finances of their own organisation")
    public boolean consortiumCanUpdateACostToApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user){
        return isAConsortiumMember(applicationFinanceResource, user);
    }

    private final boolean isAConsortiumMember(final ApplicationFinanceResource applicationFinanceResource, final UserResource user){
        final boolean isLeadApplicant = checkRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), LEADAPPLICANT, roleRepository, processRoleRepository);
        final boolean isCollaborator = checkRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), COLLABORATOR, roleRepository, processRoleRepository);
        return isLeadApplicant || isCollaborator;
    }

}
