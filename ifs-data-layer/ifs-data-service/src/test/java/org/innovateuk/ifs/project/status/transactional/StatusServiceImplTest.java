package org.innovateuk.ifs.project.status.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.*;
import org.innovateuk.ifs.project.core.mapper.ProjectUserMapper;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.PartnerOrganisationService;
import org.innovateuk.ifs.project.core.util.ProjectUsersHelper;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentBuilder.newCompetitionDocument;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentBuilder.newProjectDocument;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class StatusServiceImplTest extends BaseServiceUnitTest<StatusService> {

    private Application application;
    private Competition competition;
    private Role partnerRole;
    private User u;
    private List<PartnerOrganisation> po;
    private List<ProjectUserResource> puResource;
    private List<ProjectUser> pu;
    private Organisation o;
    private Project project;
    private Project p;
    private BankDetails bankDetails;
    private SpendProfile spendProfile;
    private ProjectProcess projectProcess;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplierMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private ProjectUserRepository projectUserRepositoryMock;

    @Mock
    private SpendProfileRepository spendProfileRepositoryMock;

    @Mock
    private BankDetailsRepository bankDetailsRepositoryMock;

    @Mock
    private MonitoringOfficerService monitoringOfficerServiceMock;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    private ApplicationFinanceMapper applicationFinanceMapperMock;

    @Mock
    private ProjectUserMapper projectUserMapperMock;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private ProjectUsersHelper projectUsersHelperMock;

    @Mock
    private SpendProfileService spendProfileServiceMock;

    @Mock
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandlerMock;

    @Mock
    private GrantOfferLetterWorkflowHandler golWorkflowHandlerMock;

    @Mock
    private PartnerOrganisationRepository partnerOrganisationRepositoryMock;

    @Mock
    private PartnerOrganisationService partnerOrganisationServiceMock;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private EligibilityWorkflowHandler eligibilityWorkflowHandlerMock;

    @Mock
    private ViabilityWorkflowHandler viabilityWorkflowHandlerMock;

    @Mock
    private SpendProfileWorkflowHandler spendProfileWorkflowHandlerMock;

    @Mock
    private ProjectProcessRepository projectProcessRepositoryMock;

    @Before
    public void setUp() {

        Organisation organisation = newOrganisation().
                withOrganisationType(OrganisationTypeEnum.BUSINESS).
                build();

        Role leadApplicantRole = Role.LEADAPPLICANT;

        long userId = 7L;
        User user = newUser().
                withId(userId).
                build();

        ProcessRole leadApplicantProcessRole = newProcessRole().
                withOrganisationId(organisation.getId()).
                withRole(leadApplicantRole).
                withUser(user).
                build();

        ProjectUser leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisation).
                withRole(PROJECT_PARTNER).
                withUser(user).
                build();

        competition = newCompetition()
                .withCompetitionDocuments(singletonList(newCompetitionDocument().build()))
                .build();

        long applicationId = 456L;
        application = newApplication().
                withId(applicationId).
                withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withCompetition(competition).
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

        long projectId = 123L;

        List<ProjectDocument> projectDocuments = newProjectDocument()
                .withProject(project)
                .withStatus(DocumentStatus.APPROVED)
                .build(1);

        project = newProject().
                withId(projectId).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                withProjectDocuments(projectDocuments).
                build();

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        o = organisation;
        o.setOrganisationType(businessOrganisationType);

        partnerRole = Role.FINANCE_CONTACT;

        po = newPartnerOrganisation().
                withOrganisation(o).
                withLeadOrganisation(true).
                build(1);

        u = newUser().
                withEmailAddress("a@b.com").
                withFirstName("A").
                withLastName("B").
                build();

        pu = newProjectUser().
                withRole(PROJECT_FINANCE_CONTACT).
                withUser(u).
                withOrganisation(o).
                withInvite(newProjectUserInvite().
                        build()).
                build(1);

        p = newProject().
                withProjectUsers(pu).
                withApplication(application).
                withPartnerOrganisations(po).
                withDateSubmitted(ZonedDateTime.now()).
                withSpendProfileSubmittedDate(ZonedDateTime.now()).
                build();

        puResource = newProjectUserResource().
                withProject(p.getId()).
                withOrganisation(o.getId()).
                withRole(partnerRole.getId()).
                withRoleName(PROJECT_PARTNER.getName()).
                build(1);

        projectProcess = newProjectProcess()
                .withActivityState(LIVE)
                .build();

        bankDetails = newBankDetails().withOrganisation(o).withApproval(true).build();
        spendProfile = newSpendProfile().withOrganisation(o).withGeneratedDate(Calendar.getInstance()).withMarkedComplete(true).build();

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());
        when(projectProcessRepositoryMock.findOneByTargetId(any())).thenReturn(projectProcess);

        User internalUser = newUser().withRoles(singleton(COMP_ADMIN)).build();
        when(userRepositoryMock.findById(internalUser.getId())).thenReturn(Optional.of(internalUser));
        setLoggedInUser(newUserResource().withId(internalUser.getId()).withRolesGlobal(singletonList(COMP_ADMIN)).build());

        competition.setProjectStages(EnumSet.allOf(ProjectSetupStage.class).stream().map(stage -> new ProjectStages(competition, stage)).collect(Collectors.toList()));
    }

    @Override
    protected StatusService supplyServiceUnderTest() {
        return new StatusServiceImpl();
    }


    @Test
    public void getProjectConsortiumStatus() {
        /*
          Create 3 organisations:
          2 Business, 1 Academic
         */
        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        OrganisationType academicOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        List<Organisation> organisations = new ArrayList<>();
        Organisation leadOrganisation = organisationRepositoryMock.findById(application.getLeadOrganisationId()).get();
        leadOrganisation.setOrganisationType(businessOrganisationType);
        organisations.add(leadOrganisation);
        leadOrganisation.setOrganisationType(businessOrganisationType);

        Organisation partnerOrganisation1 = newOrganisation().withOrganisationType(businessOrganisationType).build();
        Organisation partnerOrganisation2 = newOrganisation().withOrganisationType(academicOrganisationType).build();

        organisations.add(partnerOrganisation1);
        organisations.add(partnerOrganisation2);

        /*
         * Create 3 users project partner roles for each of the 3 organisations above
         */
        List<User> users = newUser().build(3);
        List<ProjectUser> pu = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(users.get(0), users.get(1), users.get(2))
                .withOrganisation(organisations.get(0), organisations.get(1), organisations.get(2))
                .build(3);

        /*
         * Create a project with 3 Project Users from 3 different organisations with an associated application
         */
        Project p = newProject()
                .withProjectUsers(pu)
                .withApplication(application)
                .withAddress(newAddress().build())
                .withTargetStartDate(LocalDate.now())
                .build();

        /*
         * Create 3 bank detail records, one for each organisation
         */
        List<BankDetails> bankDetails = newBankDetails().withOrganisation(organisations.get(0), organisations.get(1), organisations.get(2)).build(3);

        /*
         * Build spend profile object for use with one of the partners
         */
        SpendProfile spendProfile = newSpendProfile().build();

        /*
         * Create Finance Check information for each Organisation
         */
        List<PartnerOrganisation> partnerOrganisations = PartnerOrganisationBuilder.newPartnerOrganisation()
                .withProject(p)
                .withOrganisation(leadOrganisation, partnerOrganisation1, partnerOrganisation2)
                .withLeadOrganisation(true, false, false)
                .withPostcode(null, "TW14 9QG", " ")
                .withLeadOrganisation(true, false, false)
                .build(3);

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));

        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), organisations.get(0).getId())).thenReturn(Optional.of(bankDetails.get(0)));

        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(0).getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(1).getId())).thenReturn(Optional.empty());
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), organisations.get(2).getId())).thenReturn(Optional.empty());

        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceSuccess(monitoringOfficer));

        when(organisationRepositoryMock.findById(organisations.get(0).getId())).thenReturn(Optional.of(organisations.get(0)));
        when(organisationRepositoryMock.findById(organisations.get(1).getId())).thenReturn(Optional.of(organisations.get(1)));
        when(organisationRepositoryMock.findById(organisations.get(2).getId())).thenReturn(Optional.of(organisations.get(2)));

        List<ApplicationFinance> applicationFinances = newApplicationFinance().build(3);
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(p.getApplication().getId(), organisations.get(0).getId())).thenReturn(applicationFinances.get(0));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(p.getApplication().getId(), organisations.get(1).getId())).thenReturn(applicationFinances.get(1));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(p.getApplication().getId(), organisations.get(2).getId())).thenReturn(applicationFinances.get(2));

        ApplicationFinanceResource applicationFinanceResource0 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(0).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(0))).thenReturn(applicationFinanceResource0);

        ApplicationFinanceResource applicationFinanceResource1 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(1).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(1))).thenReturn(applicationFinanceResource1);

        ApplicationFinanceResource applicationFinanceResource2 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(2).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(2))).thenReturn(applicationFinanceResource2);

        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(3);

        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(projectUserMapperMock.mapToResource(pu.get(1))).thenReturn(puResource.get(1));
        when(projectUserMapperMock.mapToResource(pu.get(2))).thenReturn(puResource.get(2));

        when(projectFinanceService.financeChecksDetails(p.getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(p.getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsNotRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(p.getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        partnerOrganisations.forEach(org ->
                when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(org.getProject().getId(),
                        org.getOrganisation().getId())).thenReturn(org));

        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(0).getProject().getId(), partnerOrganisations.get(0).getOrganisation().getId())).thenReturn(serviceSuccess(false));
        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(1).getProject().getId(), partnerOrganisations.get(1).getOrganisation().getId())).thenReturn(serviceSuccess(false));
        when(financeCheckServiceMock.isQueryActionRequired(partnerOrganisations.get(2).getProject().getId(), partnerOrganisations.get(2).getOrganisation().getId())).thenReturn(serviceSuccess(true));

        when(golWorkflowHandlerMock.getState(p)).thenReturn(GrantOfferLetterState.PENDING);
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ProjectPartnerStatusResource expectedLeadPartnerOrganisationStatus = newProjectPartnerStatusResource().
                withName(organisations.get(0).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(0).getOrganisationType().getId())).
                withOrganisationId(organisations.get(0).getId()).
                withProjectDetailsStatus(ACTION_REQUIRED).
                withProjectTeamStatus(ACTION_REQUIRED).
                withFinanceContactStatus(ACTION_REQUIRED).
                withPartnerProjectLocationStatus(ACTION_REQUIRED).
                withMonitoringOfficerStatus(NOT_STARTED).
                withBankDetailsStatus(PENDING).
                withFinanceChecksStatus(PENDING).
                withSpendProfileStatus(NOT_STARTED).
                withDocumentsStatus(ACTION_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED).
                withProjectSetupCompleteStatus(NOT_REQUIRED).
                withIsLeadPartner(true).
                build();

        List<ProjectPartnerStatusResource> expectedFullPartnerStatuses = newProjectPartnerStatusResource().
                withName(organisations.get(1).getName(), organisations.get(2).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(1).getOrganisationType().getId()),
                        OrganisationTypeEnum.getFromId(organisations.get(2).getOrganisationType().getId())).
                withOrganisationId(organisations.get(1).getId(), organisations.get(2).getId()).
                withProjectDetailsStatus(COMPLETE, ACTION_REQUIRED).
                withProjectTeamStatus(ACTION_REQUIRED, ACTION_REQUIRED).
                withFinanceContactStatus(ACTION_REQUIRED, ACTION_REQUIRED).
                withPartnerProjectLocationStatus(COMPLETE, ACTION_REQUIRED).
                withMonitoringOfficerStatus(NOT_REQUIRED, NOT_REQUIRED).
                withBankDetailsStatus(NOT_REQUIRED, NOT_STARTED).
                withFinanceChecksStatus(PENDING, ACTION_REQUIRED).
                withSpendProfileStatus(NOT_STARTED, NOT_STARTED).
                withDocumentsStatus(NOT_REQUIRED, NOT_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED, NOT_REQUIRED).
                withProjectSetupCompleteStatus(NOT_REQUIRED, NOT_REQUIRED).
                build(2);

        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatus).
                withPartnerStatuses(expectedFullPartnerStatuses).
                build();

        // try without filtering
        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.empty());
        assertTrue(result.isSuccess());
        assertEquals(expectedProjectTeamStatusResource, result.getSuccess());

        List<ProjectPartnerStatusResource> expectedPartnerStatusesFilteredOnNonLead = newProjectPartnerStatusResource().
                withName(organisations.get(2).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(2).getOrganisationType().getId())).
                withOrganisationId(organisations.get(2).getId()).
                withProjectDetailsStatus(ACTION_REQUIRED).
                withProjectTeamStatus(ACTION_REQUIRED).
                withFinanceContactStatus(ACTION_REQUIRED).
                withPartnerProjectLocationStatus(ACTION_REQUIRED).
                withMonitoringOfficerStatus(NOT_REQUIRED).
                withBankDetailsStatus(NOT_STARTED).
                withFinanceChecksStatus(ACTION_REQUIRED).
                withSpendProfileStatus(NOT_STARTED).
                withDocumentsStatus(NOT_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED).
                withProjectSetupCompleteStatus(NOT_REQUIRED).
                build(1);

        // try with filtering on a non-lead partner organisation
        ProjectTeamStatusResource expectedProjectTeamStatusResourceFilteredOnNonLead = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatus).
                withPartnerStatuses(expectedPartnerStatusesFilteredOnNonLead).
                build();

        ServiceResult<ProjectTeamStatusResource> resultWithNonLeadFilter = service.getProjectTeamStatus(p.getId(), Optional.of(users.get(2).getId()));
        assertTrue(resultWithNonLeadFilter.isSuccess());
        assertEquals(expectedProjectTeamStatusResourceFilteredOnNonLead, resultWithNonLeadFilter.getSuccess());

        // try with filtering on a lead partner organisation
        ProjectTeamStatusResource expectedProjectTeamStatusResourceFilteredOnLead = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatus).
                build();

        ServiceResult<ProjectTeamStatusResource> resultWithLeadFilter = service.getProjectTeamStatus(p.getId(), Optional.of(users.get(0).getId()));
        assertTrue(resultWithLeadFilter.isSuccess());
        assertEquals(expectedProjectTeamStatusResourceFilteredOnLead, resultWithLeadFilter.getSuccess());


        // test MO status is pending and not action required when project details submitted
        p.setTargetStartDate(LocalDate.now());
        p.setAddress(newAddress().build());
        competition.setLocationPerPartner(false);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(any(Project.class))).thenReturn(true);
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));

        ProjectPartnerStatusResource expectedLeadPartnerOrganisationStatusWhenPDSubmitted = newProjectPartnerStatusResource().
                withName(organisations.get(0).getName()).
                withOrganisationType(
                        OrganisationTypeEnum.getFromId(organisations.get(0).getOrganisationType().getId())).
                withOrganisationId(organisations.get(0).getId()).
                withProjectDetailsStatus(COMPLETE).
                withProjectTeamStatus(ACTION_REQUIRED).
                withFinanceContactStatus(ACTION_REQUIRED).
                withPartnerProjectLocationStatus(ACTION_REQUIRED).
                withMonitoringOfficerStatus(PENDING).
                withBankDetailsStatus(PENDING).
                withFinanceChecksStatus(PENDING).
                withSpendProfileStatus(NOT_STARTED).
                withDocumentsStatus(ACTION_REQUIRED).
                withGrantOfferStatus(NOT_REQUIRED).
                withProjectSetupCompleteStatus(NOT_REQUIRED).
                withIsLeadPartner(true).
                build();

        ProjectTeamStatusResource expectedProjectTeamStatusResourceWhenPSSubmitted = newProjectTeamStatusResource().
                withProjectLeadStatus(expectedLeadPartnerOrganisationStatusWhenPDSubmitted).
                withPartnerStatuses(expectedFullPartnerStatuses).
                build();

        ServiceResult<ProjectTeamStatusResource> resultForPsSubmitted = service.getProjectTeamStatus(p.getId(), Optional.empty());
        assertTrue(resultForPsSubmitted.isSuccess());
        assertEquals(expectedProjectTeamStatusResourceWhenPSSubmitted, resultForPsSubmitted.getSuccess());
    }

    @Test
    public void isGrantOfferLetterActionRequired() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(u).withOrganisation(o)
                .withInvite(newProjectUserInvite().build())
                .build(1);

        List<PartnerOrganisation> po = newPartnerOrganisation()
                .withOrganisation(o)
                .withLeadOrganisation(true)
                .build(1);


        Project p = newProject()
                .withProjectUsers(pu)
                .withApplication(application)
                .withPartnerOrganisations(po)
                .withDateSubmitted(ZonedDateTime.now())
                .withGrantOfferLetter(golFile).build();

        List<ProjectUserResource> puResource = newProjectUserResource()
                .withProject(p.getId()
                ).withOrganisation(o.getId())
                .withRole(partnerRole.getId())
                .withRoleName(PROJECT_PARTNER.getName())
                .build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(true).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(true).build();

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        when(golWorkflowHandlerMock.getState(p)).thenReturn(GrantOfferLetterState.SENT);
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccess().getLeadPartnerStatus().getGrantOfferLetterStatus()));
    }

    @Test
    public void isGrantOfferLetterIsPendingLeadPartner() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newProjectUserInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(true).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withDateSubmitted(ZonedDateTime.now()).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(o.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(true).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(true).build();

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));

        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsNotSent = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsNotSent.isSuccess() && PENDING.equals(resultWhenGolIsNotSent.getSuccess().getLeadPartnerStatus().getGrantOfferLetterStatus()));

        // Same flow but when GOL is in Ready To Approve state.
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(true);

        // Call the service again
        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsReadyToApprove = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsReadyToApprove.isSuccess() && PENDING.equals(resultWhenGolIsReadyToApprove.getSuccess().getLeadPartnerStatus().getGrantOfferLetterStatus()));
    }

    @Test
    public void isGrantOfferLetterIsPendingNonLeadPartner() {
        User u = newUser().withEmailAddress("a@b.com").build();

        OrganisationType businessOrganisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Organisation o = organisationRepositoryMock.findById(application.getLeadOrganisationId()).get();
        o.setOrganisationType(businessOrganisationType);

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        Organisation nonLeadOrg = newOrganisation().build();
        nonLeadOrg.setOrganisationType(businessOrganisationType);

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(nonLeadOrg).withInvite(newProjectUserInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(nonLeadOrg).withLeadOrganisation(false).build(1);
        Project p = spy(newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).withDateSubmitted(ZonedDateTime.now()).build());
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(nonLeadOrg.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        when(p.getLeadOrganisation()).thenReturn(Optional.of(newPartnerOrganisation().build()));

        BankDetails bankDetails = newBankDetails().withOrganisation(o).withApproval(true).build();
        SpendProfile spendProfile = newSpendProfile().withOrganisation(o).withMarkedComplete(true).build();

        when(p.getLeadOrganisation()).thenReturn(Optional.of(newPartnerOrganisation().build()));
        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), nonLeadOrg.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(anyLong(), anyLong())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), nonLeadOrg.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        GrantOfferLetterStateResource sentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.SENT, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(sentGrantOfferLetterState));

        // Same flow but when GOL is in Ready To Approve state.
        when(golWorkflowHandlerMock.isReadyToApprove(p)).thenReturn(true);

        when(projectFinanceService.financeChecksDetails(p.getId(), o.getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(p.getId(), nonLeadOrg.getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
        when(financeCheckServiceMock.isQueryActionRequired(anyLong(), anyLong())).thenReturn(serviceSuccess(false));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));


        // Call the service again
        ServiceResult<ProjectTeamStatusResource> resultWhenGolIsReadyToApprove = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(resultWhenGolIsReadyToApprove.isSuccess() && PENDING.equals(resultWhenGolIsReadyToApprove.getSuccess().getPartnerStatuses().get(0).getGrantOfferLetterStatus()));

    }

    @Test
    public void isGrantOfferLetterComplete() {

        FileEntry golFile = newFileEntry().withFilesizeBytes(10).withMediaType("application/pdf").build();

        List<ProjectUser> pu = newProjectUser().withRole(PROJECT_FINANCE_CONTACT).withUser(u).withOrganisation(o).withInvite(newProjectUserInvite().build()).build(1);
        List<PartnerOrganisation> po = newPartnerOrganisation().withOrganisation(o).withLeadOrganisation(true).build(1);
        Project p = newProject().withProjectUsers(pu).withApplication(application).withPartnerOrganisations(po).withDateSubmitted(ZonedDateTime.now()).withGrantOfferLetter(golFile).withSignedGrantOfferLetter(golFile).withOfferSubmittedDate(ZonedDateTime.now()).build();
        List<ProjectUserResource> puResource = newProjectUserResource().withProject(p.getId()).withOrganisation(o.getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(1);

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(golWorkflowHandlerMock.getState(p)).thenReturn(GrantOfferLetterState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && COMPLETE.equals(result.getSuccess().getLeadPartnerStatus().getGrantOfferLetterStatus()));
    }

    @Test
    public void spendProfileNotComplete() {

        spendProfile.setMarkedAsComplete(false);

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && ACTION_REQUIRED.equals(result.getSuccess().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void spendProfileRequiresEligibility() {
        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.empty());
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.REVIEW);
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && NOT_STARTED.equals(result.getSuccess().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void spendProfileRequiresViability() {

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.empty());
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.REVIEW);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && NOT_STARTED.equals(result.getSuccess().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void spendProfileNotSubmittedViabilityNotApplicable() {

        p.setSpendProfileSubmittedDate(null);

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.NOT_APPLICABLE);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && LEAD_ACTION_REQUIRED.equals(result.getSuccess().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void spendProfileCompleteNotSubmitted() {

        p.setSpendProfileSubmittedDate(null);

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && LEAD_ACTION_REQUIRED.equals(result.getSuccess().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void spendProfileCompleteSubmitted() {

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && PENDING.equals(result.getSuccess().getLeadPartnerStatus().getSpendProfileStatus()));
    }

    @Test
    public void spendProfileCompleteRejected() {
        p.setSpendProfileSubmittedDate(null);

        when(projectRepositoryMock.findById(p.getId())).thenReturn(Optional.of(p));
        when(projectUserRepositoryMock.findByProjectId(p.getId())).thenReturn(pu);
        when(projectUserMapperMock.mapToResource(pu.get(0))).thenReturn(puResource.get(0));
        when(organisationRepositoryMock.findById(o.getId())).thenReturn(Optional.of(o));
        when(partnerOrganisationRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(po.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.of(bankDetails));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(p.getId(), o.getId())).thenReturn(Optional.ofNullable(spendProfile));
        when(eligibilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(EligibilityState.APPROVED);
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(p.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(viabilityWorkflowHandlerMock.getState(po.get(0))).thenReturn(ViabilityState.APPROVED);
        when(financeCheckServiceMock.isQueryActionRequired(p.getId(), o.getId())).thenReturn(serviceSuccess(false));
        GrantOfferLetterStateResource unsentGrantOfferLetterState =
                GrantOfferLetterStateResource.stateInformationForNonPartnersView(GrantOfferLetterState.PENDING, null);
        when(golWorkflowHandlerMock.getExtendedState(p)).thenReturn(serviceSuccess(unsentGrantOfferLetterState));

        ServiceResult<ProjectTeamStatusResource> result = service.getProjectTeamStatus(p.getId(), Optional.ofNullable(pu.get(0).getId()));

        assertTrue(result.isSuccess() && LEAD_ACTION_REQUIRED.equals(result.getSuccess().getLeadPartnerStatus().getSpendProfileStatus()));
        assertTrue(project.getSpendProfileSubmittedDate() == null);
    }
}