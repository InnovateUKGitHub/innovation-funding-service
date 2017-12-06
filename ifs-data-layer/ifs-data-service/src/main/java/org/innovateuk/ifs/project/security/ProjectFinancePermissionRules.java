package org.innovateuk.ifs.project.security;


import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Defines the permissions for interaction with project finances.
 */
@PermissionRules
public class ProjectFinancePermissionRules extends BasePermissionRules {
    @PermissionRule(
            value = "VIEW_VIABILITY",
            description = "Project Finance Users can view Viability")
    public boolean projectFinanceUserCanViewViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "SAVE_VIABILITY",
            description = "Project Finance Users can save Viability")
    public boolean projectFinanceUserCanSaveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_ELIGIBILITY",
            description = "Project Finance Users can view Eligibility")
    public boolean projectFinanceUserCanViewEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_ELIGIBILITY",
            description = "Project partners can view Eligibility")
    public boolean projectPartnersCanViewEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "SAVE_ELIGIBILITY",
            description = "Project Finance Users can save Eligibility")
    public boolean projectFinanceUserCanSaveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_CREDIT_REPORT",
            description = "Project Finance Users can view the Credit Report flag")
    public boolean projectFinanceUserCanViewCreditReport(ProjectCompositeId projectCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "SAVE_CREDIT_REPORT",
            description = "Project Finance Users can save Credit Report flag")
    public boolean projectFinanceUserCanSaveCreditReport(ProjectCompositeId projectCompositeId, UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "READ_PROJECT_FINANCE", description = "Project partners can see the project finances of their own project")
    public boolean partnersCanSeeTheProjectFinancesForTheirOrganisation(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isPartner(projectFinanceResource.getProject(), user.getId());
    }

    @PermissionRule(value = "READ_PROJECT_FINANCE", description = "An internal user can see project finances for organisations")
    public boolean internalUserCanSeeProjectFinancesForOrganisations(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "ADD_EMPTY_PROJECT_COST", description = "The consortium can add a cost to the application finances of their own organisation or if lead applicant")
    public boolean partnersCanAddEmptyRowWhenReadingProjectCosts(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isPartner(projectFinanceResource.getProject(), user.getId());
    }

    @PermissionRule(value = "ADD_EMPTY_PROJECT_COST", description = "The consortium can add a cost to the application finances of their own organisation or if lead applicant")
    public boolean internalUsersCanAddEmptyRowWhenReadingProjectCosts(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_ELIGIBILITY", description = "Project partners can see the project finances eligibility of their own project")
    public boolean partnersCanSeeTheProjectFinancesForTheirOrganisation(final FinanceCheckEligibilityResource financeCheckEligibilityResource, final UserResource user) {
        return isPartner(financeCheckEligibilityResource.getProjectId(), user.getId());
    }

    @PermissionRule(value = "READ_ELIGIBILITY", description = "Internal users can see the project finances eligibility of their own project")
    public boolean internalUsersCanSeeTheProjectFinancesForTheirOrganisation(final FinanceCheckEligibilityResource financeCheckEligibilityResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value="READ_OVERVIEW", description = "Internal users can see the project finance overview")
    public boolean internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(final ProjectCompositeId projectCompositeId, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value="READ_OVERVIEW", description = "Project partners can see their project finance overview")
    public boolean partnersCanSeeTheProjectFinanceOverviewsForTheirProject(final ProjectCompositeId projectCompositeId, final UserResource user) {
        return isPartner(projectCompositeId.id(), user.getId());
    }

}