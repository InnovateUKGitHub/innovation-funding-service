package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationProgressServiceSecurityTest extends BaseServiceSecurityTest<ApplicationProgressService> {
    private ApplicationPermissionRules applicationRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void updateApplicationProgress() {
        when(applicationLookupStrategy.getApplicationResource(1L)).thenReturn(newApplicationResource().build());

        assertAccessDenied(
                () -> classUnderTest.updateApplicationProgress(1L),
                () -> verify(applicationRules).applicantCanUpdateApplicationResource(
                        isA(ApplicationResource.class),
                        isA(UserResource.class)
                ));
    }

    @Test
    public void applicationReadyForSubmit() {
        when(applicationLookupStrategy.getApplicationResource(1L)).thenReturn(newApplicationResource().build());

        assertAccessDenied(
                () -> classUnderTest.applicationReadyForSubmit(1L),
                () -> verify(applicationRules).applicantCanUpdateApplicationResource(
                        isA(ApplicationResource.class),
                        isA(UserResource.class)
                ));
    }

    @Override
    protected Class<? extends ApplicationProgressService> getClassUnderTest() {
        return ApplicationProgressServiceSecurityTest.TestApplicationProgressService.class;
    }

    /**
     * Dummy implementation (for satisfying Spring Security's need to read parameter information from
     * methods, which is lost when using mocks)
     */
    public static class TestApplicationProgressService implements ApplicationProgressService {
        @Override
        public boolean applicationReadyForSubmit(Long id) {
            return false;
        }

        @Override
        public ServiceResult<BigDecimal> updateApplicationProgress(final Long applicationId) {
            return  null;
        }

    }
}
