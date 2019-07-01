package org.innovateuk.ifs.activitylog.advice;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.activitylog.domain.ActivityLog;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
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

import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
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

    private Application application;
    private Project project;
    private Organisation organisation;

    @Before
    public void login() {
        loginSteveSmith();

        application = applicationRepository.save(new Application());
        project = projectRepository.save(newProject().withName("Project name").withApplication(application).build());
        organisation = organisationRepository.save(newOrganisation().build());
    }

    @Test
    public void withApplicationId() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withApplicationId(application.getId());

        assertOneActivityLogsExistForApplication();
        assertTrue(result.isSuccess());
    }

    @Test
    public void withProjectId() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withProjectId(project.getId());

        assertOneActivityLogsExistForApplication();
        assertTrue(result.isSuccess());
    }

    @Test
    public void withProjectOrganisationCompositeId() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withProjectOrganisationCompositeId(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()));

        ActivityLog activityLog = assertOneActivityLogsExistForApplication();
        assertEquals(organisation.getId(), activityLog.getOrganisation().getId());
        assertTrue(result.isSuccess());
    }


    @Test
    public void withNotMatchingApplicationId() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withNotMatchingApplicationId(application.getId());

        assertZeroActivityLogsExistForApplication();
        assertTrue(result.isSuccess());
    }

    @Test
    public void withNotMatchingProjectId() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withNotMatchingProjectId(project.getId());

        assertZeroActivityLogsExistForApplication();
        assertTrue(result.isSuccess());
    }

    @Test
    public void withNotMatchingProjectOrganisationCompositeId() {
        assertZeroActivityLogsExistForApplication();

        testActivityLogService.withNotMatchingProjectOrganisationCompositeId(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()));

        assertZeroActivityLogsExistForApplication();
    }

    @Test
    public void withApplicationIdConditional_match() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withApplicationIdConditional(application.getId(), true);

        assertOneActivityLogsExistForApplication();
        assertTrue(result.isSuccess());
    }

    @Test
    public void withApplicationIdConditional_mismatch() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withApplicationIdConditional(application.getId(), false);

        assertZeroActivityLogsExistForApplication();
        assertTrue(result.isSuccess());
    }

    @Test
    public void withApplicationIdNotMatchingConditional() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withApplicationIdNotMatchingConditional(application.getId(), true);

        assertZeroActivityLogsExistForApplication();
        assertTrue(result.isSuccess());
    }

    @Test
    public void withApplicationIdServiceFailure() {
        assertZeroActivityLogsExistForApplication();

        ServiceResult<Void> result = testActivityLogService.withApplicationIdServiceFailure(application.getId());

        assertZeroActivityLogsExistForApplication();
        assertTrue(result.isFailure());
    }

    @Test
    public void withApplicationIdNotServiceResult() {
        assertZeroActivityLogsExistForApplication();

        testActivityLogService.withApplicationIdNotServiceResult(application.getId());

        assertZeroActivityLogsExistForApplication();
    }

    private void assertZeroActivityLogsExistForApplication() {
        assertTrue(activityLogRepository.findByApplicationIdOrderByCreatedOnDesc(application.getId()).isEmpty());
    }

    private ActivityLog assertOneActivityLogsExistForApplication() {
        List<ActivityLog> activityLogs = activityLogRepository.findByApplicationIdOrderByCreatedOnDesc(application.getId());
        assertEquals(activityLogs.size(), 1);
        return activityLogs.get(0);
    }

}
