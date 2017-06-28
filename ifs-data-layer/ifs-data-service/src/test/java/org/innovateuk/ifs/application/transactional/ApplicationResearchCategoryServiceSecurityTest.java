package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.Mockito.*;

/**
 * Testing the Spring Security and CustomPermissionEvaluator integration with ResearchCategoryService with regards to the
 * security rules that are
 */

public class ApplicationResearchCategoryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationResearchCategoryService> {

    private ApplicationLookupStrategy applicationLookupStrategy;
    private ApplicationPermissionRules applicationRules;

    @Override
    protected Class<? extends ApplicationResearchCategoryService> getClassUnderTest() {
        return TestApplicationResearchCategoryService.class;
    }

    @Before
    public void lookupPermissionRules() {
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
        applicationRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
    }

    @Test
    public void setResearchCategory() {
        final long applicationId = 1L;
        final long researchCategoryId = 1L;

        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.setResearchCategory(applicationId, researchCategoryId),
                () -> verify(applicationRules).leadApplicantCanUpdateResearchCategory(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    public static class TestApplicationResearchCategoryService implements ApplicationResearchCategoryService {

        public ServiceResult<ApplicationResource> setResearchCategory(Long applicationId, Long researchCategoryId) {
            return null;
        }
    }
}
