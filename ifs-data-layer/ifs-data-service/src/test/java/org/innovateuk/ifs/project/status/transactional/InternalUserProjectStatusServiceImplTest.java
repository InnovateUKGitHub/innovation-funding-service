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
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.domain.*;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
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
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.status.resource.ProjectStatusPageResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
    private SpendProfileService spendProfileService;

    @Mock
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    @Mock
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplier;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private BankDetailsRepository bankDetailsRepository;

    @Mock
    private MonitoringOfficerService monitoringOfficerService;

    @Mock
    protected CompetitionRepository competitionRepository;

    @Mock
    protected ApplicationRepository applicationRepository;

    @Mock
    protected ProjectRepository projectRepository;

    @Mock
    protected OrganisationRepository organisationRepository;

    @Mock
    protected PartnerOrganisationRepository partnerOrganisationRepository;

    @Mock
    protected UserRepository userRepository;

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

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(organisationRepository.findById(organisation.getId())).thenReturn(Optional.of(organisation));
        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        User internalUser = newUser().withRoles(singleton(COMP_ADMIN)).build();
        when(userRepository.findById(internalUser.getId())).thenReturn(Optional.of(internalUser));
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
        Page<Project> page = new PageImpl<>(projects, PageRequest.of(0, 5), 3);

        when(projectRepository.searchByCompetitionIdAndApplicationIdLike(competitionId, applicationSearchString, PageRequest.of(0,5))).thenReturn(page);

        ServiceResult<ProjectStatusPageResource> result = service.getCompetitionStatus(competitionId, applicationSearchString, 0, 5);

        assertTrue(result.isSuccess());

        List<ProjectStatusResource> projectStatusResources = result.getSuccess().getContent();
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

        when(projectRepository.findByApplicationCompetitionIdAndProjectProcessActivityStateIn(competitionId, COMPLETED_STATES)).thenReturn(projects);

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

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));

        when(projectRepository.findById(projects.get(0).getId())).thenReturn(Optional.of(projects.get(0)));
        when(projectRepository.findById(projects.get(1).getId())).thenReturn(Optional.of(projects.get(1)));
        when(projectRepository.findById(projects.get(2).getId())).thenReturn(Optional.of(projects.get(2)));

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projects.get(0).getId(), organisations.get(0).getId())).thenReturn(Optional.of(bankDetails.get(0)));
        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projects.get(1).getId(), organisations.get(1).getId())).thenReturn(Optional.of(bankDetails.get(1)));
        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projects.get(2).getId(), organisations.get(2).getId())).thenReturn(Optional.of(bankDetails.get(2)));

        MonitoringOfficerResource monitoringOfficerInDB = newMonitoringOfficerResource().build();
        when(monitoringOfficerService.findMonitoringOfficerForProject(projects.get(0).getId())).thenReturn(serviceSuccess(monitoringOfficerInDB));
        when(monitoringOfficerService.findMonitoringOfficerForProject(projects.get(1).getId())).thenReturn(serviceSuccess(monitoringOfficerInDB));
        when(monitoringOfficerService.findMonitoringOfficerForProject(projects.get(2).getId())).thenReturn(serviceSuccess(monitoringOfficerInDB));

        when(organisationRepository.findById(organisations.get(0).getId())).thenReturn(Optional.of(organisations.get(0)));
        when(organisationRepository.findById(organisations.get(1).getId())).thenReturn(Optional.of(organisations.get(1)));
        when(organisationRepository.findById(organisations.get(2).getId())).thenReturn(Optional.of(organisations.get(2)));

        List<ProjectUserResource> puResource = newProjectUserResource().withProject(projects.get(0).getId()).withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId()).withRole(partnerRole.getId()).withRoleName(PROJECT_PARTNER.getName()).build(3);

        when(projectFinanceService.financeChecksDetails(projects.get(0).getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(projects.get(0).getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsNotRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(projects.get(0).getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsNotRequestingFunding().build()));

        when(projectFinanceService.financeChecksDetails(projects.get(1).getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(projects.get(1).getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsNotRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(projects.get(1).getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsNotRequestingFunding().build()));

        when(projectFinanceService.financeChecksDetails(projects.get(2).getId(), organisations.get(0).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(projects.get(2).getId(), organisations.get(1).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsNotRequestingFunding().build()));
        when(projectFinanceService.financeChecksDetails(projects.get(2).getId(), organisations.get(2).getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsNotRequestingFunding().build()));

        when(spendProfileService.getSpendProfileStatus(projects.get(0).getId())).thenReturn(serviceSuccess(ApprovalType.EMPTY));
        when(spendProfileService.getSpendProfileStatus(projects.get(1).getId())).thenReturn(serviceSuccess(ApprovalType.EMPTY));
        when(spendProfileService.getSpendProfileStatus(projects.get(2).getId())).thenReturn(serviceSuccess(ApprovalType.EMPTY));

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

        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());


        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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


        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(monitoringOfficerService.findMonitoringOfficerForProject(projectId)).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(REJECTED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(REJECTED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(false);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(COMPLETE, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(PENDING, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(any(long.class), any(long.class))).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getGrantOfferLetterStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        ServiceResult<ProjectStatusResource> resultFailure = service.getProjectStatusByProjectId(projectId);
        assertTrue(resultFailure.isFailure());
    }

    @Test
    public void getProjectStatusProjectDocumentsNotFullyApproved() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withCompetitionDocument(newCompetitionDocument().build())
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
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.ACTION_REQUIRED, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsApproved() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withCompetitionDocument(newCompetitionDocument().build())
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
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.COMPLETE, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsRejected() {
        long projectId = 2345L;
        competition.setCompetitionDocuments(newCompetitionDocument().withTitle("Exploitation plan").build(1));
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.REJECTED)
                .withCompetitionDocument(competition.getCompetitionDocuments().get(0))
                .build(1);

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
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.REJECTED, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsPending() {
        long projectId = 2345L;
        List<ProjectDocument> docs = newProjectDocument()
                .withCompetitionDocument(newCompetitionDocument().build())
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
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

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
        competition.setCompetitionDocuments(newCompetitionDocument().withTitle(COLLABORATION_AGREEMENT_TITLE, "Exploitation plan").build(2));
        Project project = createProjectStatusResource(projectId,
                                                      ApprovalType.APPROVED,
                                                      false,
                                                      true,
                                                      false,
                                                      false,
                                                      false,
                                                      SETUP);
        project.setPartnerOrganisations(newPartnerOrganisation().withOrganisation(newOrganisation().build()).build(2));
        project.setProjectDocuments(docs);
        project.setApplication(application);

        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ProjectActivityStates.COMPLETE, returnedProjectStatusResource.getDocumentsStatus());
    }

    @Test
    public void getProjectStatusProjectDocumentsApprovedWithSingleOrganisations() {
        long projectId = 2345L;
        competition.setCompetitionDocuments(newCompetitionDocument().withTitle(COLLABORATION_AGREEMENT_TITLE, "Exploitation plan").build(2));
        List<ProjectDocument> docs = newProjectDocument()
                .withStatus(DocumentStatus.APPROVED)
                .withCompetitionDocument(competition.getCompetitionDocuments().get(1))
                .build(1);
        PartnerOrganisationResource partnerOrganisationResource = newPartnerOrganisationResource().build();

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

        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);

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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(new BankDetails()));
        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId2)).thenReturn(Optional.empty());
        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

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
                .withCompetitionDocument(competitionDocuments.get(0))
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

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(Optional.of(bankDetail));
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceSuccess(monitoringOfficer));
        when(organisationRepository.findById(processRole.getOrganisationId())).thenReturn(Optional.of(organisation));
        when(spendProfileService.getSpendProfileStatus(projectId)).thenReturn(serviceSuccess(spendProfileStatus));
        when(golWorkflowHandler.isApproved(project)).thenReturn(golIsApproved);
        when(golWorkflowHandler.isRejected(project)).thenReturn(golRejected);
        if (!golIsApproved) {
            when(golWorkflowHandler.isReadyToApprove(project)).thenReturn(golReadyToApprove);
            if (!golReadyToApprove)
                when(golWorkflowHandler.isSent(project)).thenReturn(golIsSent);
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

        when(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(Optional.of(newBankDetails().withApproval(true).build()));
        when(projectDetailsWorkflowHandler.isSubmitted(project)).thenReturn(true);
        when(projectFinanceService.financeChecksDetails(anyLong(), anyLong())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));

        // Status shown to support user when MO is set is COMPLETE
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceSuccess(monitoringOfficer));
        when(loggedInUserSupplier.get()).thenReturn(newUser().withRoles(singleton(Role.SUPPORT)).build());
        ServiceResult<ProjectStatusResource> result = service.getProjectStatusByProjectId(projectId);
        ProjectStatusResource returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to support user when MO is not set is NOT_STARTED and not ACTION_REQUIRED
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(loggedInUserSupplier.get()).thenReturn(newUser().withRoles(singleton(Role.SUPPORT)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to innovation lead user when MO is not set is NOT_STARTED and not ACTION_REQUIRED
        when(loggedInUserSupplier.get()).thenReturn(newUser().withRoles(singleton(Role.INNOVATION_LEAD)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to stakeholder when MO is set is COMPLETE
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceSuccess(monitoringOfficer));
        when(loggedInUserSupplier.get()).thenReturn(newUser().withRoles(singleton(Role.STAKEHOLDER)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(COMPLETE, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to stakeholder when MO is not set is NOT_STARTED and not ACTION_REQUIRED
        when(monitoringOfficerService.findMonitoringOfficerForProject(project.getId())).thenReturn(serviceFailure(CommonErrors.notFoundError(MonitoringOfficer.class)));
        when(loggedInUserSupplier.get()).thenReturn(newUser().withRoles(singleton(Role.STAKEHOLDER)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(NOT_STARTED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to comp admin user when MO is not set is ACTION_REQUIRED
        when(loggedInUserSupplier.get()).thenReturn(newUser().withRoles(singleton(COMP_ADMIN)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getMonitoringOfficerStatus());

        // Status shown to project finance user when MO is not set is ACTION_REQUIRED
        when(loggedInUserSupplier.get()).thenReturn(newUser().withRoles(singleton(Role.PROJECT_FINANCE)).build());
        result = service.getProjectStatusByProjectId(projectId);
        returnedProjectStatusResource = result.getSuccess();
        assertTrue(result.isSuccess());
        assertEquals(ACTION_REQUIRED, returnedProjectStatusResource.getMonitoringOfficerStatus());
    }
}