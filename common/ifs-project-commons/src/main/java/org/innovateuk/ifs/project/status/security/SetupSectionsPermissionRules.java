package org.innovateuk.ifs.project.status.security;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.SecurityRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.hasIFSAdminAuthority;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@Slf4j
@PermissionRules
@Component
public class SetupSectionsPermissionRules {

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectService projectService;

    @Value("${ifs.monitoringofficer.journey.update.enabled}")
    private boolean isMOJourneyUpdateEnabled;

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "An internal user can access the Project Details section when submitted by Partners (Individual)")
    public boolean internalCanAccessProjectDetailsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessProjectDetailsSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "An internal user can access after project details are submitted by the lead")
    public boolean internalCanAccessMonitoringOfficerSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessMonitoringOfficerSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "Support user can access after project details are submitted by the lead")
    public boolean supportCanAccessMonitoringOfficerSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessMonitoringOfficerSection, SecurityRuleUtil::isSupport);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "Innovation lead user can access after project details are submitted by the lead")
    public boolean innovationLeadUserCanAccessMonitoringOfficerSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessMonitoringOfficerSection, SecurityRuleUtil::isInnovationLead);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "Stakeholder & user with Stakeholder authority can access after project details are submitted by the lead")
    public boolean stakeholderCanAccessMonitoringOfficerSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessMonitoringOfficerSection, SecurityRuleUtil::hasStakeholderAuthority);
    }

    @PermissionRule(value = "EDIT_MONITORING_OFFICER_SECTION", description = "An internal user can access after project details are submitted by the lead")
    public boolean internalCanEditMonitoringOfficerSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessMonitoringOfficerSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_BANK_DETAILS_SECTION", description = "An internal user can access the Bank Details " +
            "section when submitted by Partners (Individual)")
    public boolean internalCanAccessBankDetailsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessBankDetailsSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_BANK_DETAILS_SECTION", description = "An Auditor user can access the Bank Details " +
            "section when all bank details have been approved")
    public boolean auditorCanAccessBankDetailsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessBankDetailsSection, SecurityRuleUtil::hasAuditorAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION", description = "An internal user can always access the Finance checks section")
    public boolean internalCanAccessFinanceChecksSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION", description = "An Auditor user can always access the Finance checks section")
    public boolean auditorCanAccessFinanceChecksSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksSection, SecurityRuleUtil::hasAuditorAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION", description = "A Competition finance user can always access the Finance checks section")
    public boolean competitionFinanceCanAccessFinanceChecksSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksSection, SecurityRuleUtil::isExternalFinanceUser);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "An internal user can access the Spend Profile " +
            "section when the lead partner submits the project spendprofile")
    public boolean internalCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessSpendProfileSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }
    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "A Competition finance user can access the Spend Profile " +
            "section when the lead partner submits the project spendprofile")
    public boolean competitionFinanceUsersCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessSpendProfileSection, SecurityRuleUtil::isExternalFinanceUser);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "Support user can access the Spend Profile " +
            "section when the lead partner submits the project spendprofile")
    public boolean supportCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessSpendProfileSection, SecurityRuleUtil::isSupport);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "Innovation lead user can access the Spend Profile " +
            "section when the lead partner submits the project spendprofile")
    public boolean innovationLeadCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessSpendProfileSection, SecurityRuleUtil::isInnovationLead);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "Stakeholder can access the Spend Profile " +
            "section when the lead partner submits the project spendprofile")
    public boolean stakeholderCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessSpendProfileSection, SecurityRuleUtil::hasStakeholderAuthority);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "Project MO can access the Spend Profile")
    public boolean projectMoCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return isMonitoringOfficerOnProject(projectCompositeId.id(), user.getId());
    }

    @PermissionRule(value = "ACCESS_DOCUMENTS_SECTION", description = "Comp admin or project finance users can access the Documents section")
    public boolean internalAdminUserCanAccessDocumentsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessDocumentsSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_DOCUMENTS_SECTION", description = "A support user can access the Documents section once all documents have been approved")
    public boolean supportUserCanAccessDocumentsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessDocumentsSection, SecurityRuleUtil::isSupport);
    }

    @PermissionRule(value = "ACCESS_DOCUMENTS_SECTION", description = "An innovation lead can access the Documents section once all documents have been approved")
    public boolean innovationLeadCanAccessDocumentsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessDocumentsSection, SecurityRuleUtil::isInnovationLead);
    }

    @PermissionRule(value = "ACCESS_DOCUMENTS_SECTION", description = "A Stakeholder or a user with Stakeholder authority can access the Documents section once all documents have been approved")
    public boolean stakeholderCanAccessDocumentsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessDocumentsSection, SecurityRuleUtil::hasStakeholderAuthority);
    }

    @PermissionRule(value = "ACCESS_DOCUMENTS_SECTION", description = "A monitoring officer can access the Documents section")
    public boolean monitoringOfficerCanAccessDocumentsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessDocumentsSection, SecurityRuleUtil::isMonitoringOfficer);
    }

    @PermissionRule(value = "APPROVE_DOCUMENTS", description = "Internal users can approve or reject documents")
    public boolean internalAdminUserCanApproveDocuments(ProjectCompositeId projectCompositeId, UserResource user) {
        return isMOJourneyUpdateEnabled ? (hasIFSAdminAuthority(user) || isMonitoringOfficer(user)) : (hasCompetitionAdministratorAuthority(user) || hasIFSAdminAuthority(user));
    }

    @PermissionRule(value = "RESET_GRANT_OFFER_LETTER", description = "IFS administrator can reset the grant offer letter section.")
    public boolean ifsAdminUserCanResetGrantOfferLetter(ProjectCompositeId projectCompositeId, UserResource user) {
        return SecurityRuleUtil.hasIFSAdminAuthority(user);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SEND_SECTION", description = "An internal user can access the Grant Offer Letter send " +
            "section when the lead partner submits the documents")
    public boolean internalCanAccessGrantOfferLetterSendSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessGrantOfferLetterSendSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SEND_SECTION", description = "A support user can access the Grant Offer Letter send section when the lead partner submits the documents")
    public boolean supportCanAccessGrantOfferLetterSendSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessGrantOfferLetterSendSection, SecurityRuleUtil::isSupport);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SEND_SECTION", description = "Innovation lead user can access the Grant Offer Letter send section when the lead partner submits the documents")
    public boolean innovationLeadCanAccessGrantOfferLetterSendSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessGrantOfferLetterSendSection, SecurityRuleUtil::isInnovationLead);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SEND_SECTION", description = "Stakeholder and user with Stakeholder authority can access the Grant Offer Letter send section when the lead partner submits the documents")
    public boolean stakeholderCanAccessGrantOfferLetterSendSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessGrantOfferLetterSendSection, SecurityRuleUtil::hasStakeholderAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION", description = "A finance team user can always access the Finance checks queries section")
    public boolean internalCanAccessFinanceChecksQueriesSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksQueriesSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION", description = "A Competition finance user can always access the Finance checks queries section")
    public boolean competitionFinanceUserCanAccessFinanceChecksQueriesSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksQueriesSection, SecurityRuleUtil::isExternalFinanceUser);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION", description = "A Auditor user can always access the Finance checks queries section")
    public boolean auditorUserCanAccessFinanceChecksQueriesSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksQueriesSection, SecurityRuleUtil::hasAuditorAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY", description = "A finance team user cannot add a query until a finance contact has been allocated for the organisation")
    public boolean internalCanAccessFinanceChecksAddQuery(ProjectOrganisationCompositeId target, UserResource user) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(target.getProjectId());
        return simpleFindFirst(projectUsers, pu -> pu.isFinanceContact() && Objects.equals(pu.getOrganisation(), target.getOrganisationId())).isPresent() && doSectionCheck(target.getProjectId(), user, SetupSectionInternalUser::canAccessFinanceChecksQueriesSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY", description = "A comp finance team user cannot add a query until a finance contact has been allocated for the organisation")
    public boolean compFinanceCanAccessFinanceChecksAddQuery(ProjectOrganisationCompositeId target, UserResource user) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(target.getProjectId());
        return simpleFindFirst(projectUsers, pu -> pu.isFinanceContact() && Objects.equals(pu.getOrganisation(), target.getOrganisationId())).isPresent() && doSectionCheck(target.getProjectId(), user, SetupSectionInternalUser::canAccessFinanceChecksQueriesSection, SecurityRuleUtil::isExternalFinanceUser);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_NOTES_SECTION", description = "A finance team can always access the Finance checks notes section")
    public boolean internalCanAccessFinanceChecksNotesSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksNotesSection, SecurityRuleUtil::hasCompetitionAdministratorAuthority);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_NOTES_SECTION", description = "A competition finance user can always access the Finance checks notes section")
    public boolean competitonFinanceUserCanAccessFinanceChecksNotesSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksNotesSection, SecurityRuleUtil::isExternalFinanceUser);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_NOTES_SECTION", description = "A Auditor user can always access the Finance checks notes section")
    public boolean auditorUserCanAccessFinanceChecksNotesSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionInternalUser::canAccessFinanceChecksNotesSection, SecurityRuleUtil::hasAuditorAuthority);
    }

    @PermissionRule(value = "APPROVE_REJECT_SPEND_PROFILE", description = "Internal users can approve or reject documents")
    public boolean canUserApproveOrRejectSpendProfile(ProjectCompositeId projectCompositeId, UserResource user) {
         return hasIFSAdminAuthority(user);
    }
    private boolean isMonitoringOfficerOnProject(long projectId, long userId) {
        return Optional.ofNullable(projectService.getById(projectId))
                .map(ProjectResource::getMonitoringOfficerUser)
                .map(monitoringOfficerId -> monitoringOfficerId.equals(userId))
                .orElse(false);
    }

    private boolean doSectionCheck(Long projectId, UserResource user, BiFunction<SetupSectionInternalUser, UserResource, SectionAccess> sectionCheckFn, Function<UserResource, Boolean> userCheckFn) {
        ProjectStatusResource projectStatusResource;

        if(!userCheckFn.apply(user)){
            return false;
        }

        try {
            projectStatusResource = statusService.getProjectStatus(projectId);
        } catch (ForbiddenActionException e) {
            log.error("Internal user is not allowed to access this project " + projectId, e);
            return false;
        } catch (ObjectNotFoundException e) {
            log.error("Status for project " + projectId + " cannot be found.", e);
            return false;
        }

        SetupSectionInternalUser sectionAccessor = new SetupSectionInternalUser(projectStatusResource);

        return sectionCheckFn.apply(sectionAccessor, user) == ACCESSIBLE;
    }
}
