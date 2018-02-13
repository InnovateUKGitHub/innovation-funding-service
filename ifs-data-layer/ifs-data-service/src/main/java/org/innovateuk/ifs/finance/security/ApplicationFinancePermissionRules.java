package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.checkProcessRole;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInnovationLead;

/**
 * ApplicationFinancePermissionRules are applying rules for seeing / updating the application
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
        return isAConsortiumMemberOnApplication(applicationFinanceResource, user);
    }

    @PermissionRule(value = "READ", description = "An assessor can see the application finances for organisations in the applications they assess")
    public boolean assessorCanSeeTheApplicationFinanceForOrganisationsInApplicationsTheyAssess(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        final boolean isAssessor = checkProcessRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), ASSESSOR, roleRepository, processRoleRepository);
        return isAssessor;
    }

    @PermissionRule(value = "READ", description = "An internal user can see application finances for organisations")
    public boolean internalUserCanSeeApplicationFinancesForOrganisations(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "ADD_COST", description = "The consortium can add a cost to the application finances of their own organisation or if lead applicant")
    public boolean consortiumCanAddACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationOrIsLeadApplicant(applicationFinanceResource, user);
    }

    @PermissionRule(value = "ADD_COST", description = "The CSS user can add a cost to the application finances")
    public boolean supportCanAddACostToApplicationFinance(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isSupport(user);
    }

    @PermissionRule(value = "ADD_COST", description = "Innovation lead users can add a cost to the application finances")
    public boolean innovationLeadCanAddACostToApplicationFinance(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isInnovationLead(user);
    }

    @PermissionRule(value = "UPDATE_COST", description = "The consortium can update a cost to the application finances of their own organisation or if lead applicant")
    public boolean consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationOrIsLeadApplicant(applicationFinanceResource, user);
    }

    @PermissionRule(value = "READ_FILE_ENTRY", description = "The consortium can get file entry resource for finance section of a collaborator")
    public boolean consortiumMemberCanGetFileEntryResourceByFinanceIdOfACollaborator(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplication(applicationFinanceResource.getApplication(), user);
    }

    @PermissionRule(value = "READ_FILE_ENTRY", description = "An internal user can get file entry resource for finance section of a collaborator")
    public boolean internalUserCanGetFileEntryResourceForFinanceIdOfACollaborator(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "CREATE_FILE_ENTRY", description = "A consortium member can create a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationAndOrganisation(applicationFinanceResource, user);
    }


    @PermissionRule(value = "UPDATE_FILE_ENTRY", description = "A consortium member can update a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationAndOrganisation(applicationFinanceResource, user);
    }

    @PermissionRule(value = "DELETE_FILE_ENTRY", description = "A consortium member can delete a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isAConsortiumMemberOnApplicationAndOrganisation(applicationFinanceResource, user);
    }

    private boolean isAConsortiumMemberOnApplicationAndOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), LEADAPPLICANT, roleRepository, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), COLLABORATOR, roleRepository, processRoleRepository);
        return isLeadApplicant || isCollaborator;
    }

    private boolean isAConsortiumMemberOnApplication(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationFinanceResource.getApplication(), LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationFinanceResource.getApplication(), COLLABORATOR, processRoleRepository);

        return isLeadApplicant || isCollaborator;
    }

    private boolean isAConsortiumMemberOnApplication(final Long applicationId, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationId, LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationId, COLLABORATOR, processRoleRepository);

        return isLeadApplicant || isCollaborator;
    }

    private boolean isAConsortiumMemberOnApplicationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        final boolean isLeadApplicant = checkProcessRole(user, applicationFinanceResource.getApplication(), LEADAPPLICANT, processRoleRepository);
        final boolean isCollaborator = checkProcessRole(user, applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), COLLABORATOR, roleRepository, processRoleRepository);

        return isLeadApplicant || isCollaborator;
    }
}
