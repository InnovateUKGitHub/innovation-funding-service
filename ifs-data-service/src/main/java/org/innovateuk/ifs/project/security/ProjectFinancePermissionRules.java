package org.innovateuk.ifs.project.security;


import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isCompAdmin;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

/**
 * Defines the permissions for interaction with project finances.
 */
@PermissionRules
public class ProjectFinancePermissionRules extends BasePermissionRules {

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Partners can view their own Spend Profile data")
    public boolean partnersCanViewTheirOwnSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Project Finance Users can view their own Spend Profile data")
    public boolean projectFinanceUserCanViewAnySpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE",
            description = "Lead partner view Spend Profile data")
    public boolean leadPartnerCanViewAnySpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isLeadPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Partners and Comp Admin can view their own Spend Profile data")
    public boolean partnersAndCompAdminCanViewTheirOwnSpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isCompAdmin(user) || partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Project Finance and Comp Admin Users can view their own Spend Profile data")
    public boolean projectFinanceUserAndCompAdminCanViewAnySpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isCompAdmin(user) || isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "VIEW_SPEND_PROFILE_CSV",
            description = "Lead partner and Comp Admin view Spend Profile data")
    public boolean leadPartnerAndCompAdminCanViewAnySpendProfileCsv(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return isCompAdmin(user) || isLeadPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(
            value = "EDIT_SPEND_PROFILE",
            description = "Partners can edit their own Spend Profile data")
    public boolean partnersCanEditTheirOwnSpendProfileData(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {

        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(value = "MARK_SPEND_PROFILE_COMPLETE", description = "Any partner belonging to organisation can mark its spend profile as complete")
    public boolean partnersCanMarkSpendProfileAsComplete(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return partnerBelongsToOrganisation(projectOrganisationCompositeId.getProjectId(), user.getId(), projectOrganisationCompositeId.getOrganisationId());
    }

    @PermissionRule(value = "MARK_SPEND_PROFILE_INCOMPLETE", description = "Any lead partner can mark partners spend profiles as incomplete")
    public boolean leadPartnerCanMarkSpendProfileIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return isLeadPartner(projectOrganisationCompositeId.getProjectId(), user.getId());
    }

    @PermissionRule(value = "COMPLETE_SPEND_PROFILE_REVIEW", description = "Only a project manager can complete the projects spend profiles review")
    public boolean projectManagerCanCompleteSpendProfile(Long projectId, UserResource user) {
        return isProjectManager(projectId, user.getId());
    }

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
    public boolean projectFinanceUserCanViewCreditReport(Long projectId, UserResource user) {

        return isProjectFinanceUser(user);
    }

    @PermissionRule(
            value = "SAVE_CREDIT_REPORT",
            description = "Project Finance Users can save Credit Report flag")
    public boolean projectFinanceUserCanSaveCreditReport(Long projectId, UserResource user) {

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
    public boolean internalUsersCanSeeTheProjectFinanceOverviewsForAllProjects(final Long projectId, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value="READ_OVERVIEW", description = "Project partners can see their project finance overview")
    public boolean partnersCanSeeTheProjectFinanceOverviewsForTheirProject(final Long projectId, final UserResource user) {
        return isPartner(projectId, user.getId());
    }

}