package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.domain.ActivityLogBuilder;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.builder.ApplicationOrganisationAddressBuilder;
import org.innovateuk.ifs.application.builder.FormInputResponseBuilder;
import org.innovateuk.ifs.application.builder.QuestionStatusBuilder;
import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.builder.AssessmentBuilder;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.builder.EuGrantTransferBuilder;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder;
import org.innovateuk.ifs.interview.builder.InterviewBuilder;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder;
import org.innovateuk.ifs.invite.builder.ApplicationKtaInviteBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.review.builder.ReviewBuilder;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.domain.builder.SupporterAssignmentBuilder;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.builder.ProcessRoleBuilder;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.audit.ProcessHistory;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
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

        ProcessRole processRole = ProcessRoleBuilder.newProcessRole().build();

        application = applicationRepository.save(new Application());

        activityLogRepository.save(ActivityLogBuilder.newActivityLog().build());
        applicationFinanceRepository.save(ApplicationFinanceBuilder.newApplicationFinance().build());
        applicationHiddenFromDashboardRepository.save(new ApplicationHiddenFromDashboard(application, user));
        applicationOrganisationAddressRepository.save(ApplicationOrganisationAddressBuilder.newApplicationOrganisationAddress().build());
        averageAssessorScoreRepository.save(new AverageAssessorScore(application, BigDecimal.TEN));
        euGrantTransferRepository.save(EuGrantTransferBuilder.newEuGrantTransfer().build());
        formInputResponseRepository.save(FormInputResponseBuilder.newFormInputResponse().build());
        processRoleRepository.save(processRole);
        projectRepository.save(ProjectBuilder.newProject().build());
        projectToBeCreatedRepository.save(new ProjectToBeCreated(application, "email"));
        questionStatusRepository.save(QuestionStatusBuilder.newQuestionStatus().build());
        grantProcessRepository.save(new GrantProcess(application.getId()));
        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.CREATED));
        applicationProcessRepository.save(new ApplicationProcess(application, processRole, ApplicationState.SUBMITTED));
        assessmentRepository.save(AssessmentBuilder.newAssessment().build());
        interviewRepository.save(InterviewBuilder.newInterview().build());
        interviewAssignmentRepository.save(InterviewAssignmentBuilder.newInterviewAssignment().build());
        reviewRepository.save(ReviewBuilder.newReview().build());
        supporterAssignmentRepository.save(SupporterAssignmentBuilder.newSupporterAssignment().build());
        applicationInviteRepository.save(ApplicationInviteBuilder.newApplicationInvite().build());
        applicationKtaInviteRepository.save(ApplicationKtaInviteBuilder.newApplicationKtaInvite().build());
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

        Optional<Application> newApplication = applicationRepository.findById(application.getId());



        List<ActivityLog> activityLogs = activityLogRepository.findByApplicationId(application.getId());

        List<ApplicationFinance> applicationFinances = applicationFinanceRepository.findByApplicationId(application.getId());

        List<ApplicationHiddenFromDashboard> applicationHiddenFromDashboards = applicationHiddenFromDashboardRepository.findByApplicationId(application.getId());

        List<ApplicationOrganisationAddress> applicationOrganisationAddresses = applicationOrganisationAddressRepository.findByApplicationId(application.getId());

        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(application.getId());

        EuGrantTransfer euGrantTransfer = euGrantTransferRepository.findByApplicationId(application.getId());

        List<FormInputResponse> formInputResponses = formInputResponseRepository.findByApplicationId(application.getId());

        List<ProcessRole> processRoles = processRoleRepository.findByApplicationId(application.getId());

        Optional<Project> project = projectRepository.findByApplicationId(application.getId());

        Optional<ProjectToBeCreated> projectToBeCreated = projectToBeCreatedRepository.findByApplicationId(application.getId());

        List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationId(application.getId());

        GrantProcess grantProcess = grantProcessRepository.findOneByApplicationId(application.getId());

        List<ApplicationProcess> applicationProcesses = applicationProcessRepository.findByTargetId(application.getId());

        //List<ProcessHistory> processHistories = processHistoryRepository.findByProcessId(application.getId());

        List<Assessment> assessments = assessmentRepository.findByTargetId(application.getId());

        List<Interview> interviews = interviewRepository.findByTargetId(application.getId());

        List<InterviewAssignment> interviewAssignments = interviewAssignmentRepository.findByTargetId(application.getId());

        List<Review> reviews = reviewRepository.findByTargetId(application.getId());

        List<SupporterAssignment> supporterAssignments = supporterAssignmentRepository.findByTargetId(application.getId());

        List<ApplicationInvite> applicationInvites = applicationInviteRepository.findByApplicationId(application.getId());

        Optional<ApplicationKtaInvite> applicationKtaInvite = applicationKtaInviteRepository.findByApplicationId(application.getId());

        Optional<Application> oldApplication = applicationRepository.findById(application.getId());

        Optional<ApplicationMigration> applicationMigration = applicationMigrationRepository.findByApplicationIdAndStatus(application.getId(), MigrationStatus.MIGRATED);
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
