package org.innovateuk.ifs.project.status.populator;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.monitoringofficer.MonitoringOfficerService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
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
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class SetupStatusViewModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private SetupStatusViewModelPopulator populator;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private MonitoringOfficerService monitoringOfficerService;

    @Mock
    private BankDetailsRestService bankDetailsRestService;

    @Mock
    private StatusService statusService;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    private static final boolean monitoringOfficerExpected = true;

    private CompetitionResource competition = newCompetitionResource().withLocationPerPartner(false).build();
    private ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();
    private ProjectResourceBuilder projectBuilder = newProjectResource().withApplication(application);

    private ProjectResource project = projectBuilder.build();
    private OrganisationResource organisationResource = newOrganisationResource().build();
    private OrganisationResource partnerOrganisationResource = newOrganisationResource().build();

    private BankDetailsResource bankDetailsResource = newBankDetailsResource().build();
    private RestResult<BankDetailsResource> bankDetailsFoundResult = restSuccess(bankDetailsResource);
    private RestResult<BankDetailsResource> bankDetailsNotFoundResult = restFailure(notFoundError(BankDetailsResource.class, 123L));

    private MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
    private Optional<MonitoringOfficerResource> monitoringOfficerFoundResult = Optional.of(monitoringOfficer);
    private Optional<MonitoringOfficerResource> monitoringOfficerNotFoundResult = Optional.empty();

    private Map<String, SectionStatus> partnerStatusFlagChecks = new HashMap<>();

    private UserResource loggedInUser = newUserResource().withId(1L)
            .withFirstName("James")
            .withLastName("Watts")
            .withEmail("james.watts@email.co.uk")
            .withRolesGlobal(singletonList(Role.APPLICANT))
            .withUID("2aerg234-aegaeb-23aer").build();

    @Before
    public void setUpDefaults() {
        partnerStatusFlagChecks.put("projectDetailsStatus", SectionStatus.FLAG);
        partnerStatusFlagChecks.put("monitoringOfficerStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("bankDetailsStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("financeChecksStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("spendProfileStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("otherDocumentsStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("grantOfferLetterStatus", SectionStatus.EMPTY);
    }

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
    }

    @Test
    public void testViewProjectSetupStatus() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withFinanceChecksStatus(NOT_STARTED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());

    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedAsProjectManager() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectUserResource partnerUser = newProjectUserResource()
                .withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PARTNER)
                .build();

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(partnerUser));

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("otherDocumentsStatus", SectionStatus.FLAG));

        assertFalse(viewModel.isProjectComplete());
        assertTrue(viewModel.isProjectManager());
    }

    @Test
    public void testViewProjectSetupStatusForNonLeadPartnerWithFinanceContactNotSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1)).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmittedNotFinanceContact() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1)).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));
        assertEquals(viewModel.getFinanceChecksSection(), SectionAccess.ACCESSIBLE);

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmittedAsFinanceContact() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withBankDetailsStatus(COMPLETE)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1)).
                        build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(monitoringOfficerService.getMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerNotFoundResult);
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
        when(statusService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));
        assertEquals(SectionAccess.ACCESSIBLE, viewModel.getFinanceChecksSection());

        assertFalse(viewModel.isProjectComplete());
        assertFalse(viewModel.isProjectManager());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsLeadWhenPDSubmittedFCNotYetSubmittedAndPLRequiredAndNotYetSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.EMPTY));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndNotYetSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.EMPTY),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsLeadWhenPDSubmittedFCNotSubmittedAndPLRequiredAndSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("financeChecksStatus", SectionStatus.EMPTY));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsNonLeadWhenPDSubmittedFCNotYetSubmittedAndPLRequiredAndNotYetSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.EMPTY));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsNonLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndNotYetSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.EMPTY),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsNonLeadWhenPDSubmittedFCNotSubmittedAndPLRequiredAndSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("financeChecksStatus", SectionStatus.EMPTY));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsNonLeadWhenPDSubmittedFCSubmittedAndPLRequiredAndSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, FC = Finance Contact, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsNonLeadWhenPDSubmittedAndOnlyNonLeadFCSubmittedAndPLRequiredAndSubmitted() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.EMPTY),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWhenAwaitingProjectDetailsActionFromOtherPartners() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS),
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    // PD = Project Details, PL = Project Location
    @Test
    public void testViewProjectSetupStatusAsLeadWhenPLRequiredAndAwaitingPDActionFromOtherPartners() throws Exception {

        competition.setLocationPerPartner(true);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withPartnerProjectLocationStatus(COMPLETE)
                        .withFinanceChecksStatus(PENDING)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withPartnerProjectLocationStatus(ACTION_REQUIRED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS),
                Pair.of("monitoringOfficerStatus", SectionStatus.EMPTY));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithMonitoringOfficerAssigned() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withFinanceChecksStatus(PENDING).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS),
                Pair.of("bankDetailsStatus", SectionStatus.EMPTY));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithBankDetailsEntered() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(PENDING).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithAllBankDetailsCompleteOrNotRequired() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(PENDING).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(NOT_REQUIRED).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithQueryAwaitingResponseNonFinanceContact() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(ACTION_REQUIRED).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(NOT_REQUIRED).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.FLAG));

        assertFalse(viewModel.isProjectComplete());
        assertTrue(viewModel.isShowFinanceChecksPendingQueryWarning());
    }

    @Test
    public void testViewProjectSetupStatusWithQueryAwaitingResponseAsFinanceContact() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withBankDetailsStatus(COMPLETE)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(ACTION_REQUIRED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1)).
                        build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(monitoringOfficerService.getMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerFoundResult);
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
        when(statusService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.FLAG));

        assertFalse(viewModel.isProjectComplete());
        assertTrue(viewModel.isShowFinanceChecksPendingQueryWarning());
    }

    @Test
    public void testViewProjectSetupStatusWithAllFinanceChecksApproved() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithSpendProfile() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.FLAG));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithSpendProfilePartnerComplete() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.FLAG));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithSpendAwaitingApproval() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(PENDING).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithSpendApproved() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithGOLNotSent() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(NOT_STARTED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithGOLSent() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.FLAG));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithGOLReturned() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(PENDING).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithGOLApproved() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        SetupStatusViewModel viewModel = populator.populateViewModel(project.getId(), loggedInUser, "origin").get();
        assertStandardViewModelValuesCorrect(viewModel, monitoringOfficerExpected);

        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.TICK));

        assertTrue(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAsProjectManager() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("otherDocumentsStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAndRejectedAsProjectManager() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.REJECTED).build();
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

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("otherDocumentsStatus", SectionStatus.FLAG));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAndRejectedAsPartner() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.REJECTED).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("otherDocumentsStatus", SectionStatus.HOURGLASS));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedWithOtherDocumentsSubmittedAndApprovedAsPartner() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        project = newProjectResource().withApplication(application).withDocumentsSubmittedDate(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.APPROVED).build();
        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("otherDocumentsStatus", SectionStatus.TICK));

        assertFalse(viewModel.isProjectComplete());
    }

    @Test
    public void testViewProjectSetupStatusCollaborationAgreementNotNeeded() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withFinanceChecksStatus(NOT_STARTED)
                        .withIsLeadPartner(true)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .withFinanceChecksStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(asList(organisationResource));

        SetupStatusViewModel viewModel = performPopulateView(project.getId(), loggedInUser);

        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));

        assertEquals(false, viewModel.isCollaborationAgreementRequired());

        assertFalse(viewModel.isProjectComplete());

    }

    private SetupStatusViewModel performPopulateView(Long projectId, UserResource loggedInUser) throws Exception {
        return populator.populateViewModel(projectId, loggedInUser, "origin").get();
    }

    private void setupLookupProjectDetailsExpectations(Optional<MonitoringOfficerResource> monitoringOfficerResult, RestResult<BankDetailsResource> bankDetailsResult, ProjectTeamStatusResource teamStatus) {

        ProjectUserResource pmUser = newProjectUserResource()
                .withUser(loggedInUser.getId() + 1000L)
                .withOrganisation(organisationResource.getId())
                .withRole(PROJECT_MANAGER)
                .build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(monitoringOfficerService.getMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerResult);
        when(projectRestService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId())).thenReturn(restSuccess(organisationResource));
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRole(PARTNER).build(1));

        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(pmUser));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResult);
        when(statusService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);
        when(projectService.getPartnerOrganisationsForProject(project.getId())).thenReturn(asList(organisationResource, partnerOrganisationResource));
    }

    private void assertStandardViewModelValuesCorrect(SetupStatusViewModel viewModel, boolean existingMonitoringOfficerExpected) {
        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertEquals(organisationResource.getId(), viewModel.getOrganisationId());
        assertEquals(true, viewModel.isCollaborationAgreementRequired());

        if (existingMonitoringOfficerExpected) {
            assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        } else {
            assertEquals("", viewModel.getMonitoringOfficerName());
        }
    }

    private final void assertPartnerStatusFlagsCorrect(SetupStatusViewModel viewModel, Pair<String, SectionStatus>... expectedTrueFlags) {
        for (Pair<String, SectionStatus> section : expectedTrueFlags) {
            partnerStatusFlagChecks.replace(section.getLeft(), section.getRight());
        }
        assertStatuses(viewModel);
    }

    private void assertStatuses(SetupStatusViewModel viewModel) {
        assertTrue(partnerStatusFlagChecks.get("projectDetailsStatus") == viewModel.getProjectDetailsStatus());
        assertTrue(partnerStatusFlagChecks.get("monitoringOfficerStatus") == viewModel.getMonitoringOfficerStatus());
        assertTrue(partnerStatusFlagChecks.get("bankDetailsStatus") == viewModel.getBankDetailsStatus());
        assertTrue(partnerStatusFlagChecks.get("financeChecksStatus") == viewModel.getFinanceChecksStatus());
        assertTrue(partnerStatusFlagChecks.get("spendProfileStatus") == viewModel.getSpendProfileStatus());
        assertTrue(partnerStatusFlagChecks.get("otherDocumentsStatus") == viewModel.getOtherDocumentsStatus());
        assertTrue(partnerStatusFlagChecks.get("grantOfferLetterStatus") == viewModel.getGrantOfferLetterStatus());
    }
}
