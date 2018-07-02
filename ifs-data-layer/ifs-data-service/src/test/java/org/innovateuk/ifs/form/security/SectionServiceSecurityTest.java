package org.innovateuk.ifs.form.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.form.transactional.SectionServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Module: innovation-funding-service
 **/
public class SectionServiceSecurityTest extends BaseServiceSecurityTest<SectionService> {

    private static int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    private ApplicationLookupStrategy applicationLookupStrategy;
    private SectionPermissionRules sectionPermissionRules;

    @Before
    public void lookupPermissionRules() {
        sectionPermissionRules = getMockPermissionRulesBean(SectionPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void testPermissionForGetByCompetitionIdVisibleForAssessment() {
        when(classUnderTestMock.getByCompetitionIdVisibleForAssessment(1L)).thenReturn(serviceSuccess
                (newSectionResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.getByCompetitionIdVisibleForAssessment(1L);

        verify(sectionPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).userCanReadSection(isA
                (SectionResource.class), isA(UserResource.class));
    }

    @Override
    protected Class<? extends SectionService> getClassUnderTest() {
        return SectionServiceImpl.class;
    }
}
