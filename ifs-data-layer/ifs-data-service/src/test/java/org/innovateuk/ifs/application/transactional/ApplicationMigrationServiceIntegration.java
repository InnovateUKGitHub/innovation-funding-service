package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.application.domain.*;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AverageAssessorScore;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.ApplicationKtaInvite;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectToBeCreated;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.audit.ProcessHistory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Testing {@link ApplicationMigrationService}
 */
@Rollback
@Transactional
public class ApplicationMigrationServiceIntegration extends BaseApplicationMigrationSetup {

    @Autowired
    private ApplicationMigrationService applicationMigrationService;

    @Before
    public void setup() {
        prepareData();
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

        Optional<Application> optionalNewApplication = applicationRepository.findByPreviousApplicationId(applicationId);
        assertTrue(optionalNewApplication.isPresent());

        Application newApplication = optionalNewApplication.get();
        assertNotEquals(applicationId, newApplication.getId());
        assertEquals(applicationId, newApplication.getPreviousApplicationId());

        applicationHorizonWorkProgrammeRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationHorizonWorkProgramme -> {
                    assertNotNull(applicationHorizonWorkProgramme);
                    assertNotEquals(applicationId, applicationHorizonWorkProgramme.getApplicationId());
                });

        activityLogRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(activityLog -> {
                    assertNotNull(activityLog);
                    assertNotEquals(applicationId, activityLog.getApplication().getId());
                });

        applicationFinanceRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationFinance -> {
                    assertNotNull(applicationFinance);
                    assertNotEquals(applicationId, applicationFinance.getApplication().getId());

                    applicationFinanceRowRepository.findByTargetId(applicationFinance.getId()).stream()
                            .forEach(applicationFinanceRow -> {
                                assertNotNull(applicationFinanceRow);
                                assertNotNull(applicationFinanceRow);

                                financeRowMetaValueRepository.financeRowId(applicationFinanceRow.getId()).stream()
                                        .forEach(financeRowMetaValue -> {
                                            assertNotNull(financeRowMetaValue);
                                            assertNotNull(financeRowMetaValue.getFinanceRowMetaField());
                                            assertNotNull(financeRowMetaValue.getValue());
                                        });
                            });
                });

        applicationHiddenFromDashboardRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationHiddenFromDashboard -> {
                    assertNotNull(applicationHiddenFromDashboard);
                    assertNotEquals(applicationId, applicationHiddenFromDashboard.getApplication().getId());
                });

        applicationOrganisationAddressRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationOrganisationAddress -> {
                    assertNotNull(applicationOrganisationAddress);
                    assertNotEquals(applicationId, applicationOrganisationAddress.getApplication().getId());
                });

        Optional<AverageAssessorScore> averageAssessorScore = averageAssessorScoreRepository.findByApplicationId(newApplication.getId());
        assertTrue(averageAssessorScore.isPresent());
        assertNotEquals(applicationId, averageAssessorScore.get().getApplication().getId());

        EuGrantTransfer euGrantTransfer = euGrantTransferRepository.findByApplicationId(newApplication.getId());
        assertNotNull(euGrantTransfer);
        assertNotEquals(applicationId, euGrantTransfer.getApplication().getId());

        formInputResponseRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(formInputResponse -> {
                    assertNotNull(formInputResponse);
                    assertNotEquals(applicationId, formInputResponse.getApplication().getId());
                });

        processRoleRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(processRole -> {
                    assertNotNull(processRole);
                    assertNotEquals(applicationId.longValue(), processRole.getApplicationId());
                });

        Optional<Project> project = projectRepository.findByApplicationId(newApplication.getId());
        assertTrue(project.isPresent());
        assertNotEquals(applicationId, project.get().getApplication().getId());

        Optional<ProjectToBeCreated> projectToBeCreated = projectToBeCreatedRepository.findByApplicationId(newApplication.getId());
        assertTrue(projectToBeCreated.isPresent());
        assertNotEquals(applicationId, projectToBeCreated.get().getApplication().getId());

        questionStatusRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(questionStatus -> {
                    assertNotNull(questionStatus);
                    assertNotEquals(applicationId, questionStatus.getApplication().getId());
                });

        GrantProcess grantProcess = grantProcessRepository.findOneByApplicationId(newApplication.getId());
        assertNotNull(grantProcess);
        assertNotEquals(applicationId.longValue(), grantProcess.getApplicationId());

        applicationProcessRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(applicationProcess -> {
                    assertNotNull(applicationProcess);
                    assertNotEquals(applicationId, applicationProcess.getTarget().getId());
                });

        processHistoryRepository.findByProcessId(newApplication.getId()).stream()
                .forEach(processHistory -> {
                    assertNotNull(processHistory);
                    assertNotEquals(newApplication, processHistory.getProcess().getTarget());
                });

        assessmentRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(assessment -> {
                    assertNotNull(assessment);
                    assertNotEquals(applicationId, assessment.getTarget().getId());
                });

        interviewRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(interview -> {
                    assertNotNull(interview);
                    assertNotEquals(applicationId, interview.getTarget().getId());
                });

        interviewAssignmentRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(interviewAssignment -> {
                    assertNotNull(interviewAssignment);
                    assertNotEquals(applicationId, interviewAssignment.getTarget().getId());
                });

        reviewRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(review -> {
                    assertNotNull(review);
                    assertNotEquals(applicationId, review.getTarget().getId());
                });

        supporterAssignmentRepository.findByTargetId(newApplication.getId()).stream()
                .forEach(supporterAssignment -> {
                    assertNotNull(supporterAssignment);
                    assertNotEquals(applicationId, supporterAssignment.getTarget().getId());
                });

        applicationInviteRepository.findByApplicationId(newApplication.getId()).stream()
                .forEach(applicationInvite -> {
                    assertNotNull(applicationInvite);
                    assertNotEquals(applicationId, applicationInvite.getTarget().getId());
                });

        Optional<ApplicationKtaInvite> applicationKtaInvite = applicationKtaInviteRepository.findByApplicationId(newApplication.getId());
        assertTrue(applicationKtaInvite.isPresent());
        assertNotEquals(applicationId, applicationKtaInvite.get().getTarget().getId());

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

        List<ApplicationExpressionOfInterestConfig> applicationExpressionOfInterestConfigs = applicationExpressionOfInterestConfigRepository.findByApplicationId(applicationId);
        assertThat(applicationExpressionOfInterestConfigs.size(), equalTo(0));
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

    @Test
    public void migrateEoiApplication() {
        setupEoiApplication();

        loginCompAdmin();

        ServiceResult<Long> result = applicationMigrationService.migrateApplication(applicationId, false);

        assertThat(result.isSuccess(), equalTo(true));

        Optional<Application> optionalEoiApplication =  applicationRepository.findByPreviousApplicationId(applicationId);
        assertTrue(optionalEoiApplication.isPresent());

        Application eoiApplication = optionalEoiApplication.get();
        assertNotEquals(applicationId, eoiApplication.getId());
        assertTrue(eoiApplication.getApplicationExpressionOfInterestConfig().isEnabledForExpressionOfInterest());

        ApplicationExpressionOfInterestConfig eoiApplicationExpressionOfInterestConfig = eoiApplication.getApplicationExpressionOfInterestConfig();
        applicationExpressionOfInterestConfigRepository.findById(eoiApplicationExpressionOfInterestConfig.getId()).stream()
                .forEach(applicationExpressionOfInterestConfig -> {
                    assertNotNull(applicationExpressionOfInterestConfig);
                    assertTrue(applicationExpressionOfInterestConfig.isEnabledForExpressionOfInterest());
                    assertEquals(eoiApplication.getId(), applicationExpressionOfInterestConfig.getApplication().getId());
                });

        Optional<Application> oldApplication = applicationRepository.findById(applicationId);
        assertTrue(oldApplication.isPresent());
    }
}