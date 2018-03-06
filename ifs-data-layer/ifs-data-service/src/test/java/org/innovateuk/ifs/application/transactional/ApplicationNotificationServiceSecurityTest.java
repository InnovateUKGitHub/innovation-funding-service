package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationNotificationServiceSecurityTest extends BaseServiceSecurityTest<ApplicationNotificationService> {
    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void sendNotificationApplicationSubmitted() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.sendNotificationApplicationSubmitted(applicationId),
                () -> verify(applicationRules).aLeadApplicantCanSendApplicationSubmittedNotification(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void informIneligible() {
        long applicationId = 1L;
        ApplicationIneligibleSendResource applicationIneligibleSendResource = newApplicationIneligibleSendResource().build();
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.informIneligible(applicationId, applicationIneligibleSendResource),
                PROJECT_FINANCE, COMP_ADMIN);
    }

    @Test
    public void notifyApplicantsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.notifyApplicantsByCompetition(1L),
                PROJECT_FINANCE, COMP_ADMIN);
    }

    @Override
    protected Class<? extends ApplicationNotificationService> getClassUnderTest() {
        return ApplicationNotificationServiceSecurityTest.TestApplicationNotificationService.class;
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestApplicationNotificationService implements ApplicationNotificationService {
        @Override
        public ServiceResult<Void> sendNotificationApplicationSubmitted(Long application) {
            return null;
        }

        @Override
        public ServiceResult<Void> notifyApplicantsByCompetition(Long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> informIneligible(long applicationId, ApplicationIneligibleSendResource applicationIneligibleSendResource) {
            return null;
        }
    }
}
