package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.Mockito.*;

public class ApplicationInnovationAreaServiceSecurityTest extends BaseServiceSecurityTest<ApplicationInnovationAreaService> {
    private ApplicationLookupStrategy applicationLookupStrategy;
    private ApplicationPermissionRules applicationRules;

    @Override
    protected Class<? extends ApplicationInnovationAreaService> getClassUnderTest() {
        return ApplicationInnovationAreaServiceImpl.class;
    }

    @Before
    public void lookupPermissionRules() {
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
    }

    @Test
    public void setNoInnovationAreaApplies() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.setNoInnovationAreaApplies(applicationId),
                () -> verify(applicationRules).leadApplicantCanUpdateApplicationResource(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void setInnovationArea() {
        final long applicationId = 1L;
        final long innovationAreaId = 1L;

        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.setInnovationArea(applicationId, innovationAreaId),
                () -> verify(applicationRules).leadApplicantCanUpdateApplicationResource(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void getAvailableInnovationAreas() {
        final long applicationId = 1L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.getAvailableInnovationAreas(applicationId),
                () -> verify(applicationRules).usersConnectedToTheApplicationCanViewInnovationAreas(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }
}
