package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationMigration;
import org.innovateuk.ifs.application.domain.MigrationStatus;
import org.innovateuk.ifs.application.repository.*;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AverageAssessorScoreRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ApplicationKtaInviteRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectToBeCreatedRepository;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

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

    private ApplicationMigration applicationMigration;

    @Before
    public void setup() {
        loginIfsAdmin();
        application = applicationRepository.save(new Application());

        applicationMigration = new ApplicationMigration(application.getId(), MigrationStatus.CREATED);
        applicationMigrationRepository.save(applicationMigration);
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
