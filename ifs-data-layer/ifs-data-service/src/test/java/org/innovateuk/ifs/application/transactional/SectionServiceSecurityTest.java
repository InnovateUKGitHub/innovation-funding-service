package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.SectionPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Module: innovation-funding-service
 **/
public class SectionServiceSecurityTest extends BaseServiceSecurityTest<SectionService> {

    private static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    private ApplicationLookupStrategy applicationLookupStrategy;
    private SectionPermissionRules sectionPermissionRules;

    @Before
    public void lookupPermissionRules() {
        sectionPermissionRules = getMockPermissionRulesBean(SectionPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void testPermissionForMarkSectionAsComplete() {
        Long sectionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById),
                () -> verify(sectionPermissionRules)
                        .onlyMemberOfProjectTeamCanMarkSectionAsComplete(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testPermissionForMarkSectionAsInComplete() {
        Long sectionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.markSectionAsInComplete(sectionId, applicationId, markedAsCompleteById),
                () -> verify(sectionPermissionRules)
                        .onlyMemberOfProjectTeamCanMarkSectionAsInComplete(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testPermissionForMarkSectionAsNotRequired() {
        Long sectionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.markSectionAsNotRequired(sectionId, applicationId, markedAsCompleteById),
                () -> verify(sectionPermissionRules)
                        .onlyMemberOfProjectTeamCanMarkSectionAsNotRequired(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void testPermissionForGetByCompetitionIdVisibleForAssessment() {
        when(classUnderTestMock.getByCompetitionIdVisibleForAssessment(1L))
                .thenReturn(serviceSuccess(newSectionResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS)));

        classUnderTest.getByCompetitionIdVisibleForAssessment(1L);
        verify(sectionPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS))
                .userCanReadSection(isA(SectionResource.class), isA(UserResource.class));
    }

    @Override
    protected Class<? extends SectionService> getClassUnderTest() {
        return SectionServiceImpl.class;
    }
}
