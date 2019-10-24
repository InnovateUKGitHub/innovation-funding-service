package org.innovateuk.ifs.project.status.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
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
import org.innovateuk.ifs.project.financechecks.service.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
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

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentBuilder.newCompetitionDocument;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentBuilder.newProjectDocument;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InternalUserProjectStatusServiceImplTest extends BaseServiceUnitTest<InternalUserProjectStatusService> {

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
    private ApplicationFinanceService financeServiceMock;

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

        projectProcess = newProjectProcess()
                .withActivityState(LIVE)
                .build();

        p = newProject().
                withProjectUsers(pu).
                withApplication(application).
                withPartnerOrganisations(po).
                withDateSubmitted(ZonedDateTime.now()).
                withSpendProfileSubmittedDate(ZonedDateTime.now()).
                withProjectProcess(projectProcess).
                build();

        puResource = newProjectUserResource().
                withProject(p.getId()).
                withOrganisation(o.getId()).
                withRole(partnerRole.getId()).
                withRoleName(PROJECT_PARTNER.getName()).
                build(1);


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
    protected InternalUserProjectStatusService supplyServiceUnderTest() {
        return new InternalUserProjectStatusServiceImpl();
    }

    @Test
    public void getCompetitionStatus() {
        long competitionId = 123L;
        String applicationSearchString = "1";

        List<Project> projects = setupCompetitionStatusMocks(competitionId);

        when(projectRepositoryMock.searchByCompetitionIdAndApplicationIdLike(competitionId, applicationSearchString)).thenReturn(projects);

        ServiceResult<List<ProjectStatusResource>> result = service.getCompetitionStatus(competitionId, applicationSearchString);

        assertTrue(result.isSuccess());

        List<ProjectStatusResource> projectStatusResources = result.getSuccess();
        assertTrue(projectsGetSortedByApplicationId(projectStatusResources));
        assertEquals(3, projectStatusResources.size());
        assertEquals(new Integer(3), projectStatusResources.get(0).getNumberOfPartners());
        assertEquals(new Integer(3), projectStatusResources.get(1).getNumberOfPartners());
        assertEquals(new Integer(3), projectStatusResources.get(2).getNumberOfPartners());
    }

    @Test
    public void getPreviousCompetitionStatus() {
        long competitionId = 123L;

        List<Project> projects = setupCompetitionStatusMocks(competitionId);

        when(projectRepositoryMock.findByApplicationCompetitionIdAndProjectProcessActivityStateIn(competitionId, COMPLETED_STATES)).thenReturn(projects);

        ServiceResult<List<ProjectStatusResource>> result = service.getPreviousCompetitionStatus(competitionId);

        assertTrue(result.isSuccess());

        List<ProjectStatusResource> projectStatusResources = result.getSuccess();
        assertTrue(projectsGetSortedByApplicationId(projectStatusResources));
        assertEquals(3, projectStatusResources.size());
        assertEquals(new Integer(3), projectStatusResources.get(0).getNumberOfPartners());
        assertEquals(new Integer(3), projectStatusResources.get(1).getNumberOfPartners());
        assertEquals(new Integer(3), projectStatusResources.get(2).getNumberOfPartners());
    }

    private List<Project> setupCompetitionStatusMocks(long competitionId) {
        Competition competition = newCompetition().withId(competitionId).build();

        OrganisationType businessOrganisationType = newOrganisationType()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .build();

        OrganisationType academicOrganisationType = newOrganisationType()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .build();

        List<Organisation> organisations = newOrganisation()
                .withOrganisationType(businessOrganisationType)
                .build(2);
        organisations.add(newOrganisation().withOrganisationType(academicOrganisationType).build());

        List<User> users = newUser().build(3);

        List<ProcessRole> applicantProcessRoles = newProcessRole()
                .withUser(users.get(0), users.get(1), users.get(2))
                .withRole(Role.LEADAPPLICANT, Role.APPLICANT, Role.APPLICANT)
                .withOrganisationId(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId())
                .build(3);

        List<Application> applications = newApplication()
                .withCompetition(competition)
                .withProcessRoles(applicantProcessRoles.get(0), applicantProcessRoles.get(1), applicantProcessRoles.get(2))
                .build(3);

        List<ProjectUser> projectUsers = newProjectUser()
                .withRole(PROJECT_PARTNER).withUser(users.get(0), users.get(1), users.get(2)).withOrganisation(organisations.get(0), organisations.get(1), organisations.get(2))
                .build(3);

        List<PartnerOrganisation> partnerOrganisations = newPartnerOrganisation().withOrganisation(organisations.get(0), organisations.get(1)).build(3);
        List<Project> projects = newProject()
                .withProjectProcess(projectProcess)
                .withPartnerOrganisations(partnerOrganisations)
                .withApplication(applications.get(0), applications.get(1), applications.get(2)).withProjectUsers(projectUsers)
                .build(3);

        List<BankDetails> bankDetails = newBankDetails()
                .withOrganisation(organisations.get(0), organisations.get(1), organisations.get(2))
                .build(3);

        SpendProfile spendProfile = newSpendProfile().build();

        List<PartnerOrganisationResource> partnerOrganisationResources =
                newPartnerOrganisationResource()
                        .withId(partnerOrganisations.get(0).getId(),
                                partnerOrganisations.get(1).getId(),
                                partnerOrganisations.get(2).getId())
                        .build(3);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));

        when(projectRepositoryMock.findById(projects.get(0).getId())).thenReturn(Optional.of(projects.get(0)));
        when(projectRepositoryMock.findById(projects.get(1).getId())).thenReturn(Optional.of(projects.get(1)));
        when(projectRepositoryMock.findById(projects.get(2).getId())).thenReturn(Optional.of(projects.get(2)));

        when(projectUserRepositoryMock.findByProjectId(projects.get(0).getId())).thenReturn(projectUsers);
        when(projectUserRepositoryMock.findByProjectId(projects.get(1).getId())).thenReturn(projectUsers);
        when(projectUserRepositoryMock.findByProjectId(projects.get(2).getId())).thenReturn(projectUsers);

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projects.get(0).getId(), organisations.get(0).getId())).thenReturn(bankDetails.get(0));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projects.get(1).getId(), organisations.get(1).getId())).thenReturn(bankDetails.get(1));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projects.get(2).getId(), organisations.get(2).getId())).thenReturn(bankDetails.get(2));

        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projects.get(0).getId(), organisations.get(0).getId())).thenReturn(Optional.of(spendProfile));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projects.get(1).getId(), organisations.get(1).getId())).thenReturn(Optional.of(spendProfile));
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(projects.get(2).getId(), organisations.get(2).getId())).thenReturn(Optional.of(spendProfile));

        MonitoringOfficerResource monitoringOfficerInDB = newMonitoringOfficerResource().build();
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(projects.get(0).getId())).thenReturn(serviceSuccess(monitoringOfficerInDB));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(projects.get(1).getId())).thenReturn(serviceSuccess(monitoringOfficerInDB));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(projects.get(2).getId())).thenReturn(serviceSuccess(monitoringOfficerInDB));

        when(organisationRepositoryMock.findById(organisations.get(0).getId())).thenReturn(Optional.of(organisations.get(0)));
        when(organisationRepositoryMock.findById(organisations.get(1).getId())).thenReturn(Optional.of(organisations.get(1)));
        when(organisationRepositoryMock.findById(organisations.get(2).getId())).thenReturn(Optional.of(organisations.get(2)));

        List<ApplicationFinance> applicationFinances = newApplicationFinance().build(3);
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(0).getApplication().getId(), organisations.get(0).getId())).thenReturn(applicationFinances.get(0));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(0).getApplication().getId(), organisations.get(1).getId())).thenReturn(applicationFinances.get(1));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(0).getApplication().getId(), organisations.get(2).getId())).thenReturn(applicationFinances.get(2));

        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(1).getApplication().getId(), organisations.get(0).getId())).thenReturn(applicationFinances.get(0));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(1).getApplication().getId(), organisations.get(1).getId())).thenReturn(applicationFinances.get(1));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(1).getApplication().getId(), organisations.get(2).getId())).thenReturn(applicationFinances.get(2));

        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(2).getApplication().getId(), organisations.get(0).getId())).thenReturn(applicationFinances.get(0));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(2).getApplication().getId(), organisations.get(1).getId())).thenReturn(applicationFinances.get(1));
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(projects.get(2).getApplication().getId(), organisations.get(2).getId())).thenReturn(applicationFinances.get(2));

        ApplicationFinanceResource applicationFinanceResource0 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(0).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(0))).thenReturn(applicationFinanceResource0);

        ApplicationFinanceResource applicationFinanceResource1 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(1).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(1))).thenReturn(applicationFinanceResource1);

        ApplicationFinanceResource applicationFinanceResource2 = newApplicationFinanceResource().withGrantClaimPercentage(20).withOrganisation(organisations.get(2).getId()).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinances.get(2))).thenReturn(applicationFinanceResource2);

        List<ProjectUserResource> puResource = newProjectUserResource().withProject(projects.get(0).getId()).withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(3);

        when(projectUserMapperMock.mapToResource(projectUsers.get(0))).thenReturn(puResource.get(0));
        when(projectUserMapperMock.mapToResource(projectUsers.get(1))).thenReturn(puResource.get(1));
        when(projectUserMapperMock.mapToResource(projectUsers.get(2))).thenReturn(puResource.get(2));

        when(financeServiceMock.organisationSeeksFunding(projects.get(0).getId(), projects.get(0).getApplication().getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(true));
        when(financeServiceMock.organisationSeeksFunding(projects.get(0).getId(), projects.get(0).getApplication().getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(false));
        when(financeServiceMock.organisationSeeksFunding(projects.get(0).getId(), projects.get(0).getApplication().getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(false));

        when(financeServiceMock.organisationSeeksFunding(projects.get(1).getId(), projects.get(1).getApplication().getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(true));
        when(financeServiceMock.organisationSeeksFunding(projects.get(1).getId(), projects.get(1).getApplication().getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(false));
        when(financeServiceMock.organisationSeeksFunding(projects.get(1).getId(), projects.get(1).getApplication().getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(false));

        when(financeServiceMock.organisationSeeksFunding(projects.get(2).getId(), projects.get(2).getApplication().getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(true));
        when(financeServiceMock.organisationSeeksFunding(projects.get(2).getId(), projects.get(2).getApplication().getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(false));
        when(financeServiceMock.organisationSeeksFunding(projects.get(2).getId(), projects.get(2).getApplication().getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(false));

        when(projectUsersHelperMock.getPartnerOrganisations(projects.get(0).getId())).thenReturn(organisations);
        when(projectUsersHelperMock.getPartnerOrganisations(projects.get(1).getId())).thenReturn(organisations);
        when(projectUsersHelperMock.getPartnerOrganisations(projects.get(2).getId())).thenReturn(organisations);

        when(spendProfileServiceMock.getSpendProfileStatus(projects.get(0).getId())).thenReturn(serviceSuccess(ApprovalType.EMPTY));
        when(spendProfileServiceMock.getSpendProfileStatus(projects.get(1).getId())).thenReturn(serviceSuccess(ApprovalType.EMPTY));
        when(spendProfileServiceMock.getSpendProfileStatus(projects.get(2).getId())).thenReturn(serviceSuccess(ApprovalType.EMPTY));

        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projects.get(0).getId())).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResources.get(0))));
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projects.get(1).getId())).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResources.get(1))));
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projects.get(2).getId())).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResources.get(2))));

        return projects;
    }

    private boolean projectsGetSortedByApplicationId(List<ProjectStatusResource> after) {
        return after.stream()
                .sorted(Comparator.comparing(ProjectStatusResource::getApplicationNumber))
                .collect(Collectors.toList())
                .equals(after);
    }

    @Test
    public void getProjectStatusResourceByProject() {
        long projectId = 2345L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      true,
                                                      SETUP);

        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());


        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusFinanceContactComplete() {
        long projectId = 2345L;
        long organisationId = 123L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        List<PartnerOrganisation> po = singletonList(newPartnerOrganisation().withOrganisation(o).build());
        project.setPartnerOrganisations(po);
        project.setAddress(newAddress().build());
        project.setTargetStartDate(LocalDate.now());
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(COMPLETE, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());


        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusFinanceContactCompleteButPartnerLocationsRequiredAndNotComplete() {
        long projectId = 2345L;
        long organisationId = 123L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      true,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        List<PartnerOrganisation> po = singletonList(newPartnerOrganisation().withOrganisation(o).build());
        project.setPartnerOrganisations(po);
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }


    @Test
    public void getProjectStatusFinanceContactCompleteAndPartnerLocationsRequiredAndComplete() {
        long projectId = 2345L;
        long organisationId = 123L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      true,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        List<PartnerOrganisation> po = singletonList(newPartnerOrganisation()
                .withOrganisation(o)
                .withPostcode("TW14 9QG")
                .build());
        project.setPartnerOrganisations(po);
        project.setAddress(newAddress().build());
        project.setTargetStartDate(LocalDate.now());
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(COMPLETE, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusFinanceContactIncomplete() {
        long projectId = 2345L;
        long organisationId = 123L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        List<PartnerOrganisation> po = singletonList(newPartnerOrganisation().withOrganisation(o).build());
        project.setPartnerOrganisations(po);
        project.setAddress(newAddress().build());
        project.setTargetStartDate(LocalDate.now());

        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(Optional.empty());
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(projectId)).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(COMPLETE, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(PENDING, returnedProjectStatusResource.getProjectTeamStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusResourceByProjectSpendProfileRejected() {
        long projectId = 2345L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.REJECTED,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);

        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));
        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(REJECTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusResourceByProjectGolPrecursorsCompleteAndGolRejected() {
        long projectId = 2345L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      false,
                                                      false,
                                                      true,
                                                      false,
                                                      SETUP);
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(newBankDetails().withApproval(true).build());
        when(projectUsersHelperMock.getFinanceContact(any(long.class), any(long.class))).thenReturn(pu);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(REJECTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(REJECTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusResourceByProjectGolPrecursorsCompleteAndGolApproved() {
        long projectId = 2345L;
        long orgId = 564321L;
        PartnerOrganisationResource partnerOrg = newPartnerOrganisationResource().withOrganisation(orgId).build();

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      SETUP);

        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(newBankDetails().withApproval(true).build());
        when(projectUsersHelperMock.getFinanceContact(any(long.class), any(long.class))).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(false);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projectId)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrg)));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusResourceByProjectGolPrecursorsCompleteAndGolSent() {
        long projectId = 2345L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(newBankDetails().withApproval(true).build());
        when(projectUsersHelperMock.getFinanceContact(any(long.class), any(long.class))).thenReturn(pu);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(PENDING, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusResourceByProjectGolPrecursorsCompleteAndSignedGolSubmitted() {
        long projectId = 2345L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(newBankDetails().withApproval(true).build());
        when(projectUsersHelperMock.getFinanceContact(any(long.class), any(long.class))).thenReturn(pu);

        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusResourceByProjectGolPrecursorsCompleteAndGolReadyToSend() {
        long projectId = 2345L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(newBankDetails().withApproval(true).build());
        when(projectUsersHelperMock.getFinanceContact(any(long.class), any(long.class))).thenReturn(pu);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(PENDING, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusProjectDocumentsNotFullyApproved() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.SUBMITTED, DocumentStatus.APPROVED)
                .build(2);

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        project.setProjectDocuments(docs);
        project.setApplication(application);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        verify(financeServiceMock).organisationSeeksFunding(any(long.class), any(long.class), any(long.class));
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsApproved() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.APPROVED)
                .build(1);

        competition.setCompetitionDocuments(newCompetitionDocument().withTitle("Exploitation plan").build(1));
        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        project.setProjectDocuments(docs);
        project.setApplication(application);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        verify(financeServiceMock).organisationSeeksFunding(any(long.class), any(long.class), any(long.class));
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.COMPLETE, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsRejected() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.REJECTED)
                .build(1);

        competition.setCompetitionDocuments(newCompetitionDocument().withTitle("Exploitation plan").build(1));
        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        project.setProjectDocuments(docs);
        project.setApplication(application);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        verify(financeServiceMock).organisationSeeksFunding(any(long.class), any(long.class), any(long.class));
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.REJECTED, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsPending() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.APPROVED)
                .build(1);

        competition.setCompetitionDocuments(newCompetitionDocument().withTitle("Exploitation plan").build(2));
        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        project.setProjectDocuments(docs);
        project.setApplication(application);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        verify(financeServiceMock).organisationSeeksFunding(any(long.class), any(long.class), any(long.class));
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.PENDING, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsApprovedWithMultipleOrganisations() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.APPROVED, DocumentStatus.APPROVED)
                .build(2);
        List<PartnerOrganisationResource> partnerOrganisationResources = newPartnerOrganisationResource().build(2);
        competition.setCompetitionDocuments(newCompetitionDocument().withTitle(COLLABORATION_AGREEMENT_TITLE, "Exploitation plan").build(2));
        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        project.setProjectDocuments(docs);
        project.setApplication(application);

        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projectId)).thenReturn(serviceSuccess(partnerOrganisationResources));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        verify(financeServiceMock).organisationSeeksFunding(any(long.class), any(long.class), any(long.class));
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.COMPLETE, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsApprovedWithSingleOrganisations() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.APPROVED)
                .build(1);
        PartnerOrganisationResource partnerOrganisationResource = newPartnerOrganisationResource().build();

        competition.setCompetitionDocuments(newCompetitionDocument().withTitle(COLLABORATION_AGREEMENT_TITLE, "Exploitation plan").build(2));
        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        project.setProjectDocuments(docs);
        project.setApplication(application);

        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projectId)).thenReturn(serviceSuccess(singletonList(partnerOrganisationResource)));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        verify(financeServiceMock).organisationSeeksFunding(any(long.class), any(long.class), any(long.class));
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(COMPLETE, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusBankDetailsCompleteNotApproved() {
        long projectId = 2345L;
        long organisationId = 123L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        List<PartnerOrganisation> po = singletonList(newPartnerOrganisation().withOrganisation(o).build());
        project.setPartnerOrganisations(po);
        project.setAddress(newAddress().build());
        project.setTargetStartDate(LocalDate.now());
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(new BankDetails());
        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(COMPLETE, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
    }

    @Test
    public void getProjectStatusBankDetailsIncomplete() {
        long projectId = 2345L;
        long organisationId = 123L;
        long organisationId2 = 234L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        Organisation o2 = newOrganisation().withId(organisationId2).build();
        List<PartnerOrganisation> po = asList(newPartnerOrganisation().withOrganisation(o).build(), newPartnerOrganisation().withOrganisation(o2).build());
        project.setPartnerOrganisations(po);
        project.setAddress(newAddress().build());
        project.setTargetStartDate(LocalDate.now());
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(newBankDetails().withApproval(true).build());
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId2)).thenReturn(null);
        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(pu);
        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId2)).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(2), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(COMPLETE, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(PENDING, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
    }

    @Test
    public void getProjectStatusBankDetailsApproved() {
        long projectId = 2345L;
        long organisationId = 123L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        List<PartnerOrganisation> po = singletonList(newPartnerOrganisation().withOrganisation(o).build());
        project.setPartnerOrganisations(po);
        project.setAddress(newAddress().build());
        project.setTargetStartDate(LocalDate.now());
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(newBankDetails().withApproval(true).build());
        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(project.getName(), returnedProjectStatusResource.getProjectTitle());
        assertEquals(project.getId(), returnedProjectStatusResource.getProjectNumber());
        assertEquals(Integer.valueOf(1), returnedProjectStatusResource.getNumberOfPartners());

        assertEquals(COMPLETE, returnedProjectStatusResource.getProjectDetailsStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getBankDetailsStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getFinanceChecksStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getSpendProfileStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
    }

    private Project createProjectStatusResource(long projectId,
                                                ApprovalType spendProfileStatus,
                                                Boolean golReadyToApprove,
                                                Boolean golIsSent,
                                                Boolean golIsApproved,
                                                Boolean golRejected,
                                                boolean locationPerPartnerRequired,
                                                ProjectState projectState) {

        long competitionId = 112L;

        List<CompetitionDocument> competitionDocuments
                = newCompetitionDocument().withTitle("document").build(1);

        Competition competition = CompetitionBuilder.newCompetition()
                .withId(competitionId)
                .withCompetitionDocuments(competitionDocuments)
                .withLocationPerPartner(locationPerPartnerRequired)
                .build();
        competition.setProjectStages(EnumSet.allOf(ProjectSetupStage.class).stream().map(stage -> new ProjectStages(competition, stage)).collect(Collectors.toList()));

        Application application = newApplication()
                .withCompetition(competition)
                .build();
        Organisation organisation = newOrganisation().build();
        Role role = Role.LEADAPPLICANT;
        ProcessRole processRole = newProcessRole().
                withRole(role).
                withApplication(application).
                withOrganisationId(organisation.getId()).
                build();
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().withOrganisation(organisation).build();
        PartnerOrganisationResource partnerOrganisationResource = newPartnerOrganisationResource().withId(partnerOrganisation.getId()).build();

        List<ProjectDocument> projectDocuments = newProjectDocument()
                .withProject(project)
                .withStatus(DocumentStatus.APPROVED)
                .build(1);
        ProjectProcess projectProcess = newProjectProcess().withProject(project).withActivityState(projectState).build();

        Project project = newProject()
                .withId(projectId)
                .withApplication(application)
                .withPartnerOrganisations(singletonList(partnerOrganisation))
                .withProjectDocuments(projectDocuments)
                .withProjectProcess(projectProcess)
                .build();

        BankDetails bankDetail = newBankDetails().withProject(project).build();
        SpendProfile spendprofile = newSpendProfile().withOrganisation(organisation).build();
        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(projectProcessRepositoryMock.findOneByTargetId(projectId)).thenReturn(projectProcess);
        when(projectUsersHelperMock.getPartnerOrganisations(project.getId())).thenReturn(singletonList(organisation));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(bankDetail);
        when(spendProfileRepositoryMock.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(Optional.of(spendprofile));
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceSuccess(monitoringOfficer));
        when(partnerOrganisationServiceMock.getProjectPartnerOrganisations(projectId)).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResource)));
        when(organisationRepositoryMock.findById(processRole.getOrganisationId())).thenReturn(Optional.of(organisation));
        when(spendProfileServiceMock.getSpendProfileStatus(projectId)).thenReturn(serviceSuccess(spendProfileStatus));
        when(golWorkflowHandlerMock.isApproved(project)).thenReturn(golIsApproved);
        when(golWorkflowHandlerMock.isRejected(project)).thenReturn(golRejected);
        if (!golIsApproved) {
            when(golWorkflowHandlerMock.isReadyToApprove(project)).thenReturn(golReadyToApprove);
            if (!golReadyToApprove)
                when(golWorkflowHandlerMock.isSent(project)).thenReturn(golIsSent);
        }
        return project;
    }

    /**
     * Tests MO requirement for IFS-1307
     */
    @Test
    public void getProjectStatusShowMOStatusForSupportAndInnoLeadAndStakeholder() {
        long projectId = 2345L;
        long organisationId = 123L;

        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.EMPTY,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        Organisation o = newOrganisation().withId(organisationId).build();
        List<PartnerOrganisation> po = singletonList(newPartnerOrganisation().withOrganisation(o).build());
        project.setPartnerOrganisations(po);
        project.setAddress(newAddress().build());
        project.setTargetStartDate(LocalDate.now());
        Optional<ProjectUser> pu = Optional.of(newProjectUser().withRole(PROJECT_FINANCE_CONTACT).build());
        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();

        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(newBankDetails().withApproval(true).build());
        when(projectUsersHelperMock.getFinanceContact(projectId, organisationId)).thenReturn(pu);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);
        when(financeServiceMock.organisationSeeksFunding(any(Long.class), any(long.class), any(long.class))).thenReturn(serviceSuccess(true));

        // Status shown to support user when MO is set is COMPLETE
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceSuccess(monitoringOfficer));
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().withRoles(singleton(Role.SUPPORT)).build());
        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to support user when MO is not set is NOT_STARTED and not ACTION_REQUIRED
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().withRoles(singleton(Role.SUPPORT)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to innovation lead user when MO is not set is NOT_STARTED and not ACTION_REQUIRED
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().withRoles(singleton(Role.INNOVATION_LEAD)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to stakeholder when MO is set is COMPLETE
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceSuccess(monitoringOfficer));
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().withRoles(singleton(Role.STAKEHOLDER)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to stakeholder when MO is not set is NOT_STARTED and not ACTION_REQUIRED
        when(monitoringOfficerServiceMock.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().withRoles(singleton(Role.STAKEHOLDER)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to comp admin user when MO is not set is ACTION_REQUIRED
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().withRoles(singleton(COMP_ADMIN)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to project finance user when MO is not set is ACTION_REQUIRED
        when(loggedInUserSupplierMock.get()).thenReturn(newUser().withRoles(singleton(Role.PROJECT_FINANCE)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getMonitoringOfficerStatus());
    }
}