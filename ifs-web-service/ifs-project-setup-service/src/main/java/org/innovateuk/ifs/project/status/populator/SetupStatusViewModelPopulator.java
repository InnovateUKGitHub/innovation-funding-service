package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupPostAwardServiceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusStageViewModel;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusViewModel;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.sections.SectionStatus;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.sections.SectionStatus.TICK;

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
    private CompetitionRestService competitionRestService;

    @Autowired
    private SetupSectionStatus sectionStatus;

    @Autowired
    private CompetitionSetupPostAwardServiceRestService competitionSetupPostAwardServiceRestService;

    @Autowired
    private NavigationUtils navigationUtils;

    public SetupStatusViewModel populateViewModel(long projectId,
                                                  UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        boolean monitoringOfficer = monitoringOfficerService.isMonitoringOfficerOnProject(projectId, loggedInUser.getId()).getSuccess();

        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        RestResult<OrganisationResource> organisationResult = projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId());
        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(project.getId(), Optional.of(loggedInUser.getId()));

        CompletableFuture<ProjectTeamStatusResource> teamStatusRequest = async(() -> statusService.getProjectTeamStatus(project.getId(), Optional.empty()));
        CompletableFuture<OrganisationResource> organisationRequest = async(() -> monitoringOfficer ?
                projectService.getLeadOrganisation(project.getId()) :
                projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId()).getSuccess());

        List<SetupStatusStageViewModel> stages = competition.getProjectSetupStages().stream()
                .filter(stage -> (ProjectSetupStage.BANK_DETAILS != stage) || showBankDetails(organisationResult, teamStatus))
                .map(stage -> toStageViewModel(stage, project, competition, loggedInUser, monitoringOfficer, teamStatusRequest, organisationRequest))
                .collect(toList());

        boolean isInvestorPartnership = FundingType.INVESTOR_PARTNERSHIPS == competition.getFundingType();

        RestResult<CompetitionPostAwardServiceResource> competitionPostAwardServiceResource = competitionSetupPostAwardServiceRestService.getPostAwardService(project.getCompetition());

        boolean isProjectManager = projectService.isProjectManager(loggedInUser.getId(), projectId);
        boolean isProjectFinanceContact = projectService.isProjectFinanceContact(loggedInUser.getId(), projectId);

        return new SetupStatusViewModel(
                project,
                monitoringOfficer,
                stages,
                competition.isLoan(),
                showApplicationFeedbackLink(project, loggedInUser, monitoringOfficer),
                isInvestorPartnership,
                isProjectManager,
                isProjectFinanceContact,
                competitionPostAwardServiceResource.getSuccess().getPostAwardService(),
                navigationUtils.getLiveProjectsLandingPageUrl());
    }

    private boolean showBankDetails(RestResult<OrganisationResource> organisationResult, ProjectTeamStatusResource teamStatus) {

        if (organisationResult.isFailure()) {
            return true;
        }

        OrganisationResource organisation = organisationResult.getSuccess();

        if (organisation.isInternational()) {
            return false;
        }

        Optional<ProjectPartnerStatusResource> statusForOrg = teamStatus.getPartnerStatusForOrganisation(organisation.getId());

        if (statusForOrg.isPresent() && ProjectActivityStates.NOT_REQUIRED == statusForOrg.get().getBankDetailsStatus()) {
            return false;
        }
        return true;
    }

    private boolean showApplicationFeedbackLink(ProjectResource project,
                                                UserResource loggedInUser,
                                                boolean isMonitoringOfficer){

        if (isMonitoringOfficer){
            return true;
        } else {
            long organisationId = projectService.getOrganisationIdFromUser(project.getId(), loggedInUser);
            return projectRestService.existsOnApplication(project.getId(), organisationId).getSuccess();
        }
    }

    private SetupStatusStageViewModel toStageViewModel(ProjectSetupStage stage, ProjectResource project, CompetitionResource competition, UserResource user, boolean monitoringOfficer,
                                                       CompletableFuture<ProjectTeamStatusResource> teamStatusRequest, CompletableFuture<OrganisationResource> organisationRequest) {

        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(resolve(teamStatusRequest));
        boolean competitionIsProcurement = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess().isProcurement();
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
                return new SetupStatusStageViewModel(stage, stage.getShortName(),
                        projectComplete ? "Confirm the proposed start date and location of the project."
                                : competitionIsProcurement ? "The start date and location of the project." : "The proposed start date and location of the project.",
                        projectComplete ? format("/project/%d/details/readonly", project.getId())
                                : format("/project/%d/details", project.getId()),
                        sectionStatus.projectDetailsSectionStatus(
                                isProjectDetailsProcessCompleted,
                                awaitingProjectDetailsActionFromOtherPartners,
                                isLeadPartner),
                        statusAccessor.canAccessProjectDetailsSection(resolve(organisationRequest))
                );
            case PROJECT_TEAM:
                return new SetupStatusStageViewModel(stage, stage.getShortName(),
                        projectComplete ? "Add people to your project."
                                : "The people on your project.",
                        projectComplete ? format("/project/%d/team", project.getId())
                                : format("/project/%d/team", project.getId()),
                        sectionStatus.projectTeamSectionStatus(ownOrganisation.getProjectTeamStatus()),
                        statusAccessor.canAccessProjectTeamSection(resolve(organisationRequest))
                );
            case DOCUMENTS:
                boolean isProjectManager = projectService.getProjectManager(project.getId()).map(pu -> pu.isUser(user.getId())).orElse(false);
                return new SetupStatusStageViewModel(stage, stage.getShortName(),
                        isProjectManager ? "You must upload supporting documents to be reviewed."
                                : "The Project Manager must upload supporting documents to be reviewed.",
                        format("/project/%d/document/all", project.getId()),
                        sectionStatus.documentsSectionStatus(
                                isProjectManager,
                                project,
                                competition
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
                return new SetupStatusStageViewModel(stage, "Monitoring Officer",
                        maybeMonitoringOfficer.isPresent() ? format("Your Monitoring Officer for this project is %s.", maybeMonitoringOfficer.get().getFullName())
                                : "We will assign the project a Monitoring Officer.",
                        projectComplete ? format("/project/%d/monitoring-officer/readonly", project.getId())
                                : format("/project/%d/monitoring-officer", project.getId()),
                        sectionStatus.monitoringOfficerSectionStatus(maybeMonitoringOfficer.isPresent(),
                                requiredProjectDetailsForMonitoringOfficerComplete),
                        statusAccessor.canAccessMonitoringOfficerSection(resolve(organisationRequest), partnerProjectLocationRequired),
                        maybeMonitoringOfficer.isPresent() ? null : "awaiting-assignment"
                );
            case BANK_DETAILS:
                return new SetupStatusStageViewModel(stage, stage.getShortName(),
                        competitionIsProcurement ? "We need your organisation's bank details." : "We need bank details for those partners eligible for funding.",
                        projectComplete ? format("/project/%d/bank-details/readonly", project.getId())
                                : format("/project/%d/bank-details", project.getId()),
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

                return new SetupStatusStageViewModel(stage, stage.getShortName(),
                        "We will review your financial information.",
                        format("/project/%d/finance-checks", project.getId()),
                        financeChecksStatus,
                        monitoringOfficer ? SectionAccess.NOT_ACCESSIBLE : financeChecksAccess,
                        pendingQueries ? "pending-query" : null
                );
            case SPEND_PROFILE:
                return new SetupStatusStageViewModel(stage, stage.getShortName(),
                        "Once we have approved your project finances you can change your project spend profile.",
                        format("/project/%d/partner-organisation/%d/spend-profile", project.getId(), resolve(organisationRequest).getId()),
                        sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus()),
                        statusAccessor.canAccessSpendProfileSection(resolve(organisationRequest))
                );
            case GRANT_OFFER_LETTER:
                String title = competitionIsProcurement ? "Contract" : "Grant offer letter";
                return new SetupStatusStageViewModel(stage, title,
                        "Once all tasks are complete the Project Manager can review, sign and submit the grant offer letter to Innovate UK.",
                        format("/project/%d/offer", project.getId()),
                        sectionStatus.grantOfferLetterSectionStatus(
                                ownOrganisation.getGrantOfferLetterStatus(),
                                isLeadPartner
                        ),
                        statusAccessor.canAccessGrantOfferLetterSection(resolve(organisationRequest))
                );
            case PROJECT_SETUP_COMPLETE:
                SectionStatus projectSetupCompleteStatus = sectionStatus.projectSetupCompleteStatus(ownOrganisation.getProjectSetupCompleteStatus());
                return new SetupStatusStageViewModel(stage,
                        stage.getShortName(),
                        "Once all tasks are complete Innovate UK will review your application.",
                        String.format("/project/%d/setup", project.getId()),
                        projectSetupCompleteStatus,
                        statusAccessor.canAccessProjectSetupCompleteSection(),
                        projectSetupCompleteStatus.equals(TICK) ? null : "awaiting-review"
                );
        }
        throw new IllegalArgumentException("Unknown enum type " + stage.name());
    }

    private boolean isLeadPartner(ProjectTeamStatusResource teamStatus, OrganisationResource organisation) {
        return teamStatus.getLeadPartnerStatus().getOrganisationId().equals(organisation.getId());
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

    private boolean allOtherPartnersProjectLocationStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForOtherPartners(status -> COMPLETE.equals(status.getPartnerProjectLocationStatus()));
    }

    private boolean allPartnersProjectLocationStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForAllPartners(status -> COMPLETE.equals(status.getPartnerProjectLocationStatus()));
    }
}