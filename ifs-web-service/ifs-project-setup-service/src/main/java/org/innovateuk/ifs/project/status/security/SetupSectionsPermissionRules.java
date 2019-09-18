package org.innovateuk.ifs.project.status.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isMonitoringOfficer;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@PermissionRules
@Component
public class SetupSectionsPermissionRules {

    private static final Log LOG = LogFactory.getLog(SetupSectionsPermissionRules.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private OrganisationRestService organisationRestService;

    private SetupSectionPartnerAccessorSupplier accessorSupplier = new SetupSectionPartnerAccessorSupplier();

    @PermissionRule(value = "ACCESS_PROJECT_TEAM_STATUS", description = "A partner can access the Project Team Status page when the project is in a correct state to do so")
    public boolean partnerCanAccessProjectTeamStatus(ProjectCompositeId projectCompositeId, UserResource user) {
        return isProjectInViewableState(projectCompositeId.id());
    }

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "A partner can access the Project Details section when their Companies House data is complete or not required")
    public boolean partnerCanAccessProjectDetailsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessProjectDetailsSection);
    }

    @PermissionRule(value = "ACCESS_PROJECT_TEAM_SECTION", description = "A partner can access the Project Team section when their Companies House data is complete or not required")
    public boolean partnerCanAccessProjectTeamSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessProjectDetailsSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CONTACT_PAGE", description = "A partner can access the Finance Contact " +
            "page when their Companies House data is complete or not required, and the Grant Offer Letter has not yet been generated")
    public boolean partnerCanAccessFinanceContactPage(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessFinanceContactPage);
    }

    @PermissionRule(value = "ACCESS_PARTNER_PROJECT_LOCATION_PAGE", description = "A partner can access the partner project location " +
            "page when their Companies House data is complete or not required, and the Monitoring Officer has not yet been assigned")
    public boolean partnerCanAccessProjectLocationPage(ProjectCompositeId projectCompositeId, UserResource user) {
        boolean partnerProjectLocationRequired = isPartnerProjectLocationRequired(projectCompositeId);
        return doSectionCheck(projectCompositeId.id(), user,
                (setupSectionAccessibilityHelper, organisation) -> setupSectionAccessibilityHelper.canAccessPartnerProjectLocationPage(organisation, partnerProjectLocationRequired));
    }

    private boolean isPartnerProjectLocationRequired(ProjectCompositeId projectCompositeId) {
        ProjectResource project = projectService.getById(projectCompositeId.id());
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

        return competition.isLocationPerPartner();
    }

    @PermissionRule(value = "ACCESS_PROJECT_MANAGER_PAGE", description = "A lead can access the Project Manager " +
            "page when their Companies House data is complete or not required, and the Grant Offer Letter has not yet been generated")
    public boolean leadCanAccessProjectManagerPage(ProjectCompositeId projectCompositeId, UserResource user) {
        return !isMonitoringOfficerOnProject(projectCompositeId.id(), user.getId()) && doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::leadCanAccessProjectManagerPage);
    }

    private boolean isMonitoringOfficerOnProject(long projectId, long userId) {
        return Optional.ofNullable(projectService.getById(projectId))
                .map(ProjectResource::getMonitoringOfficerUser)
                .map(monitoringOfficerId -> monitoringOfficerId.equals(userId))
                .orElse(false);
    }

    @PermissionRule(value = "ACCESS_PROJECT_START_DATE_PAGE", description = "A lead can access the Project Start Date " +
            "page when their Companies House data is complete or not required, and the Spend Profile has not yet been generated")
    public boolean leadCanAccessProjectStartDatePage(ProjectCompositeId projectCompositeId, UserResource user) {
        return !isMonitoringOfficerOnProject(projectCompositeId.id(), user.getId()) && doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::leadCanAccessProjectStartDatePage);
    }

    @PermissionRule(value = "ACCESS_PROJECT_ADDRESS_PAGE", description = "A lead can access the Project Address " +
            "page when their Companies House data is complete or not required, and the Grant Offer Letter has not yet been generated")
    public boolean leadCanAccessProjectAddressPage(ProjectCompositeId projectCompositeId, UserResource user) {
        return !isMonitoringOfficerOnProject(projectCompositeId.id(), user.getId()) && doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::leadCanAccessProjectAddressPage);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "A partner can access the Monitoring Officer " +
            "section when their Companies House details are complete or not required, and the Project Details have been submitted")
    public boolean partnerCanAccessMonitoringOfficerSection(ProjectCompositeId projectCompositeId, UserResource user) {
        boolean partnerProjectLocationRequired = isPartnerProjectLocationRequired(projectCompositeId);
        return doSectionCheck(projectCompositeId.id(), user,
                (setupSectionAccessibilityHelper, organisation) -> setupSectionAccessibilityHelper.canAccessMonitoringOfficerSection(organisation, partnerProjectLocationRequired));
    }

    @PermissionRule(value = "ACCESS_BANK_DETAILS_SECTION", description = "A partner can access the Bank Details " +
            "section when their Companies House details are complete or not required, and they have a Finance Contact " +
            "available for their Organisation")
    public boolean partnerCanAccessBankDetailsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return !isMonitoringOfficerOnProject(projectCompositeId.id(), user.getId()) && doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessBankDetailsSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL", description = "A partner can access the finance details " +
            " when their Companies House details are complete or not required, and the Project Details have been submitted")
    public boolean partnerCanAccessFinanceChecksSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return !isMonitoringOfficerOnProject(projectCompositeId.id(), user.getId()) &&  doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessFinanceChecksSection);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "A partner can access the Spend Profile " +
            "section when their Companies House details are complete or not required, the Project Details have been submitted, " +
            "and the Organisation's Bank Details have been approved or queried")
    public boolean partnerCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @PermissionRule(value = "ACCESS_TOTAL_SPEND_PROFILE_SECTION", description = "Only the project manager can access the Spend Profile " +
            "section when their Companies House details are complete or not required, the Project Details have been submitted, " +
            "and the Organisation's Bank Details have been approved or queried")
    public boolean projectManagerCanAccessSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return isProjectManager(projectCompositeId.id(), user) && doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @PermissionRule(value = "SUBMIT_SPEND_PROFILE_SECTION", description = "A partner can attempt to submit the Spend Profile " +
            "section when their Companies House details are complete or not required, the Project Details have been submitted, " +
            "and the Organisation's Bank Details have been approved or queried")
    public boolean partnerCanSubmitSpendProfileSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessSpendProfileSection);
    }

    @PermissionRule(value = "EDIT_SPEND_PROFILE_SECTION", description = "A partner can edit their own Spend Profile " +
            "section when their Companies House details are complete or not required, the Project Details have been submitted, " +
            "and the Organisation's Bank Details have been approved or not required")
    public boolean partnerCanEditSpendProfileSection(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        return doSectionCheck(projectOrganisationCompositeId.getProjectId(), user, (setupSectionAccessibilityHelper, organisation) -> setupSectionAccessibilityHelper.canEditSpendProfileSection(organisation, projectOrganisationCompositeId.getOrganisationId()));
    }

    @PermissionRule(value = "ACCESS_DOCUMENTS_SECTION", description = "A lead can access Documents section. A partner can access Documents " +
            "section if their Companies House information is unnecessary or complete")
    public boolean canAccessDocumentsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessDocumentsSection);
    }

    @PermissionRule(value = "EDIT_DOCUMENTS_SECTION", description = "A project manager can edit Documents section")
    public boolean projectManagerCanEditDocumentsSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return isProjectManager(projectCompositeId.id(), user);
    }

    private boolean isProjectManager(Long projectId, UserResource user) {
        return projectService.isProjectManager(user.getId(), projectId);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SECTION", description = "A lead partner can access the Grant Offer Letter " +
            "section when all other sections are complete and a Grant Offer Letter has been generated by the internal team")
    public boolean partnerCanAccessGrantOfferLetterSection(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection);
    }

    @PermissionRule(value = "ACCESS_SIGNED_GRANT_OFFER_LETTER", description = "A lead partner can view and download signed grant offer letter document")
    public boolean leadPartnerAccessToSignedGrantOfferLetter(ProjectCompositeId projectCompositeId, UserResource user) {
        return doSectionCheck(projectCompositeId.id(), user, SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection) &&
                projectService.isUserLeadPartner(projectCompositeId.id(), user.getId());
    }

    @PermissionRule(value = "MARK_SPEND_PROFILE_INCOMPLETE", description = "All lead partners can mark partners spend profiles as incomplete")
    public boolean userCanMarkSpendProfileIncomplete(ProjectCompositeId projectCompositeId, UserResource user) {
        List<ProjectUserResource> projectLeadPartners = projectService.getLeadPartners(projectCompositeId.id());
        Optional<ProjectUserResource> returnedProjectUser = simpleFindFirst(projectLeadPartners, projectUserResource -> projectUserResource.getUser().equals(user.getId()));

        return returnedProjectUser.isPresent();
    }

    @PermissionRule(value = "IS_NOT_FROM_OWN_ORGANISATION", description = "A lead partner cannot mark their own spend profiles as incomplete")
    public boolean userCannotMarkOwnSpendProfileIncomplete(ProjectOrganisationCompositeId projectOrganisationCompositeId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndProjectId(user.getId(), projectOrganisationCompositeId.getProjectId()).getSuccess();
        return !organisation.getId().equals(projectOrganisationCompositeId.getOrganisationId());
    }

    private boolean doSectionCheck(long projectId, UserResource user, BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> sectionCheckFn) {
        try {

            if (!isProjectInViewableState(projectId)) {
                return false;
            }

            boolean isMonitoringOfficer = isMonitoringOfficer(user);

            long organisationId = isMonitoringOfficer ?
                    projectService.getLeadOrganisation(projectId).getId() : projectService.getOrganisationIdFromUser(projectId, user);

            ProjectTeamStatusResource teamStatus = isMonitoringOfficer ?
                    getProjectTeamStatusForMonitoringOfficer(projectId) : statusService.getProjectTeamStatus(projectId, Optional.of(user.getId()));

            ProjectPartnerStatusResource partnerStatusForUser = teamStatus.getPartnerStatusForOrganisation(organisationId).get();

            SetupSectionAccessibilityHelper sectionAccessor = accessorSupplier.apply(teamStatus);
            OrganisationResource organisation = new OrganisationResource();
            organisation.setId(partnerStatusForUser.getOrganisationId());
            organisation.setOrganisationType(partnerStatusForUser.getOrganisationType().getId());

            return sectionCheckFn.apply(sectionAccessor, organisation) == ACCESSIBLE;
        } catch (ForbiddenActionException e) {
            LOG.error("User " + user.getId() + " is not a Partner on an Organisation for Project " + projectId + ".  Denying access to Project Setup", e);
            return false;
        }
    }

    private ProjectTeamStatusResource getProjectTeamStatusForMonitoringOfficer(long projectId) {
        ProjectUserResource leadProjectUser = projectService.getLeadPartners(projectId).stream().findFirst().get();
        return statusService.getProjectTeamStatus(projectId, Optional.of(leadProjectUser.getUser()));
    }

    private boolean isProjectInViewableState(long projectId) {
        ProjectResource project = projectService.getById(projectId);
        return !project.getProjectState().isOffline()
                && !project.getProjectState().isWithdrawn();
    }

    public class SetupSectionPartnerAccessorSupplier implements Function<ProjectTeamStatusResource, SetupSectionAccessibilityHelper> {
        @Override
        public SetupSectionAccessibilityHelper apply(ProjectTeamStatusResource teamStatus) {
            return new SetupSectionAccessibilityHelper(teamStatus);
        }
    }
}
