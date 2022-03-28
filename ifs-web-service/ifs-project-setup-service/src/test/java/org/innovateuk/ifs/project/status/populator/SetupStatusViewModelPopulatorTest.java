package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionPostAwardServiceResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupPostAwardServiceRestService;
import org.innovateuk.ifs.competition.service.CompetitionThirdPartyConfigRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusStageViewModel;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusViewModel;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.sections.SectionStatus;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.*;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder.newProjectDocumentResource;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionStatus.*;
import static org.innovateuk.ifs.sections.SectionStatus.INCOMPLETE;
import static org.innovateuk.ifs.sections.SectionStatus.MO_ACTION_REQUIRED;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class SetupStatusViewModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private SetupStatusViewModelPopulator populator;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerService;

    @Mock
    private BankDetailsRestService bankDetailsRestService;

    @Mock
    private StatusService statusService;

    @Spy
    private SetupSectionStatus setupSectionStatus;

    @Mock
    private AsyncFuturesGenerator futuresGenerator;

    @Mock
    private NavigationUtils navigationUtils;

    @Mock
    private CompetitionSetupPostAwardServiceRestService competitionSetupPostAwardServiceRestService;

    @Mock
    private CompetitionThirdPartyConfigRestService competitionThirdPartyConfigRestService;

    private static final boolean monitoringOfficerExpected = true;

    private static final String liveProjectsLandingPageUrl = "https://ifs.local-dev/live-projects-landing-page";

    List<CompetitionDocumentResource> projectDocumentConfig =
            newCompetitionDocumentResource()
                    .withTitle("Risk Register", "Plan Document")
                    .build(2);

    String termsTemplate = "terms-template";
    GrantTermsAndConditionsResource grantTermsAndConditions =
            new GrantTermsAndConditionsResource("name", termsTemplate, 1);
    CompetitionResource competition = newCompetitionResource()
            .withTermsAndConditions(grantTermsAndConditions)
            .withProjectDocument(projectDocumentConfig)
            .withProjectSetupStages(new ArrayList<>(EnumSet.allOf(ProjectSetupStage.class)))
            .build();
    private ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();
    private ProjectResourceBuilder projectBuilder = newProjectResource().withApplication(application);

    private ProjectResource project = projectBuilder
            .withCompetition(competition.getId())
            .withProjectState(LIVE)
            .build();
    private OrganisationResource organisationResource = newOrganisationResource().build();
    private OrganisationResource partnerOrganisationResource = newOrganisationResource().build();

    private BankDetailsResource bankDetailsResource = newBankDetailsResource().build();
    private RestResult<BankDetailsResource> bankDetailsFoundResult = restSuccess(bankDetailsResource);
    private RestResult<BankDetailsResource> bankDetailsNotFoundResult = restFailure(notFoundError(BankDetailsResource.class, 123L));

    private MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().withId(88L).build();
    private RestResult<MonitoringOfficerResource> monitoringOfficerFoundResult = restSuccess(monitoringOfficer);
    private RestResult<MonitoringOfficerResource> monitoringOfficerNotFoundResult = restFailure(HttpStatus.NOT_FOUND);

    private UserResource loggedInUser = newUserResource()
            .withId(1L)
            .withFirstName("James")
            .withLastName("Watts")
            .withEmail("james.watts@email.co.uk")
            .withRoleGlobal(Role.APPLICANT)
            .withUid("2aerg234-aegaeb-23aer").build();

    String thirdPartyTncLabel = "3rd party tnc label";
    String thirdPartyTncGuidance = "3rd party tnc guidance";
    String thirdPartyCostGuidanceUrl = "https://www.google.com";
    CompetitionThirdPartyConfigResource thirdPartyConfig = newCompetitionThirdPartyConfigResource()
            .withTermsAndConditionsLabel(thirdPartyTncLabel)
            .withTermsAndConditionsGuidance(thirdPartyTncGuidance)
            .withProjectCostGuidanceUrl(thirdPartyCostGuidanceUrl)
            .build();

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGenerator);
    }

    @Test
    public void viewProjectSetupStatus() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withFinanceChecksStatus(NOT_STARTED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
        assertEquals(false, viewModel.isProjectManager());
        assertEquals(false, viewModel.isProjectFinanceContact());
        assertCompetitionPostAwardService(viewModel, PostAwardService.CONNECT);
    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedAsProjectManager() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectUserResource partnerUser = newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_PARTNER)
                .build();

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(partnerUser));

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, FLAG);
    }

    @Test
    public void viewProjectSetupStatusForNonLeadPartnerWithFinanceContactNotSubmitted() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmitted() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmittedNotFinanceContact() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmittedAsFinanceContact() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withBankDetailsStatus(COMPLETE)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1))
                .withProjectState(LIVE).
                        build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationResource.getId());
        when(projectRestService.existsOnApplication(project.getId(), organisationResource.getId())).thenReturn(restSuccess(true));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerNotFoundResult);
        when(monitoringOfficerService.isMonitoringOfficerOnProject(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(false));
        when(projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(Arrays.asList(newProjectUserResource()
                        .withUser(loggedInUser.getId())
                        .withOrganisation(organisationResource.getId())
                        .withRole(PROJECT_FINANCE_CONTACT).build(),
                newProjectUserResource()
                        .withUser(loggedInUser.getId())
                        .withOrganisation(organisationResource.getId())
                        .withRole(PROJECT_PARTNER).build()));

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_FINANCE_CONTACT).build())));

        ProjectUserResource partnerUser = newProjectUserResource()
                .withUser(loggedInUser.getId() + 1000L)
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_PARTNER).build();
        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(partnerUser));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsFoundResult);
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);

        when(projectService.isProjectManager(loggedInUser.getId(), project.getId())).thenReturn(false);
        when(projectService.isProjectFinanceContact(loggedInUser.getId(), project.getId())).thenReturn(true);
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(thirdPartyConfig));
        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.IFS_POST_AWARD);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);

        assertStageAccess(viewModel, FINANCE_CHECKS, ACCESSIBLE);
        assertEquals(false, viewModel.isProjectManager());
        assertEquals(true, viewModel.isProjectFinanceContact());
        assertCompetitionPostAwardService(viewModel, PostAwardService.IFS_POST_AWARD);
        assertEquals(liveProjectsLandingPageUrl, viewModel.getLiveProjectsLandingPageUrl());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsLeadWhenPDSubmittedFCNotYetSubmittedAndPLRequiredAndNotYetSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndNotYetSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsLeadWhenPDSubmittedFCNotSubmittedAndPLRequiredAndSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, EMPTY);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, SectionStatus.HOURGLASS);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsNonLeadWhenPDSubmittedFCNotYetSubmittedAndPLRequiredAndNotYetSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsNonLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndNotYetSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, SectionStatus.HOURGLASS);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsNonLeadWhenPDSubmittedFCNotSubmittedAndPLRequiredAndSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, EMPTY);

    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsNonLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsNonLeadWhenPDSubmittedAndOnlyNonLeadFCSubmittedAndPLRequiredAndSubmitted() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);


    }

    @Test
    public void viewProjectSetupStatusWhenAwaitingProjectDetailsActionFromOtherPartners() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
    }

    // PD = Project Details, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsLeadWhenPLRequiredAndAwaitingPDActionFromOtherPartners() {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);

    }

    @Test
    public void viewProjectSetupStatusWithMonitoringOfficerAssigned() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withFinanceChecksStatus(PENDING).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build())
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
        assertStageStatus(viewModel, BANK_DETAILS, EMPTY);
    }

    @Test
    public void viewProjectSetupStatusWithBankDetailsEntered() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(PENDING).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build())
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithAllBankDetailsCompleteOrNotRequired() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(PENDING).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(NOT_REQUIRED).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithQueryAwaitingResponseNonFinanceContact() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(ACTION_REQUIRED).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(NOT_REQUIRED).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, FLAG);

        assertStageStatusOverride(viewModel, FINANCE_CHECKS, "pending-query");
    }

    @Test
    public void viewProjectSetupStatusWithQueryAwaitingResponseAsFinanceContact() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withBankDetailsStatus(COMPLETE)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1))
                .withProjectState(LIVE).
                        build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationResource.getId());
        when(projectRestService.existsOnApplication(project.getId(), organisationResource.getId())).thenReturn(restSuccess(true));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerFoundResult);
        when(monitoringOfficerService.isMonitoringOfficerOnProject(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(false));
        when(projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(Arrays.asList(newProjectUserResource()
                        .withUser(loggedInUser.getId())
                        .withOrganisation(organisationResource.getId())
                        .withRole(PROJECT_FINANCE_CONTACT).build(),
                newProjectUserResource()
                        .withUser(loggedInUser.getId())
                        .withOrganisation(organisationResource.getId())
                        .withRole(PROJECT_PARTNER).build()));

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId() + 1000L)
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_FINANCE_CONTACT).build())));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsFoundResult);
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);

        when(projectService.isProjectManager(loggedInUser.getId(), project.getId())).thenReturn(true);
        when(projectService.isProjectFinanceContact(loggedInUser.getId(), project.getId())).thenReturn(true);
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(thirdPartyConfig));
        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.IFS_POST_AWARD);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, FLAG);

        assertStageStatusOverride(viewModel, FINANCE_CHECKS, "pending-query");
        assertEquals(true, viewModel.isProjectManager());
        assertEquals(true, viewModel.isProjectFinanceContact());
        assertCompetitionPostAwardService(viewModel, PostAwardService.IFS_POST_AWARD);
        assertEquals(liveProjectsLandingPageUrl, viewModel.getLiveProjectsLandingPageUrl());
    }

    @Test
    public void viewProjectSetupStatusWithAllFinanceChecksApproved() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);


    }

    @Test
    public void viewProjectSetupStatusWithSpendProfile() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWithSpendProfilePartnerComplete() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWithSpendAwaitingApproval() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(PENDING).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithSpendApproved() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, TICK);
    }

    @Test
    public void viewProjectSetupStatusWithGOLNotSent() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(NOT_STARTED).
                        withOrganisationId(organisationResource.getId()).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, TICK);
        assertStageStatus(viewModel, GRANT_OFFER_LETTER, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithGOLSent() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withProjectTeamStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(ACTION_REQUIRED).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE)
                .withProjectManagerAssigned(true)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, TICK);
        assertStageStatus(viewModel, GRANT_OFFER_LETTER, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWithGOLReturned() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(PENDING).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, TICK);
        assertStageStatus(viewModel, GRANT_OFFER_LETTER, HOURGLASS);
    }

    @Test
    public void viewProjectSetupStatusWithGOLApproved() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withProjectTeamStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(COMPLETE).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = populator.populateViewModel(project.getId(), loggedInUser);
        assertStandardViewModelValuesCorrect(viewModel, monitoringOfficerExpected);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, TICK);
        assertStageStatus(viewModel, SPEND_PROFILE, TICK);
        assertStageStatus(viewModel, GRANT_OFFER_LETTER, TICK);

    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAsProjectManager() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).withProjectState(LIVE).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, EMPTY);
    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAndRejectedAsProjectManager() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).withProjectState(LIVE).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(loggedInUser.getId(), loggedInUser.getId())
                .withOrganisation(organisationResource.getId(), organisationResource.getId())
                .withRole(PROJECT_PARTNER, PROJECT_MANAGER)
                .build(2);

        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_MANAGER).build())));

        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.CONNECT);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAndRejectedAsPartner() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).withProjectState(LIVE).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, EMPTY);
    }

    @Test
    public void viewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAndApprovedAsPartner() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).withProjectState(LIVE).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, EMPTY);
    }

    @Test
    public void viewProjectSetupStatusWhenAllDocumentsNotYetUploaded() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        project = newProjectResource().withApplication(application).withProjectState(LIVE).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_MANAGER).build())));

        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.CONNECT);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWhenAllDocumentsUploadedButNotSubmitted() {

        SetupStatusViewModel viewModel = performDocumentsTest(DocumentStatus.UPLOADED, DocumentStatus.UPLOADED);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWhenOnlyOneDocumentSubmitted() {

        SetupStatusViewModel viewModel = performDocumentsTest(DocumentStatus.UPLOADED, DocumentStatus.SUBMITTED);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWhenAllDocumentsSubmitted() {

        SetupStatusViewModel viewModel = performDocumentsTest(DocumentStatus.SUBMITTED, DocumentStatus.SUBMITTED);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, HOURGLASS);
    }

    @Test
    public void viewMODocumentsStatusWhenNonSubmitted() {

        SetupStatusViewModel viewModel = performDocumentsForMOViewTest(DocumentStatus.UNSET, DocumentStatus.UNSET);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, DOCUMENTS, INCOMPLETE);
    }

    @Test
    public void viewMODocumentsStatusWhenOneSubmitted() {

        SetupStatusViewModel viewModel = performDocumentsForMOViewTest(DocumentStatus.SUBMITTED, DocumentStatus.UNSET);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, DOCUMENTS, MO_ACTION_REQUIRED);
    }

    @Test
    public void viewMODocumentsStatusWhenAllSubmitted() {

        SetupStatusViewModel viewModel = performDocumentsForMOViewTest(DocumentStatus.SUBMITTED, DocumentStatus.SUBMITTED);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, DOCUMENTS, MO_ACTION_REQUIRED);
    }

    @Test
    public void viewMODocumentsStatusWhenOneRejected() {

        SetupStatusViewModel viewModel = performDocumentsForMOViewTest(DocumentStatus.REJECTED, DocumentStatus.SUBMITTED);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, DOCUMENTS, MO_ACTION_REQUIRED);
    }

    @Test
    public void viewMODocumentsStatusWhenOneRejectedOneApproved() {

        SetupStatusViewModel viewModel = performDocumentsForMOViewTest(DocumentStatus.REJECTED, DocumentStatus.APPROVED);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, DOCUMENTS, INCOMPLETE);
    }

    @Test
    public void viewMODocumentsStatusWhenAllApproved() {

        SetupStatusViewModel viewModel = performDocumentsForMOViewTest(DocumentStatus.APPROVED, DocumentStatus.APPROVED);

        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, DOCUMENTS, TICK);
    }

    @Test
    public void viewProjectSetupStatusWhenAnyDocumentRejected() {

        SetupStatusViewModel viewModel = performDocumentsTest(DocumentStatus.REJECTED, DocumentStatus.SUBMITTED);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, FLAG);
    }

    @Test
    public void viewProjectSetupStatusWhenAllApproved() {

        SetupStatusViewModel viewModel = performDocumentsTest(DocumentStatus.APPROVED, DocumentStatus.APPROVED);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, DOCUMENTS, TICK);
    }

    private SetupStatusViewModel performDocumentsTest(DocumentStatus document1Status, DocumentStatus document2Status) {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        List<ProjectDocumentResource> projectDocumentResources = newProjectDocumentResource()
                .withStatus(document1Status, document2Status)
                .withCompetitionDocument(projectDocumentConfig.get(0), projectDocumentConfig.get(1))
                .build(2);

        project = newProjectResource()
                .withProjectState(LIVE)
                .withApplication(application)
                .withProjectDocuments(projectDocumentResources)
                .build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_MANAGER).build())));

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        return viewModel;
    }

    private SetupStatusViewModel performDocumentsForMOViewTest(DocumentStatus document1Status, DocumentStatus document2Status) {

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource().build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .withProjectManagerAssigned(true)
                .build();

        List<ProjectDocumentResource> projectDocumentResources = newProjectDocumentResource()
                .withStatus(document1Status, document2Status)
                .withCompetitionDocument(projectDocumentConfig.get(0), projectDocumentConfig.get(1))
                .build(2);

        project = newProjectResource()
                .withProjectState(LIVE)
                .withApplication(application)
                .withCompetition(competition.getId())
                .withProjectDocuments(projectDocumentResources)
                .withMonitoringOfficerUser(monitoringOfficer.getId())
                .withProjectUsers(singletonList(newProjectUserResource().withRole(ProjectParticipantRole.MONITORING_OFFICER).build().getId()))
                .build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(monitoringOfficerService.isMonitoringOfficerOnProject(project.getId(), monitoringOfficer.getId())).thenReturn(restSuccess(true));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(projectRestService.getOrganisationByProjectAndUser(project.getId(), monitoringOfficer.getId())).thenReturn(restSuccess(newOrganisationResource().build()));
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(organisationResource);
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(competitionThirdPartyConfigResource));

        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(restSuccess(monitoringOfficer));
        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.CONNECT);

        UserResource loggedInMO = newUserResource().withId(monitoringOfficer.getId()).withRoleGlobal(MONITORING_OFFICER).build();

        return performPopulateView(project.getId(), loggedInMO);
    }

    @Test
    public void viewProjectSetupStatusCollaborationAgreementNotNeeded() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .build();

        List<CompetitionDocumentResource> competitionDocuments = CompetitionDocumentResourceBuilder.newCompetitionDocumentResource()
                .withTitle(COLLABORATION_AGREEMENT_TITLE, "Other Document")
                .withCompetition(competition.getId())
                .build(2);

        competition.setCompetitionDocuments(competitionDocuments);

        List<ProjectDocumentResource> projectDocuments = newProjectDocumentResource()
                .withCompetitionDocument(competitionDocuments.get(1))
                .withStatus(DocumentStatus.APPROVED)
                .build(1);

        project = newProjectResource()
                .withApplication(application)
                .withProjectDocuments(projectDocuments)
                .withCompetition(competition.getId())
                .withProjectState(LIVE)
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(asList(organisationResource));

        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.CONNECT);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertFalse(viewModel.isMonitoringOfficer());
    }

    @Test
    public void viewFinanceChecksStatusForMo() {
        String termsTemplate = "terms-template";
        GrantTermsAndConditionsResource grantTermsAndConditions =
                new GrantTermsAndConditionsResource("name", termsTemplate, 1);
        CompetitionResource sbriCompetition = newCompetitionResource()
                .withTermsAndConditions(grantTermsAndConditions)
                .withFundingType(FundingType.GRANT)
                .withProjectDocument(projectDocumentConfig)
                .withProjectSetupStages(new ArrayList<>(EnumSet.allOf(ProjectSetupStage.class)))
                .build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(ProjectActivityStates.INCOMPLETE).
                        withBankDetailsStatus(ProjectActivityStates.INCOMPLETE).
                        withFinanceChecksStatus(ProjectActivityStates.INCOMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withProjectSetupCompleteStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(ProjectActivityStates.INCOMPLETE).
                        withBankDetailsStatus(ProjectActivityStates.INCOMPLETE).
                        build(1))
                .withProjectState(LIVE).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus, true, sbriCompetition);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertTrue(viewModel.isMonitoringOfficer());

        Optional<SetupStatusStageViewModel> stageViewModel = viewModel.getStages().stream()
                .filter(setupStatusStageViewModel -> setupStatusStageViewModel.getStage() == FINANCE_CHECKS)
                .findAny();

        assertTrue(stageViewModel.isPresent());
        assertEquals(FINANCE_CHECKS, stageViewModel.get().getStage());
        assertEquals(String.format("/project/%d/finance-check/read-only", project.getId()), stageViewModel.get().getUrl());
        assertEquals(ACCESSIBLE, stageViewModel.get().getAccess());
    }

    private SetupStatusViewModel performPopulateView(Long projectId, UserResource loggedInUser) {
        return populator.populateViewModel(projectId, loggedInUser);
    }

    private void setupLookupProjectDetailsExpectations(RestResult<MonitoringOfficerResource> monitoringOfficerResult,
                                                       RestResult<BankDetailsResource> bankDetailsResult, ProjectTeamStatusResource teamStatus) {
        setupLookupProjectDetailsExpectations(monitoringOfficerResult, bankDetailsResult, teamStatus, false, competition);
    }

    private void setupLookupProjectDetailsExpectations(RestResult<MonitoringOfficerResource> monitoringOfficerResult,
                                                       RestResult<BankDetailsResource> bankDetailsResult,
                                                       ProjectTeamStatusResource teamStatus, boolean isUserMoOnProject, CompetitionResource competitionResource) {

        ProjectUserResource pmUser = newProjectUserResource()
                .withUser(loggedInUser.getId() + 1000L)
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_MANAGER)
                .build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationResource.getId());
        when(projectRestService.existsOnApplication(project.getId(), organisationResource.getId())).thenReturn(restSuccess(true));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competitionResource));
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerResult);
        when(monitoringOfficerService.isMonitoringOfficerOnProject(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(isUserMoOnProject));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(organisationResource);
        when(projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(thirdPartyConfig));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_PARTNER).build(1));

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(pmUser));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResult);
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(asList(organisationResource, partnerOrganisationResource));

        when(projectService.isProjectManager(loggedInUser.getId(), project.getId())).thenReturn(false);
        when(projectService.isProjectFinanceContact(loggedInUser.getId(), project.getId())).thenReturn(false);

        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.CONNECT);
    }

    @Test
    public void viewSpendProfileRejectedStatusAsMO() {
        ReflectionTestUtils.setField(populator, "isMOSpendProfileUpdateEnabled", false);
        SetupStatusViewModel viewModel = setSpendProfileProjectSetup(LEAD_ACTION_REQUIRED);
        assertTrue(viewModel.isMonitoringOfficer());
        assertStageStatus(viewModel, SPEND_PROFILE, LEAD_ACTION_FLAG);
    }

    @Test
    public void viewImprovedSpendProfileRejectedStatusAsMO() {
        ReflectionTestUtils.setField(populator, "isMOSpendProfileUpdateEnabled", true);
        SetupStatusViewModel viewModel = setSpendProfileProjectSetup(LEAD_ACTION_REQUIRED);
        assertTrue(viewModel.isMonitoringOfficer());
        assertStageStatus(viewModel, SPEND_PROFILE, INCOMPLETE);
    }

    @Test
    public void viewImprovedSpendProfileApprovedStatusAsMO() {
        ReflectionTestUtils.setField(populator, "isMOSpendProfileUpdateEnabled", true);
        SetupStatusViewModel viewModel = setSpendProfileProjectSetup(COMPLETE);
        assertTrue(viewModel.isMonitoringOfficer());
        assertStageStatus(viewModel, SPEND_PROFILE, TICK);
    }

    @Test
    public void viewImprovedSpendProfileAwaitingReviewStatusAsMO() {
        ReflectionTestUtils.setField(populator, "isMOSpendProfileUpdateEnabled", true);
        SetupStatusViewModel viewModel = setSpendProfileProjectSetup(PENDING);
        assertTrue(viewModel.isMonitoringOfficer());
        assertStageStatus(viewModel, SPEND_PROFILE, MO_ACTION_REQUIRED);
    }

    @Test
    public void viewImprovedSpendProfileNotYetSubmittedStatusAsMO() {
        ReflectionTestUtils.setField(populator, "isMOSpendProfileUpdateEnabled", true);
        SetupStatusViewModel viewModel = setSpendProfileProjectSetup(NOT_STARTED);
        assertTrue(viewModel.isMonitoringOfficer());
        assertStageStatus(viewModel, SPEND_PROFILE, INCOMPLETE);
    }

    private SetupStatusViewModel setSpendProfileProjectSetup(ProjectActivityStates projectActivityState) {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(projectActivityState)
                        .withProjectSetupCompleteStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .withProjectState(LIVE)
                .withProjectManagerAssigned(true)
                .build();

        project = newProjectResource()
                .withProjectState(LIVE)
                .withApplication(application)
                .withCompetition(competition.getId())
                .withMonitoringOfficerUser(monitoringOfficer.getId())
                .withProjectUsers(singletonList(newProjectUserResource().withRole(ProjectParticipantRole.MONITORING_OFFICER).build().getId()))
                .build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(monitoringOfficerService.isMonitoringOfficerOnProject(project.getId(), monitoringOfficer.getId())).thenReturn(restSuccess(true));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(projectRestService.getOrganisationByProjectAndUser(project.getId(), monitoringOfficer.getId())).thenReturn(restSuccess(newOrganisationResource().build()));
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(organisationResource);
        when(competitionThirdPartyConfigRestService.findOneByCompetitionId(competition.getId())).thenReturn(restSuccess(thirdPartyConfig));
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(restSuccess(monitoringOfficer));
        setupCompetitionPostAwardServiceExpectations(project, PostAwardService.CONNECT);

        UserResource loggedInMO = newUserResource().withId(monitoringOfficer.getId()).withRoleGlobal(MONITORING_OFFICER).build();
        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInMO);
        return viewModel;
    }

    private void setupCompetitionPostAwardServiceExpectations(ProjectResource project, PostAwardService postAwardService) {
        CompetitionPostAwardServiceResource competitionPostAwardServiceResource = CompetitionPostAwardServiceResourceBuilder.newCompetitionPostAwardServiceResource()
                .withCompetitionId(project.getCompetition())
                .withPostAwardService(postAwardService)
                .build();
        when(navigationUtils.getLiveProjectsLandingPageUrl()).thenReturn(liveProjectsLandingPageUrl);
        when(competitionSetupPostAwardServiceRestService.getPostAwardService(project.getCompetition())).thenReturn(restSuccess(competitionPostAwardServiceResource));
    }

    private void assertStandardViewModelValuesCorrect(SetupStatusViewModel viewModel, boolean existingMonitoringOfficerExpected) {
        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getCompetitionName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
    }

    private void assertStageStatus(SetupStatusViewModel viewModel, ProjectSetupStage stage, SectionStatus status) {
        SetupStatusStageViewModel found = viewModel.getStages().stream().filter(stageViewModel -> stageViewModel.getStage() == stage).findFirst().get();
        assertEquals(status, found.getStatus());
    }

    private void assertStageStatusOverride(SetupStatusViewModel viewModel, ProjectSetupStage stage, String override) {
        SetupStatusStageViewModel found = viewModel.getStages().stream().filter(stageViewModel -> stageViewModel.getStage() == stage).findFirst().get();
        assertEquals(found.getStatusOverride(), override);
    }

    private void assertStageAccess(SetupStatusViewModel viewModel, ProjectSetupStage stage, SectionAccess access) {
        SetupStatusStageViewModel found = viewModel.getStages().stream().filter(stageViewModel -> stageViewModel.getStage() == stage).findFirst().get();
        assertEquals(found.getAccess(), access);
    }

    private void assertCompetitionPostAwardService(SetupStatusViewModel viewModel, PostAwardService postAwardService) {
        assertEquals(postAwardService, viewModel.getPostAwardService());
    }
}
