package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.project.status.viewmodel.SectionAccessList;
import org.innovateuk.ifs.project.status.viewmodel.SectionStatusList;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusStageViewModel;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusViewModel;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.sections.SectionStatus;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;

/**
 * Populator for creating the {@link SetupStatusViewModel}
 */
@Service
public class SetupStatusViewModelPopulator extends AsyncAdaptor {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private SetupSectionStatus sectionStatus;

    public SetupStatusViewModel populateViewModel(long projectId,
                                                  UserResource loggedInUser) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        boolean  monitoringOfficer = loggedInUser.getId().equals(project.getMonitoringOfficerUser());

        List<SetupStatusStageViewModel> stages = competition.getProjectSetupStages().stream()
                .map(stage -> toStageViewModel(stage, project, competition, loggedInUser, monitoringOfficer))
                .collect(toList());

        return new SetupStatusViewModel(
                project,
                monitoringOfficer,
                stages);
    }

    private SetupStatusStageViewModel toStageViewModel(ProjectSetupStage stage, ProjectResource project, CompetitionResource competition, UserResource user, boolean monitoringOfficer) {
        CompletableFuture<ProjectTeamStatusResource> teamStatusRequest = async(() -> statusService.getProjectTeamStatus(project.getId(), Optional.empty()));
        CompletableFuture<OrganisationResource> organisationRequest = async(() -> monitoringOfficer ?
                                projectService.getLeadOrganisation(project.getId()) :
                                projectRestService.getOrganisationByProjectAndUser(project.getId(), user.getId()).getSuccess());

        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(resolve(teamStatusRequest));
        boolean projectComplete = project.getProjectState().isLive();
        boolean isLeadPartner = isLeadPartner(resolve(teamStatusRequest), resolve(organisationRequest));
        boolean partnerProjectLocationRequired = competition.isLocationPerPartner();
        ProjectPartnerStatusResource ownOrganisation = resolve(teamStatusRequest).getPartnerStatusForOrganisation(resolve(organisationRequest).getId()).get();
        switch (stage) {
            case PROJECT_DETAILS:
                boolean isProjectDetailsProcessCompleted =
                        isLeadPartner ?
                                checkLeadPartnerProjectDetailsProcessCompleted(resolve(teamStatusRequest))
                                : partnerProjectDetailsComplete(statusAccessor, resolve(organisationRequest), partnerProjectLocationRequired);
                boolean awaitingProjectDetailsActionFromOtherPartners = isLeadPartner && awaitingProjectDetailsActionFromOtherPartners(resolve(teamStatusRequest),
                        partnerProjectLocationRequired);
                return new SetupStatusStageViewModel(stage.getColumnName(),
                        projectComplete ? "Confirm the proposed start date and location of the project."
                            : "The proposed start date and location of the project.",
                        projectComplete? String.format("/project/%d/details", project.getId())
                            : String.format("/project/%d/readonly", project.getId()),
                        sectionStatus.projectDetailsSectionStatus(
                                isProjectDetailsProcessCompleted,
                                awaitingProjectDetailsActionFromOtherPartners,
                                isLeadPartner),
                        statusAccessor.canAccessProjectDetailsSection(resolve(organisationRequest))
                    );
            case PROJECT_TEAM:
                return new SetupStatusStageViewModel(stage.getColumnName(),
                        projectComplete ? "Add people to your project."
                                : "The people on your project.",
                        projectComplete ? String.format("/project/%d/team", project.getId())
                                : String.format("/project/%d/readonly", project.getId()),
                        sectionStatus.projectTeamSectionStatus(ownOrganisation.getProjectTeamStatus()),
                        statusAccessor.canAccessProjectTeamSection(resolve(organisationRequest))
                    );
            case DOCUMENTS:
                boolean isProjectManager = projectService.getProjectManager(project.getId()).map(pu -> pu.isUser(user.getId())).orElse(false);
                List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(project.getId());
                boolean collaborationAgreementRequired = partnerOrganisations.size() > 1;
                return new SetupStatusStageViewModel(stage.getColumnName(),
                        isProjectManager ? "You must upload supporting documents to be reviewed."
                                : "The Project Manager must upload supporting documents to be reviewed.",
                        String.format("/project/%d/document/all", project.getId()),
                        sectionStatus.documentsSectionStatus(
                                isProjectManager,
                                getCompetitionDocuments(
                                        competition,
                                        collaborationAgreementRequired
                                ),
                                project.getProjectDocuments()
                        ),
                        statusAccessor.canAccessDocumentsSection(resolve(organisationRequest))
                );
            case MONITORING_OFFICER:
                Optional<MonitoringOfficerResource> maybeMonitoringOfficer = monitoringOfficerService.findMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject();
                boolean isProjectDetailsSubmitted = COMPLETE.equals(resolve(teamStatusRequest).getLeadPartnerStatus().getProjectDetailsStatus());
                boolean requiredProjectDetailsForMonitoringOfficerComplete =
                        requiredProjectDetailsForMonitoringOfficerComplete(partnerProjectLocationRequired,
                                isProjectDetailsSubmitted,
                                resolve(teamStatusRequest));
                return new SetupStatusStageViewModel("Monitoring Officer",
                        maybeMonitoringOfficer.isPresent() ? String.format("Your Monitoring Officer for this project is %s", maybeMonitoringOfficer.get().getFullName())
                                : "We will assign the project a Monitoring Officer.",
                        projectComplete ? String.format("/project/%d/monitoring-officer", project.getId())
                                : String.format("/project/%d/monitoring-officer/readonly", project.getId()),
                        sectionStatus.monitoringOfficerSectionStatus(maybeMonitoringOfficer.isPresent(),
                                requiredProjectDetailsForMonitoringOfficerComplete),
                        statusAccessor.canAccessMonitoringOfficerSection(resolve(organisationRequest), partnerProjectLocationRequired),
                        maybeMonitoringOfficer.isPresent() ? null : "Awaiting assignment"
                );
            case BANK_DETAILS:
                return new SetupStatusStageViewModel(stage.getColumnName(),
                        "We need bank details for those partners eligible for funding.",
                        projectComplete ? String.format("/project/%d/bank-details", project.getId())
                                : String.format("/project/%d/bank-details/readonly", project.getId()),
                        sectionStatus.bankDetailsSectionStatus(ownOrganisation.getBankDetailsStatus()),
                        monitoringOfficer ? SectionAccess.NOT_ACCESSIBLE : statusAccessor.canAccessBankDetailsSection(resolve(organisationRequest))
                );
            case FINANCE_CHECKS:
                SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(resolve(organisationRequest));
                SectionStatus financeChecksStatus = sectionStatus.financeChecksSectionStatus(
                        ownOrganisation.getFinanceChecksStatus(),
                        financeChecksAccess
                );
                boolean pendingQueries = SectionStatus.FLAG.equals(financeChecksStatus);

                return new SetupStatusStageViewModel(stage.getColumnName(),
                       "We will review your financial information.",
                        String.format("/project/%d/finance-checks", project.getId()),
                        financeChecksStatus,
                        monitoringOfficer ? SectionAccess.NOT_ACCESSIBLE : financeChecksAccess,
                        pendingQueries ? "Pending query" : null
                );
            case SPEND_PROFILE:
                return new SetupStatusStageViewModel(stage.getColumnName(),
                        "Once we have approved your project finances you can change your project spend profile.",
                        String.format("/project/%d/spend-profile", project.getId()),
                        sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus()),
                        statusAccessor.canAccessSpendProfileSection(resolve(organisationRequest))
                );
            case GRANT_OFFER_LETTER:
                return new SetupStatusStageViewModel("Grant offer letter",
                        "Once all tasks are complete the Project Manager can review, sign and submit the grant offer letter to Innovate UK.",
                        String.format("/project/%d/offer", project.getId()),
                        sectionStatus.grantOfferLetterSectionStatus(
                                ownOrganisation.getGrantOfferLetterStatus(),
                                isLeadPartner
                        ),
                        statusAccessor.canAccessGrantOfferLetterSection(resolve(organisationRequest))
                );
        }
        return new SetupStatusStageViewModel(stage.getColumnName(),"","", SectionStatus.EMPTY, SectionAccess.ACCESSIBLE);
    }








    private SetupStatusViewModel getSetupStatusViewModel(BasicDetails basicDetails,
                                                         ProjectTeamStatusResource teamStatus,
                                                         Optional<MonitoringOfficerResource> monitoringOfficer,
                                                         boolean isProjectManager,
                                                         List<OrganisationResource> partnerOrganisations,
                                                         boolean isMonitoringOfficer) {


//        CompletableFuture<OrganisationResource> organisationRequest =
//                awaitAll(projectRequest).thenApply(project ->
//                        loggedInUser.getId().equals(project.getMonitoringOfficerUser()) ?
//                                projectService.getLeadOrganisation(projectId) :
//                                projectRestService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId()).getSuccess());
//        CompletableFuture<ProjectTeamStatusResource> teamStatusRequest = async(() -> statusService.getProjectTeamStatus(projectId, Optional.empty()));
//        CompletableFuture<Boolean> isProjectManagerRequest = async(() -> projectService.getProjectManager(projectId).map(pu -> pu.isUser(loggedInUser.getId())).orElse(false));
//        CompletableFuture<Optional<MonitoringOfficerResource>> monitoringOfficerRequest = async(() -> monitoringOfficerService.findMonitoringOfficerForProject(projectId).getOptionalSuccessObject());
//        CompletableFuture<List<OrganisationResource>> partnerOrganisationsRequest = async(() -> projectService.getPartnerOrganisationsForProject(projectId));

        boolean collaborationAgreementRequired = partnerOrganisations.size() > 1;

        SectionAccessList sectionAccesses = getSectionAccesses(basicDetails, teamStatus);
        SectionStatusList sectionStatuses = getSectionStatuses(basicDetails, teamStatus, monitoringOfficer, isProjectManager, collaborationAgreementRequired);

        boolean pendingQueries = SectionStatus.FLAG.equals(sectionStatuses.getFinanceChecksStatus());

        boolean leadPartner = isLeadPartner(teamStatus, basicDetails.getOrganisation());
        boolean projectDocuments = basicDetails.getCompetition().getCompetitionDocuments().size() > 0;

    return null;
    }

    private SectionStatusList getSectionStatuses(BasicDetails basicDetails,
                                                 ProjectTeamStatusResource teamStatus,
                                                 Optional<MonitoringOfficerResource> monitoringOfficer,
                                                 boolean isProjectManager,
                                                 boolean collaborationAgreementRequired) {

        if (teamStatus.getProjectState().isOffline()) {
            return SectionStatusList.offline();
        }

        CompetitionResource competition = basicDetails.getCompetition();
        OrganisationResource organisation = basicDetails.getOrganisation();
        ProjectResource project = basicDetails.getProject();

        ProjectPartnerStatusResource ownOrganisation = teamStatus.getPartnerStatusForOrganisation(organisation.getId()).get();
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);

        boolean partnerProjectLocationRequired = competition.isLocationPerPartner();

        boolean isLeadPartner = isLeadPartner(teamStatus, organisation);

        boolean isProjectDetailsProcessCompleted =
                isLeadPartner ?
                        checkLeadPartnerProjectDetailsProcessCompleted(teamStatus)
                        : partnerProjectDetailsComplete(statusAccessor, organisation, partnerProjectLocationRequired);

        boolean isProjectDetailsSubmitted = COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus());

        boolean awaitingProjectDetailsActionFromOtherPartners = isLeadPartner && awaitingProjectDetailsActionFromOtherPartners(teamStatus,
                                                                                                                               partnerProjectLocationRequired);

        boolean requiredProjectDetailsForMonitoringOfficerComplete =
                requiredProjectDetailsForMonitoringOfficerComplete(partnerProjectLocationRequired,
                                                                   isProjectDetailsSubmitted,
                                                                   teamStatus);


        SectionStatus projectDetailsStatus = sectionStatus.projectDetailsSectionStatus(
                isProjectDetailsProcessCompleted,
                awaitingProjectDetailsActionFromOtherPartners,
                isLeadPartner);
        SectionStatus projectTeamStatus = sectionStatus.projectTeamSectionStatus(ownOrganisation.getProjectTeamStatus());
        SectionStatus monitoringOfficerStatus = sectionStatus.monitoringOfficerSectionStatus(monitoringOfficer.isPresent(),
                                                                                             requiredProjectDetailsForMonitoringOfficerComplete);
        SectionStatus bankDetailsStatus = sectionStatus.bankDetailsSectionStatus(ownOrganisation.getBankDetailsStatus());
        SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(organisation);
        SectionStatus financeChecksStatus = sectionStatus.financeChecksSectionStatus(
                ownOrganisation.getFinanceChecksStatus(),
                financeChecksAccess
        );
        SectionStatus spendProfileStatus = sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus());
        SectionStatus documentsStatus = sectionStatus.documentsSectionStatus(
                isProjectManager,
                getCompetitionDocuments(
                        competition,
                        collaborationAgreementRequired
                ),
                project.getProjectDocuments()
        );
        SectionStatus grantOfferStatus = sectionStatus.grantOfferLetterSectionStatus(
                ownOrganisation.getGrantOfferLetterStatus(),
                isLeadPartner
        );

        return new SectionStatusList(
                projectDetailsStatus,
                projectTeamStatus,
                monitoringOfficerStatus,
                bankDetailsStatus,
                financeChecksStatus,
                spendProfileStatus,
                documentsStatus,
                grantOfferStatus
        );
    }

    private List<CompetitionDocumentResource> getCompetitionDocuments(CompetitionResource competition, boolean collaborationAgreementRequired) {

        List<CompetitionDocumentResource> competitionDocuments = competition.getCompetitionDocuments();

        if (!collaborationAgreementRequired) {
            competitionDocuments.removeIf(
                    document -> document.getTitle().equals(COLLABORATION_AGREEMENT_TITLE));
        }

        return competitionDocuments;
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
        SectionAccess projectTeamAccess = statusAccessor.canAccessProjectTeamSection(organisation);
        SectionAccess monitoringOfficerAccess = statusAccessor.canAccessMonitoringOfficerSection(organisation, partnerProjectLocationRequired);
        SectionAccess bankDetailsAccess = statusAccessor.canAccessBankDetailsSection(organisation);
        SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(organisation);
        SectionAccess spendProfileAccess = statusAccessor.canAccessSpendProfileSection(organisation);
        SectionAccess documentsAccess = statusAccessor.canAccessDocumentsSection(organisation);
        SectionAccess grantOfferAccess = statusAccessor.canAccessGrantOfferLetterSection(organisation);

        return new SectionAccessList(companiesHouseAccess,
                                     projectDetailsAccess,
                                     projectTeamAccess,
                                     monitoringOfficerAccess,
                                     bankDetailsAccess,
                                     financeChecksAccess,
                                     spendProfileAccess,
                                     documentsAccess,
                                     grantOfferAccess);
    }

    private boolean requiredProjectDetailsForMonitoringOfficerComplete(boolean partnerProjectLocationRequired, boolean isProjectDetailsSubmitted, ProjectTeamStatusResource teamStatus) {

        if (partnerProjectLocationRequired) {
            return isProjectDetailsSubmitted && allPartnersProjectLocationStatusComplete(teamStatus);
        } else {
            return isProjectDetailsSubmitted;
        }

    }

    private boolean partnerProjectDetailsComplete(SetupSectionAccessibilityHelper statusAccessor, OrganisationResource organisation, boolean partnerProjectLocationRequired) {

        return !partnerProjectLocationRequired || statusAccessor.isPartnerProjectLocationSubmitted(organisation);
    }

    public boolean checkLeadPartnerProjectDetailsProcessCompleted(ProjectTeamStatusResource teamStatus) {
        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();
        return leadPartnerStatus.getProjectDetailsStatus().equals(COMPLETE);
    }

    private boolean awaitingProjectDetailsActionFromOtherPartners(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        return partnerProjectLocationRequired && isAwaitingWhenProjectLocationRequired(teamStatus, leadPartnerStatus);
    }

    private boolean isAwaitingWhenProjectLocationRequired(ProjectTeamStatusResource teamStatus, ProjectPartnerStatusResource leadPartnerStatus) {
        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getPartnerProjectLocationStatus())
                && !allOtherPartnersProjectLocationStatusComplete(teamStatus);
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

    private class BasicDetails {
        private ProjectResource project;
        private CompetitionResource competition;
        private ApplicationResource application;
        private OrganisationResource organisation;

        public ProjectResource getProject() {
            return project;
        }

        public CompetitionResource getCompetition() {
            return competition;
        }

        public ApplicationResource getApplication() {
            return application;
        }

        public OrganisationResource getOrganisation() {
            return organisation;
        }
    }
}
