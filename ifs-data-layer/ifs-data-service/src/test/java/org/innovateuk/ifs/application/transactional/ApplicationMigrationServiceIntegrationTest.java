package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.domain.ActivityLogBuilder;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.address.builder.AddressBuilder;
import org.innovateuk.ifs.address.builder.AddressTypeBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.application.builder.ApplicationOrganisationAddressBuilder;
import org.innovateuk.ifs.application.builder.FormInputResponseBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusBuilder;
import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.builder.EuGrantTransferBuilder;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder;
import org.innovateuk.ifs.organisation.builder.OrganisationBuilder;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.workflow.audit.ProcessHistory;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Testing {@link ApplicationMigrationService}
 */
@Rollback
@Transactional
public class ApplicationMigrationServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest  {

    @Autowired
    private ApplicationMigrationRepository applicationMigrationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressTypeRepository addressTypeRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Autowired
    private AverageAssessorScoreRepository averageAssessorScoreRepository;

    @Autowired
    private EuGrantTransferRepository euGrantTransferRepository;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectToBeCreatedRepository projectToBeCreatedRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private GrantProcessRepository grantProcessRepository;

    @Autowired
    private ApplicationProcessRepository applicationProcessRepository;

    @Autowired
    private ProcessHistoryRepository processHistoryRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private ApplicationKtaInviteRepository applicationKtaInviteRepository;

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private ApplicationMigrationService applicationMigrationService;

    private Long applicationId;

