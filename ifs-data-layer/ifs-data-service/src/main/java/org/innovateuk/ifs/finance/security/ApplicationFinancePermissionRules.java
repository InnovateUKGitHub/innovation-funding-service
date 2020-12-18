package org.innovateuk.ifs.finance.security;

import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.security.ApplicationSecurityHelper;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ApplicationFinancePermissionRules are applying rules for seeing / updating the application
 */
@Component
@PermissionRules
public class ApplicationFinancePermissionRules extends BasePermissionRules {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ApplicationSecurityHelper applicationSecurityHelper;

    @PermissionRule(value = "READ", description = "If the user can view the application they can view the finances.")
    public boolean canViewFinancesIfCanViewApplication(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return applicationSecurityHelper.canViewApplication(applicationFinanceResource.getApplication(), user);
    }

    @PermissionRule(value = "ADD_COST", description = "The consortium can add a cost to the application finances of their own organisation or if lead applicant")
    public boolean consortiumCanAddACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isMemberOfProjectTeamForOrganisation(applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), user);
    }

    @PermissionRule(value = "UPDATE_COST", description = "The consortium can update a cost to the application finances of their own organisation or if lead applicant")
    public boolean consortiumCanUpdateACostToApplicationFinanceForTheirOrganisationOrIsLeadApplicant(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isMemberOfProjectTeamForOrganisation(applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), user);
    }

    @PermissionRule(value = "READ_FILE_ENTRY", description = "If then user can view the application they can view the finance file.")
    public boolean canViewApplication(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return applicationSecurityHelper.canViewApplication(applicationFinanceResource.getApplication(), user);
    }

    @PermissionRule(value = "CREATE_FILE_ENTRY", description = "A consortium member can create a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanCreateAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isMemberOfProjectTeamForOrganisation(applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), user);
    }

    @PermissionRule(value = "UPDATE_FILE_ENTRY", description = "A consortium member can update a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanUpdateAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isMemberOfProjectTeamForOrganisation(applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), user);
    }

    @PermissionRule(value = "DELETE_FILE_ENTRY", description = "A consortium member can delete a file entry for the finance section for their organisation")
    public boolean consortiumMemberCanDeleteAFileForTheApplicationFinanceForTheirOrganisation(final ApplicationFinanceResource applicationFinanceResource, final UserResource user) {
        return isMemberOfProjectTeamForOrganisation(applicationFinanceResource.getApplication(), applicationFinanceResource.getOrganisation(), user);
    }
}
