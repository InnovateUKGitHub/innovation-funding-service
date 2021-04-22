package org.innovateuk.ifs.project.financechecks.security;


import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.user.resource.Role.EXTERNAL_FINANCE;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
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
        return hasProjectFinanceAuthority(user);
    }

    @PermissionRule(
            value = "VIEW_VIABILITY",
            description = "Project Finance Users can view Viability")
    public boolean competitionFinanceUserCanViewViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "SAVE_VIABILITY",
            description = "Project Finance Users can save Viability")
    public boolean projectFinanceUserCanSaveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return hasProjectFinanceAuthority(user) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "SAVE_VIABILITY",
            description = "Project Finance Users can save Viability")
    public boolean competitionFinanceUserCanSaveViability(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectOrganisationCompositeId.getProjectId(), user.getId()) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "VIEW_ELIGIBILITY",
            description = "Project Finance Users can view Eligibility")
    public boolean projectFinanceUserCanViewEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user);
    }

    @PermissionRule(
            value = "VIEW_ELIGIBILITY",
            description = "Competition Finance Users can view Eligibility")
    public boolean competitionFinanceUserCanViewEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectOrganisationCompositeId.getProjectId(), user.getId());
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
        return hasProjectFinanceAuthority(user) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "SAVE_ELIGIBILITY",
            description = "Competition Finance Users can save Eligibility")
    public boolean competitionFinanceUserCanSaveEligibility(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectOrganisationCompositeId.getProjectId(), user.getId()) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "SAVE_FUNDING_RULES",
            description = "Project Finance Users can save Funding Rules")
    public boolean projectFinanceUserCanSaveFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "SAVE_FUNDING_RULES",
            description = "Competition Finance Users can save Funding Rules")
    public boolean competitionFinanceUserCanSaveFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectOrganisationCompositeId.getProjectId(), user.getId()) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "SAVE_MILESTONE_CHECK",
            description = "Project Finance Users can save Milestone Check")
    public boolean projectFinanceUserCanSaveMilestoneCheck(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "RESET_MILESTONE_CHECK",
            description = "Project Finance Users can save Milestone Check")
    public boolean projectFinanceUserCanResetMilestoneCheck(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user) && isProjectActive(projectOrganisationCompositeId.getProjectId());
    }

    @PermissionRule(
            value = "VIEW_MILESTONE_STATUS",
            description = "Project Finance Users can view Milestone Check")
    public boolean projectFinanceUserCanViewMilestoneCheck(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user);
    }

    @PermissionRule(
            value = "VIEW_MILESTONE_STATUS",
            description = "Users can see their own Milestone Check status")
    public boolean userCanViewTheirOwnMilestoneStatus(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_FUNDING_RULES",
            description = "Project Finance Users can view Funding Rules status")
    public boolean projectFinanceUserCanViewFundingRules(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user);
    }

    @PermissionRule(
            value = "VIEW_FUNDING_RULES",
            description = "Users can see their own Funding Rules status")
    public boolean userCanViewTheirOwnFundingRulesStatus(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "RESET_ELIGIBILITY",
            description = "Project finance user can reset Eligibility")
    public boolean projectFinanceUserCanResetEligibility(ProjectCompositeId projectCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(
            value = "RESET_VIABILITY",
            description = "Project finance user can reset Viability")
    public boolean projectFinanceUserCanResetViability(ProjectCompositeId projectCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(
            value = "RESET_FINANCE_CHECKS",
            description = "Project finance user can reset both Viability and Eligibility Checks")
    public boolean projectFinanceUserCanResetFinanceChecks(ProjectCompositeId projectCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(
            value = "RESET_ELIGIBILITY",
            description = "System Maintenance can reset Eligibility")
    public boolean systemMaintenanceUserCanResetEligibility(ProjectCompositeId projectCompositeId, UserResource user) {
        return isSystemMaintenanceUser(user) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(
            value = "RESET_VIABILITY",
            description = "System Maintenance can reset Viability")
    public boolean systemMaintenanceUserCanResetViability(ProjectCompositeId projectCompositeId, UserResource user) {
        return isSystemMaintenanceUser(user) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(
            value = "RESET_FINANCE_CHECKS",
            description = "System Maintenance can reset both Viability and Eligibility Checks")
    public boolean systemMaintenanceUserCanResetFinanceChecks(ProjectCompositeId projectCompositeId, UserResource user) {
        return isSystemMaintenanceUser(user) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(
            value = "VIEW_CREDIT_REPORT",
            description = "Project Finance Users can view the Credit Report flag")
    public boolean projectFinanceUserCanViewCreditReport(ProjectCompositeId projectCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user);
    }

    @PermissionRule(
            value = "VIEW_CREDIT_REPORT",
            description = "Competition Finance Users can view the Credit Report flag")
    public boolean competitionFinanceUserCanViewCreditReport(ProjectCompositeId projectCompositeId, UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectCompositeId.id(), user.getId());
    }

    @PermissionRule(
            value = "SAVE_CREDIT_REPORT",
            description = "Competition Finance Users can view the Credit Report flag")
    public boolean competitionFinanceUserCanSaveCreditReport(ProjectCompositeId projectCompositeId, UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectCompositeId.id(), user.getId()) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(
            value = "SAVE_CREDIT_REPORT",
            description = "Project Finance Users can save Credit Report flag")
    public boolean projectFinanceUserCanSaveCreditReport(ProjectCompositeId projectCompositeId, UserResource user) {
        return hasProjectFinanceAuthority(user) && isProjectActive(projectCompositeId.id());
    }

    @PermissionRule(value = "READ_PROJECT_FINANCE", description = "Project partners can see the project finances of their own project")
    public boolean partnersCanSeeTheProjectFinancesForTheirOrganisation(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isPartner(projectFinanceResource.getProject(), user.getId());
    }

    @PermissionRule(value = "READ_PROJECT_FINANCE", description = "An internal user can see project finances for organisations")
    public boolean internalUserCanSeeProjectFinancesForOrganisations(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_PROJECT_FINANCE", description = "A stakeholder user can see project finances for organisations")
    public boolean stakeholderUserCanSeeProjectFinancesForOrganisations(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return user.hasRole(STAKEHOLDER);
    }

    @PermissionRule(value = "READ_PROJECT_FINANCE", description = "A Competition finance user can see project finances for organisations")
    public boolean competitionFinanceUserCanSeeProjectFinancesForOrganisations(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return user.hasRole(EXTERNAL_FINANCE);
    }

    @PermissionRule(value = "UPDATE_PROJECT_FINANCE", description = "Project partners can update the project finances of their own project")
    public boolean projectPartnerCanUpdateProjectFinance(final ProjectFinanceResource financeResource, final UserResource user) {
        return isPartner(financeResource.getProject(), user.getId());
    }

    @PermissionRule(value = "UPDATE_PROJECT_FINANCE", description = "Project finance users users can update the project finances of a project")
    public boolean internalUsersCanUpdateProjectFinance(final ProjectFinanceResource financeResource, final UserResource user) {
        return hasProjectFinanceAuthority(user);
    }

    @PermissionRule(value = "ADD_EMPTY_PROJECT_COST", description = "The consortium can add a cost to the application finances of their own organisation or if lead applicant")
    public boolean partnersCanAddEmptyRowWhenReadingProjectCosts(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isPartner(projectFinanceResource.getProject(), user.getId()) && isProjectActive(projectFinanceResource.getProject());
    }

    @PermissionRule(value = "ADD_EMPTY_PROJECT_COST", description = "The consortium can add a cost to the application finances of their own organisation or if lead applicant")
    public boolean internalUsersCanAddEmptyRowWhenReadingProjectCosts(final ProjectFinanceResource projectFinanceResource, final UserResource user) {
        return isInternal(user) && isProjectActive(projectFinanceResource.getProject());
    }

    @PermissionRule(value = "READ_ELIGIBILITY", description = "Project partners can see the project finances eligibility of their own project")
    public boolean partnersCanSeeTheProjectFinancesForTheirOrganisation(final FinanceCheckEligibilityResource financeCheckEligibilityResource, final UserResource user) {
        return isPartner(financeCheckEligibilityResource.getProjectId(), user.getId());
    }

    @PermissionRule(value = "READ_ELIGIBILITY", description = "Internal users can see the project finances eligibility of their own project")
    public boolean internalUsersCanSeeTheProjectFinancesForTheirOrganisation(final FinanceCheckEligibilityResource financeCheckEligibilityResource, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_ELIGIBILITY", description = "Internal users can see the project finances eligibility of their own project")
    public boolean competitionFinanceUsersCanSeeTheProjectFinancesForTheirOrganisation(final FinanceCheckEligibilityResource financeCheckEligibilityResource, final UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(financeCheckEligibilityResource.getProjectId(), user.getId());
    }

    @PermissionRule(value = "READ_OVERVIEW", description = "Internal users can see the project finance overview")
    public boolean internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(final ProjectCompositeId projectCompositeId, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "READ_OVERVIEW", description = "Competition finance users can see the project finance overview")
    public boolean competitionFinanceUsersCanSeeTheProjectFinanceOverviewsForAllProjects(final ProjectCompositeId projectCompositeId, final UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(projectCompositeId.id(), user.getId());
    }

    @PermissionRule(value = "READ_OVERVIEW", description = "Project partners can see their project finance overview")
    public boolean partnersCanSeeTheProjectFinanceOverviewsForTheirProject(final ProjectCompositeId projectCompositeId, final UserResource user) {
        return isPartner(projectCompositeId.id(), user.getId());
    }
}