    @Before
    public void setup() {
        loginIfsAdmin();

        Competition competition = competitionRepository.findById(1L).get();
        Question question = competition.getQuestions().stream().findFirst().get();
        FormInput formInput = question.getFormInputs().stream().findFirst().get();

        loginSteveSmith();

        User user = UserBuilder.newUser()
                .withId(getSteveSmith().getId())
                .build();

        Application application = new Application();
        application.setCompetition(competition);

        application = applicationRepository.save(application);
        applicationId = application.getId();

        Organisation organisation = organisationRepository.save(OrganisationBuilder.newOrganisation().build());

        ProcessRole processRole = processRoleRepository.save(ProcessRoleBuilder.newProcessRole()
                .withApplication(application)
                .withRole(ProcessRoleType.LEADAPPLICANT)
                .withOrganisation(organisation)
                .withUser(user)
                .build());

        activityLogRepository.save(ActivityLogBuilder.newActivityLog()
                .withApplication(application)
                .withType(ActivityType.APPLICATION_SUBMITTED)
                .build());

        applicationFinanceRepository.save(ApplicationFinanceBuilder.newApplicationFinance()
                .withApplication(application).build());

        applicationHiddenFromDashboardRepository.save(new ApplicationHiddenFromDashboard(application, user));

        Address address = addressRepository.save(AddressBuilder.newAddress().build());
        AddressType addressType = addressTypeRepository.save(AddressTypeBuilder.newAddressType().build());
        OrganisationAddress organisationAddress = organisationAddressRepository.save(OrganisationAddressBuilder.newOrganisationAddress()
                .withAddress(address)
                .withAddressType(addressType)
                .build());
        applicationOrganisationAddressRepository.save(ApplicationOrganisationAddressBuilder.newApplicationOrganisationAddress()
                .withApplication(application)
                .withOrganisationAddress(organisationAddress)
                .build());

        averageAssessorScoreRepository.save(new AverageAssessorScore(application, BigDecimal.TEN));

        euGrantTransferRepository.save(EuGrantTransferBuilder.newEuGrantTransfer()
                .withApplication(application).build());

        formInputResponseRepository.save(FormInputResponseBuilder.newFormInputResponse()
                .withApplication(application)
                .withFormInputs(formInput)
                .build());

        projectRepository.save(ProjectBuilder.newProject()
                .withApplication(application)
                .withName("Project").build());

        projectToBeCreatedRepository.save(new ProjectToBeCreated(application, "email"));

        questionStatusRepository.save(QuestionStatusBuilder.newQuestionStatus()
                .withApplication(application).build());

        grantProcessRepository.save(new GrantProcess(application.getId()));

        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.CREATED));
        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.SUBMITTED));

        assessmentRepository.save(new Assessment(application, processRole));

        Interview interview = new Interview(application, processRole);
        interview.setProcessState(InterviewState.ASSIGNED);
        interviewRepository.save(interview);
        ProcessRole interviewProcessRole = processRoleRepository.save(ProcessRoleBuilder.newProcessRole()
                .withApplication(application)
                .withRole(ProcessRoleType.INTERVIEW_LEAD_APPLICANT)
                .withOrganisation(organisation)
                .withUser(user)
                .build());

        interviewAssignmentRepository.save(new InterviewAssignment(application, interviewProcessRole));

        Review review = new Review(application, processRole);
        review.setProcessState(ReviewState.CREATED);
        reviewRepository.save(review);

        supporterAssignmentRepository.save(new SupporterAssignment(application, user));

        InviteOrganisation inviteOrganisation = inviteOrganisationRepository.save(InviteOrganisationBuilder.newInviteOrganisation()
                .withOrganisation(organisation)
                .build());
        applicationInviteRepository.save(new ApplicationInvite("name", "application_invite@email.com", application, inviteOrganisation, UUID.randomUUID().toString(), InviteStatus.CREATED));

        applicationKtaInviteRepository.save(new ApplicationKtaInvite("name", "application_kta_invite@email.com", application,UUID.randomUUID().toString(), InviteStatus.CREATED));

        applicationMigrationRepository.save(new ApplicationMigration(applicationId, MigrationStatus.CREATED));
    }

    @Test
    public void findByApplicationIdAndStatus() {
        loginSystemMaintenanceUser();

        ServiceResult<Optional<ApplicationMigration>> result = applicationMigrationService.findByApplicationIdAndStatus(applicationId, MigrationStatus.CREATED);

        assertThat(result.isSuccess(), equalTo(true));

        Optional<ApplicationMigration> migration = result.getSuccess();

        assertTrue(migration.isPresent());
        assertEquals(applicationId, migration.get().getApplicationId());
        assertEquals(MigrationStatus.CREATED, migration.get().getStatus());
    }

    @Test
    public void migrateApplication() {
        loginSystemMaintenanceUser();

        ServiceResult<Void> result = applicationMigrationService.migrateApplication(applicationId);

        assertThat(result.isSuccess(), equalTo(true));

        Optional<Application> optionalNewApplication =  applicationRepository.findByPreviousApplicationId(applicationId);
        assertTrue(optionalNewApplication.isPresent());

        Application newApplication = optionalNewApplication.get();
        assertNotEquals(newApplication.getId(), applicationId);

        activityLogRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(activityLog -> {
                    assertNotNull(activityLog);
                    assertNotEquals(activityLog.getApplication().getId(), applicationId);
                });

        applicationFinanceRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationFinance -> {
                    assertNotNull(applicationFinance);
                    assertNotEquals(applicationFinance.getApplication().getId(), applicationId);
                });

        applicationHiddenFromDashboardRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationHiddenFromDashboard -> {
                    assertNotNull(applicationHiddenFromDashboard);
                    assertNotEquals(applicationHiddenFromDashboard.getApplication().getId(), applicationId);
                });

       applicationOrganisationAddressRepository.findByApplicationId(newApplication.getId()).stream()
               .forEach(applicationOrganisationAddress -> {
                   assertNotNull(applicationOrganisationAddress);
                   assertNotEquals(applicationOrganisationAddress.getApplication().getId(), applicationId);
               });

        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(newApplication.getId());
        assertTrue(averageAssessorScore.isPresent());
        assertNotEquals(averageAssessorScore.get().getApplication().getId(), applicationId);

        EuGrantTransfer euGrantTransfer = euGrantTransferRepository.findByApplicationId(newApplication.getId());
        assertNotNull(euGrantTransfer);
        assertNotEquals(euGrantTransfer.getApplication().getId(), applicationId);

        formInputResponseRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(formInputResponse -> {
                    assertNotNull(formInputResponse);
                    assertNotEquals(formInputResponse.getApplication().getId(), applicationId);
                });

        processRoleRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(processRole -> {
                    assertNotNull(processRole);
                    assertNotEquals(processRole.getApplicationId(), applicationId.longValue());
                });

        Optional<Project> project = projectRepository.findByApplicationId(newApplication.getId());
        assertTrue(project.isPresent());
        assertNotEquals(project.get().getApplication().getId(),applicationId);

        Optional<ProjectToBeCreated> projectToBeCreated = projectToBeCreatedRepository.findByApplicationId(newApplication.getId());
        assertTrue(projectToBeCreated.isPresent());
        assertNotEquals(projectToBeCreated.get().getApplication().getId(), applicationId);

        questionStatusRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(questionStatus -> {
                    assertNotNull(questionStatus);
                    assertNotEquals(questionStatus.getApplication().getId(), applicationId);
                });

        GrantProcess grantProcess = grantProcessRepository.findOneByApplicationId(newApplication.getId());
        assertNotNull(grantProcess);
        assertNotEquals(grantProcess.getApplicationId(), applicationId.longValue());

        applicationProcessRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(applicationProcess -> {
                    assertNotNull(applicationProcess);
                    assertNotEquals(applicationProcess.getTarget().getId(), applicationId);
                });

        processHistoryRepository.findByProcessId(newApplication.getId()).stream()
                .forEach(processHistory -> {
                    assertNotNull(processHistory);
                    assertNotEquals(processHistory.getProcess().getTarget(), newApplication);
                });

        assessmentRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(assessment -> {
                    assertNotNull(assessment);
                    assertNotEquals(assessment.getTarget().getId(), applicationId);
                });

        interviewRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(interview -> {
                    assertNotNull(interview);
                    assertNotEquals(interview.getTarget().getId(), applicationId);
                });

       interviewAssignmentRepository.findByTargetId(newApplication.getId()).stream()
               .forEach(interviewAssignment -> {
                   assertNotNull(interviewAssignment);
                   assertNotEquals(interviewAssignment.getTarget().getId(), applicationId);
               });

        reviewRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(review -> {
                    assertNotNull(review);
                    assertNotEquals(review.getTarget().getId(), applicationId);
                });

        supporterAssignmentRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(supporterAssignment -> {
                    assertNotNull(supporterAssignment);
                    assertNotEquals(supporterAssignment.getTarget().getId(), applicationId);
                });

        applicationInviteRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationInvite -> {
                    assertNotNull(applicationInvite);
                    assertNotEquals(applicationInvite.getTarget().getId(), applicationId);
                });

        Optional<ApplicationKtaInvite> applicationKtaInvite = applicationKtaInviteRepository.findByApplicationId(newApplication.getId());
        assertTrue(applicationKtaInvite.isPresent());
        assertNotEquals(applicationKtaInvite.get().getTarget().getId(), applicationId);

        Optional<Application> oldApplication = applicationRepository.findById(applicationId);
        assertFalse(oldApplication.isPresent());

        verifyOldApplicationDataDeleted();
    }

    private void verifyOldApplicationDataDeleted() {
        List<ActivityLog> activityLogs = activityLogRepository.findByApplicationId(applicationId);
        assertThat(activityLogs.size(), equalTo(0));

        List<ApplicationFinance> applicationFinances = applicationFinanceRepository.findByApplicationId(applicationId);
        assertThat(applicationFinances.size(), equalTo(0));

        List<ApplicationHiddenFromDashboard> applicationHiddenFromDashboards = applicationHiddenFromDashboardRepository.findByApplicationId(applicationId);
        assertThat(applicationHiddenFromDashboards.size(), equalTo(0));

        List<ApplicationOrganisationAddress> applicationOrganisationAddresses = applicationOrganisationAddressRepository.findByApplicationId(applicationId);
        assertThat(applicationOrganisationAddresses.size(), equalTo(0));

        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(applicationId);
        assertFalse(averageAssessorScore.isPresent());

        EuGrantTransfer euGrantTransfer = euGrantTransferRepository.findByApplicationId(applicationId);
        assertNull(euGrantTransfer);

        List<FormInputResponse> formInputResponses = formInputResponseRepository.findByApplicationId(applicationId);
        assertThat(formInputResponses.size(), equalTo(0));

        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(applicationId);
        assertThat(processRoles.size(), equalTo(0));

        Optional<Project> project = projectRepository.findByApplicationId(applicationId);
        assertFalse(project.isPresent());

        Optional<ProjectToBeCreated> projectToBeCreated = projectToBeCreatedRepository.findByApplicationId(applicationId);
        assertFalse(projectToBeCreated.isPresent());

        List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationId(applicationId);
        assertThat(questionStatuses.size(), equalTo(0));

        GrantProcess grantProcess = grantProcessRepository.findOneByApplicationId(applicationId);
        assertNull(grantProcess);

        List<ApplicationProcess> applicationProcesses = applicationProcessRepository.findByTargetId(applicationId);
        assertThat(applicationProcesses.size(), equalTo(0));

        List<ProcessHistory> processHistories = processHistoryRepository.findByProcessId(applicationId);
        assertThat(processHistories.size(), equalTo(0));

        List<Assessment> assessments = assessmentRepository.findByTargetId(applicationId);
        assertThat(assessments.size(), equalTo(0));

        List<Interview> interviews = interviewRepository.findByTargetId(applicationId);
        assertThat(interviews.size(), equalTo(0));

        List<InterviewAssignment> interviewAssignments = interviewAssignmentRepository.findByTargetId(applicationId);
        assertThat(interviewAssignments.size(), equalTo(0));

        List<Review> reviews = reviewRepository.findByTargetId(applicationId);
        assertThat(reviews.size(), equalTo(0));

        List<SupporterAssignment> supporterAssignments = supporterAssignmentRepository.findByTargetId(applicationId);
        assertThat(supporterAssignments.size(), equalTo(0));

        List<ApplicationInvite> applicationInvites = applicationInviteRepository.findByApplicationId(applicationId);
        assertThat(applicationInvites.size(), equalTo(0));

        Optional<ApplicationKtaInvite> applicationKtaInvite = applicationKtaInviteRepository.findByApplicationId(applicationId);
        assertFalse(applicationKtaInvite.isPresent());
    }

    @Test
    public void updateApplicationMigrationStatus() {
        loginSystemMaintenanceUser();

        ServiceResult<ApplicationMigration> result = applicationMigrationService.updateApplicationMigrationStatus(
                new ApplicationMigration(applicationId, MigrationStatus.MIGRATED));

        assertThat(result.isSuccess(), equalTo(true));

        ApplicationMigration migration = result.getSuccess();

        assertEquals(applicationId, migration.getApplicationId());
        assertEquals(MigrationStatus.MIGRATED, migration.getStatus());
    }
}
