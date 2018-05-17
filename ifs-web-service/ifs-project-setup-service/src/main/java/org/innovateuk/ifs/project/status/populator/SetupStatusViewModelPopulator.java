package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.async.util.AsyncAdaptor;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoringofficer.MonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.sections.SectionStatus;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.project.status.viewmodel.SectionAccessList;
import org.innovateuk.ifs.project.status.viewmodel.SectionStatusList;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;

/**
 * Populator for creating the {@link SetupStatusViewModel}
 */
@Service
public class SetupStatusViewModelPopulator extends AsyncAdaptor {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    public CompletableFuture<SetupStatusViewModel> populateViewModel(Long projectId, UserResource loggedInUser) {

        CompletableFuture<ProjectResource> projectRequest = async(() -> projectService.getById(projectId));
        CompletableFuture<OrganisationResource> organisationRequest = async(() -> projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()));

        CompletableFuture<ApplicationResource> applicationRequest = awaitAll(projectRequest).thenApply(project -> applicationService.getById(project.getApplication()));
        CompletableFuture<CompetitionResource> competitionRequest = awaitAll(applicationRequest).thenApply(application -> competitionService.getById(application.getCompetition()));
        CompletableFuture<BasicDetails> basicDetailsRequest = awaitAll(projectRequest, competitionRequest, organisationRequest).thenApply(BasicDetails::new);

        CompletableFuture<ProjectTeamStatusResource> teamStatusRequest = async(() -> statusService.getProjectTeamStatus(projectId, Optional.empty()));
        CompletableFuture<Boolean> isProjectManagerRequest = async(() -> projectService.getProjectManager(projectId).map(pu -> pu.isUser(loggedInUser.getId())).orElse(false));
        CompletableFuture<Optional<MonitoringOfficerResource>> monitoringOfficerRequest = async(() -> monitoringOfficerService.getMonitoringOfficerForProject(projectId));
        CompletableFuture<List<OrganisationResource>> partnerOrganisationsRequest = async(() -> projectService.getPartnerOrganisationsForProject(projectId));

