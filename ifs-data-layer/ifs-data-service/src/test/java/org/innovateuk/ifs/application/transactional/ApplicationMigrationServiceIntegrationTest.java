package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.activitylog.domain.ActivityLogBuilder;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.application.builder.ApplicationOrganisationAddressBuilder;
import org.innovateuk.ifs.application.builder.FormInputResponseBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusBuilder;
import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.builder.AssessmentBuilder;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.builder.EuGrantTransferBuilder;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder;
import org.innovateuk.ifs.interview.builder.InterviewBuilder;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder;
import org.innovateuk.ifs.invite.builder.ApplicationKtaInviteBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.review.builder.ReviewBuilder;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.supporter.domain.builder.SupporterAssignmentBuilder;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

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
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationHiddenFromDashboardRepository applicationHiddenFromDashboardRepository;

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
    private ApplicationMigrationService applicationMigrationService;

    private Application application;

    @Before
    public void setup() {
        loginIfsAdmin();

        User user = UserBuilder.newUser()
                .withId(getIfsAdmin().getId())
                .build();

        application = applicationRepository.save(new Application());

        ProcessRole processRole = ProcessRoleBuilder.newProcessRole()
                .withApplication(application)
                .build();

        activityLogRepository.save(ActivityLogBuilder.newActivityLog()
                .withApplication(application)
                .withType(ActivityType.APPLICATION_SUBMITTED)
                .build());
        applicationFinanceRepository.save(ApplicationFinanceBuilder.newApplicationFinance()
                .withApplication(application).build());
        applicationHiddenFromDashboardRepository.save(new ApplicationHiddenFromDashboard(application, user));
        applicationOrganisationAddressRepository.save(ApplicationOrganisationAddressBuilder.newApplicationOrganisationAddress()
                .withApplication(application).build());
        averageAssessorScoreRepository.save(new AverageAssessorScore(application, BigDecimal.TEN));
        euGrantTransferRepository.save(EuGrantTransferBuilder.newEuGrantTransfer()
                .withApplication(application).build());
        formInputResponseRepository.save(FormInputResponseBuilder.newFormInputResponse()
                .withApplication(application).build());
        processRoleRepository.save(processRole);
        projectRepository.save(ProjectBuilder.newProject()
                .withApplication(application).build());
        projectToBeCreatedRepository.save(new ProjectToBeCreated(application, "email"));
        questionStatusRepository.save(QuestionStatusBuilder.newQuestionStatus()
                .withApplication(application).build());
        grantProcessRepository.save(new GrantProcess(application.getId()));
        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.CREATED));
        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.SUBMITTED));
        assessmentRepository.save(AssessmentBuilder.newAssessment()
                .withApplication(application).build());
        interviewRepository.save(InterviewBuilder.newInterview()
                .withTarget(application).build());
        interviewAssignmentRepository.save(InterviewAssignmentBuilder.newInterviewAssignment()
                .withTarget(application).build());
        reviewRepository.save(ReviewBuilder.newReview()
                .withTarget(application).build());
        supporterAssignmentRepository.save(SupporterAssignmentBuilder.newSupporterAssignment()
                .withApplication(application).build());
        applicationInviteRepository.save(ApplicationInviteBuilder.newApplicationInvite()
                .withApplication(application).build());
        applicationKtaInviteRepository.save(ApplicationKtaInviteBuilder.newApplicationKtaInvite()
                .withApplication(application).build());
        applicationMigrationRepository.save(new ApplicationMigration(application.getId(), MigrationStatus.CREATED));
    }

    @Test
    public void findByApplicationIdAndStatus() {
        loginSystemMaintenanceUser();

        ServiceResult<Optional<ApplicationMigration>> result = applicationMigrationService.findByApplicationIdAndStatus(application.getId(), MigrationStatus.CREATED);

        assertThat(result.isSuccess(), equalTo(true));

        Optional<ApplicationMigration> migration = result.getSuccess();

        assertTrue(migration.isPresent());
        assertEquals(application.getId(), migration.get().getApplicationId());
        assertEquals(MigrationStatus.CREATED, migration.get().getStatus());
    }

    @Test
    public void migrateApplication() {
        loginSystemMaintenanceUser();

        ServiceResult<Void> result = applicationMigrationService.migrateApplication(application.getId());

        assertThat(result.isSuccess(), equalTo(true));

        Optional<Application> optionalNewApplication =  applicationRepository.findByPreviousApplicationId(application.getId());
        assertTrue(optionalNewApplication.isPresent());

        Application newApplication = optionalNewApplication.get();
        assertNotEquals(newApplication.getId(), application.getId());

        activityLogRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(activityLog -> {
                    assertNotNull(activityLog);
                    assertNotEquals(activityLog.getApplication().getId(), application.getId());
                });

        applicationFinanceRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationFinance -> {
                    assertNotNull(applicationFinance);
                    assertNotEquals(applicationFinance.getApplication().getId(), application.getId());
                });

        applicationHiddenFromDashboardRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationHiddenFromDashboard -> {
                    assertNotNull(applicationHiddenFromDashboard);
                    assertNotEquals(applicationHiddenFromDashboard.getApplication().getId(), application.getId());
                });

       applicationOrganisationAddressRepository.findByApplicationId(newApplication.getId()).stream()
               .forEach(applicationOrganisationAddress -> {
                   assertNotNull(applicationOrganisationAddress);
                   assertNotEquals(applicationOrganisationAddress.getApplication().getId(), application.getId());
               });

        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(newApplication.getId());
        assertTrue(averageAssessorScore.isPresent());
        assertNotEquals(averageAssessorScore.get().getApplication().getId(), application.getId());

        EuGrantTransfer euGrantTransfer = euGrantTransferRepository.findByApplicationId(newApplication.getId());
        assertNotNull(euGrantTransfer);
        assertNotEquals(euGrantTransfer.getApplication().getId(), application.getId());

        formInputResponseRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(formInputResponse -> {
                    assertNotNull(formInputResponse);
                    assertNotEquals(formInputResponse.getApplication().getId(), application.getId());
                });

        processRoleRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(processRole -> {
                    assertNotNull(processRole);
                    assertNotEquals(processRole.getApplicationId(), application.getId().longValue());
                });

        Optional<Project> project = projectRepository.findByApplicationId(newApplication.getId());
        assertTrue(project.isPresent());
        assertNotEquals(project.get().getApplication().getId(), application.getId());

        Optional<ProjectToBeCreated> projectToBeCreated = projectToBeCreatedRepository.findByApplicationId(newApplication.getId());
        assertTrue(projectToBeCreated.isPresent());
        assertNotEquals(projectToBeCreated.get().getApplication().getId(), application.getId());

        questionStatusRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(questionStatus -> {
                    assertNotNull(questionStatus);
                    assertNotEquals(questionStatus.getApplication().getId(), application.getId());
                });

        GrantProcess grantProcess = grantProcessRepository.findOneByApplicationId(newApplication.getId());
        assertNotNull(grantProcess);
        assertNotEquals(grantProcess.getApplicationId(), application.getId().longValue());

        applicationProcessRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(applicationProcess -> {
                    assertNotNull(applicationProcess);
                    assertNotEquals(applicationProcess.getTarget().getId(), application.getId());
                });

        processHistoryRepository.findByProcessId(newApplication.getId()).stream()
                .forEach(processHistory -> {
                    assertNotNull(processHistory);
                    assertNotEquals(processHistory.getProcess().getTarget(), newApplication);
                });

        assessmentRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(assessment -> {
                    assertNotNull(assessment);
                    assertNotEquals(assessment.getTarget().getId(), application.getId());
                });

        interviewRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(interview -> {
                    assertNotNull(interview);
                    assertNotEquals(interview.getTarget().getId(), application.getId());
                });

       interviewAssignmentRepository.findByTargetId(newApplication.getId()).stream()
               .forEach(interviewAssignment -> {
                   assertNotNull(interviewAssignment);
                   assertNotEquals(interviewAssignment.getTarget().getId(), application.getId());
               });

        reviewRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(review -> {
                    assertNotNull(review);
                    assertNotEquals(review.getTarget().getId(), application.getId());
                });

        supporterAssignmentRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(supporterAssignment -> {
                    assertNotNull(supporterAssignment);
                    assertNotEquals(supporterAssignment.getTarget().getId(), application.getId());
                });

        applicationInviteRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationInvite -> {
                    assertNotNull(applicationInvite);
                    assertNotEquals(applicationInvite.getTarget().getId(), application.getId());
                });

        Optional<ApplicationKtaInvite> applicationKtaInvite = applicationKtaInviteRepository.findByApplicationId(newApplication.getId());
        assertTrue(applicationKtaInvite.isPresent());
        assertNotEquals(applicationKtaInvite.get().getTarget().getId(), application.getId());

        Optional<Application> oldApplication = applicationRepository.findById(newApplication.getId());
        assertFalse(oldApplication.isPresent());

        Optional<ApplicationMigration> applicationMigration = applicationMigrationRepository.findByApplicationIdAndStatus(newApplication.getId(), MigrationStatus.MIGRATED);
        assertTrue(applicationMigration.isPresent());
        assertNotEquals(applicationMigration.get().getApplicationId(), application.getId());
        assertNotEquals(applicationMigration.get().getStatus(), MigrationStatus.MIGRATED);
    }

    @Test
    public void updateApplicationMigrationStatus() {
        loginSystemMaintenanceUser();

        ServiceResult<ApplicationMigration> result = applicationMigrationService.updateApplicationMigrationStatus(
                new ApplicationMigration(application.getId(), MigrationStatus.MIGRATED));

        assertThat(result.isSuccess(), equalTo(true));

        ApplicationMigration migration = result.getSuccess();

        assertEquals(application.getId(), migration.getApplicationId());
        assertEquals(MigrationStatus.MIGRATED, migration.getStatus());
    }
}
