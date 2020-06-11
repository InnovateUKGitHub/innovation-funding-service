package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionPostAwardServiceResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupPostAwardServiceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder.newProjectDocumentResource;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionStatus.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertEquals;
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
    private AsyncFuturesGenerator futuresGeneratorMock;

    @Mock
    private CompetitionSetupPostAwardServiceRestService competitionSetupPostAwardServiceRestService;

    private static final boolean monitoringOfficerExpected = true;

    List<CompetitionDocumentResource> projectDocumentConfig =
            newCompetitionDocumentResource()
                    .withTitle("Risk Register", "Plan Document")
                    .build(2);

    private CompetitionResource competition = newCompetitionResource()
            .withLocationPerPartner(false)
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

    private MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
    private RestResult<MonitoringOfficerResource> monitoringOfficerFoundResult = restSuccess(monitoringOfficer);
    private RestResult<MonitoringOfficerResource> monitoringOfficerNotFoundResult = restFailure(HttpStatus.NOT_FOUND);

    private UserResource loggedInUser = newUserResource().withId(1L)
            .withFirstName("James")
            .withLastName("Watts")
            .withEmail("james.watts@email.co.uk")
            .withRolesGlobal(singletonList(Role.APPLICANT))
            .withUID("2aerg234-aegaeb-23aer").build();

    private CompetitionPostAwardServiceResource competitionPostAwardServiceResource = CompetitionPostAwardServiceResourceBuilder.newCompetitionPostAwardServiceResource()
            .withCompetitionId(competition.getId())
            .withPostAwardService(PostAwardService.CONNECT)
            .build();

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
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
                .withRole(PARTNER)
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
                        .withRole(FINANCE_CONTACT).build(),
                newProjectUserResource()
                        .withUser(loggedInUser.getId())
                        .withOrganisation(organisationResource.getId())
                        .withRole(PARTNER).build()));

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(FINANCE_CONTACT).build())));

        ProjectUserResource partnerUser = newProjectUserResource()
                .withUser(loggedInUser.getId() + 1000L)
                .withOrganisation(organisationResource.getId())
                .withRole(PARTNER).build();
        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(partnerUser));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsFoundResult);
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);

        setupCompetitionPostAwardServiceExpectations(competition, project, loggedInUser);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, HOURGLASS);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, HOURGLASS);

        assertStageAccess(viewModel, FINANCE_CHECKS, ACCESSIBLE);
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void viewProjectSetupStatusAsLeadWhenPDSubmittedFCNotYetSubmittedAndPLRequiredAndNotYetSubmitted() {

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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

        competition.setLocationPerPartner(true);

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
                        .withRole(FINANCE_CONTACT).build(),
                newProjectUserResource()
                        .withUser(loggedInUser.getId())
                        .withOrganisation(organisationResource.getId())
                        .withRole(PARTNER).build()));

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId() + 1000L)
                .withOrganisation(organisationResource.getId())
                .withRole(FINANCE_CONTACT).build())));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsFoundResult);
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);

        setupCompetitionPostAwardServiceExpectations(competition, project, loggedInUser);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertStageStatus(viewModel, PROJECT_DETAILS, TICK);
        assertStageStatus(viewModel, ProjectSetupStage.MONITORING_OFFICER, TICK);
        assertStageStatus(viewModel, BANK_DETAILS, TICK);
        assertStageStatus(viewModel, FINANCE_CHECKS, FLAG);

        assertStageStatusOverride(viewModel, FINANCE_CHECKS, "pending-query");
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
                .withRole(PARTNER, PROJECT_MANAGER)
                .build(2);

        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of((newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_MANAGER).build())));

        setupCompetitionPostAwardServiceExpectations(competition, project, loggedInUser);

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

        setupCompetitionPostAwardServiceExpectations(competition, project, loggedInUser);

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

        setupCompetitionPostAwardServiceExpectations(competition, project, loggedInUser);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        return viewModel;
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

        setupCompetitionPostAwardServiceExpectations(competition, project, loggedInUser);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertFalse(viewModel.isMonitoringOfficer());
    }

    private SetupStatusViewModel performPopulateView(Long projectId, UserResource loggedInUser) {
        return populator.populateViewModel(projectId, loggedInUser);
    }

    private void setupLookupProjectDetailsExpectations(RestResult<MonitoringOfficerResource> monitoringOfficerResult, RestResult<BankDetailsResource> bankDetailsResult, ProjectTeamStatusResource teamStatus) {

        ProjectUserResource pmUser = newProjectUserResource()
                .withUser(loggedInUser.getId() + 1000L)
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_MANAGER)
                .build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(organisationResource.getId());
        when(projectRestService.existsOnApplication(project.getId(), organisationResource.getId())).thenReturn(restSuccess(true));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(competition));
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerResult);
        when(monitoringOfficerService.isMonitoringOfficerOnProject(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(false));
        when(projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PARTNER).build(1));

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(pmUser));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResult);
        when(statusService.getProjectTeamStatus(eq(project.getId()), any(Optional.class))).thenReturn(teamStatus);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(asList(organisationResource, partnerOrganisationResource));

        setupCompetitionPostAwardServiceExpectations(competition, project, loggedInUser);
    }

    private void setupCompetitionPostAwardServiceExpectations(CompetitionResource competition, ProjectResource project, UserResource loggedInUser) {
        when(competitionSetupPostAwardServiceRestService.getPostAwardService(competition.getId())).thenReturn(restSuccess(competitionPostAwardServiceResource));
        when(projectService.isProjectManager(loggedInUser.getId(), project.getId())).thenReturn(false);
        when(projectService.isProjectFinanceContact(loggedInUser.getId(), project.getId())).thenReturn(false);
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
}
