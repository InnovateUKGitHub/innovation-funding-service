package org.innovateuk.ifs.activitylog.advice;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.activitylog.repository.ActivityLogRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Transactional
@Rollback
public class ActivityLogAdviceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    public static final ActivityType TEST_ACTIVITY_TYPE = ActivityType.APPLICATION_SUBMITTED;

    @Autowired
    private TestActivityLogService testActivityLogService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Before
    public void login() {
        loginSteveSmith();
    }

    @Test
    public void withApplicationId() {
        Application application = applicationRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(application);

        ServiceResult<Void> result = testActivityLogService.withApplicationId(application.getId());

        assertOneActivityLogsExistForApplication(application);
        assertTrue(result.isSuccess());
    }

    @Test
    public void withProjectId() {
        Project project = projectRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(project.getApplication());

        ServiceResult<Void> result = testActivityLogService.withProjectId(project.getId());

        assertOneActivityLogsExistForApplication(project.getApplication());
        assertTrue(result.isSuccess());
    }

    @Test
    public void withProjectOrganisationCompositeId() {
        Project project = projectRepository.findAll().get(0);
        Organisation organisation = organisationRepository.findAll().iterator().next();

        assertZeroActivityLogsExistForApplication(project.getApplication());

        ServiceResult<Void> result = testActivityLogService.withProjectOrganisationCompositeId(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()));

        ActivityLog activityLog = assertOneActivityLogsExistForApplication(project.getApplication());
        assertEquals(organisation.getId(), activityLog.getOrganisation().getId());
        assertTrue(result.isSuccess());
    }


    @Test
    public void withNotMatchingApplicationId() {
        Application application = applicationRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(application);

        ServiceResult<Void> result = testActivityLogService.withNotMatchingApplicationId(application.getId());

        assertZeroActivityLogsExistForApplication(application);
        assertTrue(result.isSuccess());
    }

    @Test
    public void withNotMatchingProjectId() {
        Project project = projectRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(project.getApplication());

        ServiceResult<Void> result = testActivityLogService.withNotMatchingProjectId(project.getId());

        assertZeroActivityLogsExistForApplication(project.getApplication());
        assertTrue(result.isSuccess());
    }

    @Test
    public void withNotMatchingProjectOrganisationCompositeId() {
        Project project = projectRepository.findAll().get(0);
        Organisation organisation = organisationRepository.findAll().iterator().next();

        assertZeroActivityLogsExistForApplication(project.getApplication());

        testActivityLogService.withNotMatchingProjectOrganisationCompositeId(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()));

        assertZeroActivityLogsExistForApplication(project.getApplication());
    }

    @Test
    public void withApplicationIdConditional_match() {
        Application application = applicationRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(application);

        ServiceResult<Void> result = testActivityLogService.withApplicationIdConditional(application.getId(), true);

        assertOneActivityLogsExistForApplication(application);
        assertTrue(result.isSuccess());
    }

    @Test
    public void withApplicationIdConditional_mismatch() {
        Application application = applicationRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(application);

        ServiceResult<Void> result = testActivityLogService.withApplicationIdConditional(application.getId(), false);

        assertZeroActivityLogsExistForApplication(application);
        assertTrue(result.isSuccess());
    }

    @Test
    public void withApplicationIdNotMatchingConditional() {
        Application application = applicationRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(application);

        ServiceResult<Void> result = testActivityLogService.withApplicationIdNotMatchingConditional(application.getId(), true);

        assertZeroActivityLogsExistForApplication(application);
        assertTrue(result.isSuccess());
    }

    @Test
    public void withApplicationIdServiceFailure() {
        Application application = applicationRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(application);

        ServiceResult<Void> result = testActivityLogService.withApplicationIdServiceFailure(application.getId());

        assertZeroActivityLogsExistForApplication(application);
        assertTrue(result.isFailure());
    }

    @Test
    public void withApplicationIdNotServiceResult() {
        Application application = applicationRepository.findAll().get(0);

        assertZeroActivityLogsExistForApplication(application);

        testActivityLogService.withApplicationIdNotServiceResult(application.getId());

        assertZeroActivityLogsExistForApplication(application);
    }

    private void assertZeroActivityLogsExistForApplication(Application application) {
        assertTrue(activityLogRepository.findByApplicationId(application.getId()).isEmpty());
    }

    private ActivityLog assertOneActivityLogsExistForApplication(Application application) {
        List<ActivityLog> activityLogs = activityLogRepository.findByApplicationId(application.getId());
        assertEquals(activityLogs.size(), 1);
        return activityLogs.get(0);
    }

}