        return awaitAll(basicDetailsRequest, teamStatusRequest, monitoringOfficerRequest, isProjectManagerRequest, partnerOrganisationsRequest).thenApply(futureResults -> {

            BasicDetails basicDetails = basicDetailsRequest.get();
            ProjectTeamStatusResource teamStatus = teamStatusRequest.get();
            Optional<MonitoringOfficerResource> monitoringOfficer = monitoringOfficerRequest.get();
            boolean isProjectManager = isProjectManagerRequest.get();
            List<OrganisationResource> partnerOrganisations = partnerOrganisationsRequest.get();

            return getSetupStatusViewModel(basicDetails, teamStatus, monitoringOfficer, isProjectManager, partnerOrganisations);
        });
    }

    private SetupStatusViewModel getSetupStatusViewModel(BasicDetails basicDetails, ProjectTeamStatusResource teamStatus, Optional<MonitoringOfficerResource> monitoringOfficer, boolean isProjectManager, List<OrganisationResource> partnerOrganisations) {
        SectionAccessList sectionAccesses = getSectionAccesses(basicDetails, teamStatus);
        SectionStatusList sectionStatuses = getSectionStatuses(basicDetails, teamStatus, monitoringOfficer, isProjectManager);

        boolean pendingQueries = SectionStatus.FLAG.equals(sectionStatuses.getFinanceChecksStatus());

        boolean leadPartner = isLeadPartner(teamStatus, basicDetails.getOrganisation());
        int partnerOrganisationsCount = partnerOrganisations.size();
        boolean collaborationAgreementRequired = partnerOrganisationsCount > 1;

        return new SetupStatusViewModel(
                basicDetails.getProject(),
                basicDetails.getCompetition(),
                monitoringOfficer,
                basicDetails.getOrganisation(),
                leadPartner,
                sectionAccesses,
                sectionStatuses,
                collaborationAgreementRequired,
                isProjectManager,
                pendingQueries);
    }

    private SectionStatusList getSectionStatuses(BasicDetails basicDetails, ProjectTeamStatusResource teamStatus, Optional<MonitoringOfficerResource> monitoringOfficer, boolean isProjectManager) {

        CompetitionResource competition = basicDetails.getCompetition();
        OrganisationResource organisation = basicDetails.getOrganisation();
        ProjectResource project = basicDetails.getProject();

        ProjectPartnerStatusResource ownOrganisation = teamStatus.getPartnerStatusForOrganisation(organisation.getId()).get();
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);

        boolean partnerProjectLocationRequired = competition.isLocationPerPartner();

        boolean isLeadPartner = isLeadPartner(teamStatus, organisation);

        boolean isProjectDetailsProcessCompleted = isLeadPartner ? checkLeadPartnerProjectDetailsProcessCompleted(teamStatus, partnerProjectLocationRequired)
                : partnerProjectDetailsComplete(statusAccessor, organisation, partnerProjectLocationRequired);

        boolean isProjectDetailsSubmitted = COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus());

        boolean awaitingProjectDetailsActionFromOtherPartners = isLeadPartner && awaitingProjectDetailsActionFromOtherPartners(teamStatus, partnerProjectLocationRequired);

        boolean requiredProjectDetailsForMonitoringOfficerComplete = requiredProjectDetailsForMonitoringOfficerComplete(partnerProjectLocationRequired, isProjectDetailsSubmitted, teamStatus);

        SetupSectionStatus sectionStatus = new SetupSectionStatus();

        SectionStatus projectDetailsStatus = sectionStatus.projectDetailsSectionStatus(isProjectDetailsProcessCompleted, awaitingProjectDetailsActionFromOtherPartners, isLeadPartner);
        SectionStatus monitoringOfficerStatus = sectionStatus.monitoringOfficerSectionStatus(monitoringOfficer.isPresent(), requiredProjectDetailsForMonitoringOfficerComplete);
        SectionStatus bankDetailsStatus = sectionStatus.bankDetailsSectionStatus(ownOrganisation.getBankDetailsStatus());
        SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(organisation);
        SectionStatus financeChecksStatus = sectionStatus.financeChecksSectionStatus(ownOrganisation.getFinanceChecksStatus(), financeChecksAccess);
        SectionStatus spendProfileStatus= sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus());
        SectionStatus otherDocumentsStatus = sectionStatus.otherDocumentsSectionStatus(project, isProjectManager);
        SectionStatus grantOfferStatus = sectionStatus.grantOfferLetterSectionStatus(ownOrganisation.getGrantOfferLetterStatus(), isLeadPartner);

        return new SectionStatusList(projectDetailsStatus, monitoringOfficerStatus, bankDetailsStatus, financeChecksStatus, spendProfileStatus, otherDocumentsStatus, grantOfferStatus);
    }

    private boolean isLeadPartner(ProjectTeamStatusResource teamStatus, OrganisationResource organisation) {
        return teamStatus.getLeadPartnerStatus().getOrganisationId().equals(organisation.getId());
    }

    private SectionAccessList getSectionAccesses(BasicDetails basicDetails, ProjectTeamStatusResource teamStatus) {

        CompetitionResource competition = basicDetails.getCompetition();
        OrganisationResource organisation = basicDetails.getOrganisation();

        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);

        boolean partnerProjectLocationRequired = competition.isLocationPerPartner();

        SectionAccess companiesHouseAccess = statusAccessor.canAccessCompaniesHouseSection(organisation);
        SectionAccess projectDetailsAccess = statusAccessor.canAccessProjectDetailsSection(organisation);
        SectionAccess monitoringOfficerAccess = statusAccessor.canAccessMonitoringOfficerSection(organisation, partnerProjectLocationRequired);
        SectionAccess bankDetailsAccess = statusAccessor.canAccessBankDetailsSection(organisation);
        SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(organisation);
        SectionAccess spendProfileAccess = statusAccessor.canAccessSpendProfileSection(organisation);
        SectionAccess otherDocumentsAccess = statusAccessor.canAccessOtherDocumentsSection(organisation);
        SectionAccess grantOfferAccess = statusAccessor.canAccessGrantOfferLetterSection(organisation);

        return new SectionAccessList(companiesHouseAccess, projectDetailsAccess, monitoringOfficerAccess, bankDetailsAccess, financeChecksAccess, spendProfileAccess, otherDocumentsAccess, grantOfferAccess);
    }

    private boolean requiredProjectDetailsForMonitoringOfficerComplete(boolean partnerProjectLocationRequired, boolean isProjectDetailsSubmitted, ProjectTeamStatusResource teamStatus) {

        if (partnerProjectLocationRequired) {
            return isProjectDetailsSubmitted && allPartnersProjectLocationStatusComplete(teamStatus);
        } else {
            return isProjectDetailsSubmitted;
        }

    }

    private boolean partnerProjectDetailsComplete(SetupSectionAccessibilityHelper statusAccessor, OrganisationResource organisation, boolean partnerProjectLocationRequired) {
        boolean financeContactSubmitted = statusAccessor.isFinanceContactSubmitted(organisation);

        return partnerProjectLocationRequired ? financeContactSubmitted && statusAccessor.isPartnerProjectLocationSubmitted(organisation)
                : financeContactSubmitted;
    }

    public boolean checkLeadPartnerProjectDetailsProcessCompleted(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        boolean projectDetailsAndAllFinanceContactComplete =  COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && allOtherPartnersFinanceContactStatusComplete(teamStatus);

        return partnerProjectLocationRequired ? projectDetailsAndAllFinanceContactComplete
                && COMPLETE.equals(leadPartnerStatus.getPartnerProjectLocationStatus())
                && allOtherPartnersProjectLocationStatusComplete(teamStatus)
                : projectDetailsAndAllFinanceContactComplete;
    }

    private boolean awaitingProjectDetailsActionFromOtherPartners(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        return partnerProjectLocationRequired ? isAwaitingWhenProjectLocationRequired(teamStatus, leadPartnerStatus)
                : isAwaitingWhenProjectLocationNotRequired(teamStatus, leadPartnerStatus);
    }

    private boolean isAwaitingWhenProjectLocationRequired(ProjectTeamStatusResource teamStatus, ProjectPartnerStatusResource leadPartnerStatus) {
        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && COMPLETE.equals(leadPartnerStatus.getPartnerProjectLocationStatus())
                && (!allOtherPartnersFinanceContactStatusComplete(teamStatus) || !allOtherPartnersProjectLocationStatusComplete(teamStatus));
    }

    private boolean isAwaitingWhenProjectLocationNotRequired(ProjectTeamStatusResource teamStatus, ProjectPartnerStatusResource leadPartnerStatus) {
        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && !allOtherPartnersFinanceContactStatusComplete(teamStatus);
    }

    private boolean allOtherPartnersFinanceContactStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForOtherPartners(status -> COMPLETE.equals(status.getFinanceContactStatus()));
    }

    private boolean allOtherPartnersProjectLocationStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForOtherPartners(status -> COMPLETE.equals(status.getPartnerProjectLocationStatus()));
    }

    private boolean allPartnersProjectLocationStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForAllPartners(status -> COMPLETE.equals(status.getPartnerProjectLocationStatus()));
    }

    /**
     * Simple class to bunch and contain some of the basic information gathered from the top-level API calls to improve
     * readability of the code
     */
    private class BasicDetails {

        private ProjectResource project;
        private CompetitionResource competition;
        private OrganisationResource organisation;

        public BasicDetails(ProjectResource project, CompetitionResource competition, OrganisationResource organisation) {
            this.project = project;
            this.competition = competition;
            this.organisation = organisation;
        }

        public ProjectResource getProject() {
            return project;
        }

        public CompetitionResource getCompetition() {
            return competition;
        }

        public OrganisationResource getOrganisation() {
            return organisation;
        }
    }
}
