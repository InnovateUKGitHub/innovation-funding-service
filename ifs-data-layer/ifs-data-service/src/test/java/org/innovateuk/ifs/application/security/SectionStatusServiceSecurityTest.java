package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.application.transactional.SectionStatusServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Module: innovation-funding-service
 **/
public class SectionStatusServiceSecurityTest extends BaseServiceSecurityTest<SectionStatusService> {

    private ApplicationLookupStrategy applicationLookupStrategy;
    private QuestionStatusRules questionStatusRules;

    @Before
    public void lookupPermissionRules() {
        questionStatusRules = getMockPermissionRulesBean(QuestionStatusRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void testPermissionForMarkSectionAsComplete() {
        Long sectionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource()
                .build());
        assertAccessDenied(
                () -> classUnderTest.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById),
                () -> verify(questionStatusRules).onlyMemberOfProjectTeamCanMarkSection(isA(ApplicationResource.class),
                        isA(UserResource.class))
        );
    }

    @Test
    public void testPermissionForMarkSectionAsInComplete() {
        Long sectionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource()
                .build());
        assertAccessDenied(
                () -> classUnderTest.markSectionAsInComplete(sectionId, applicationId, markedAsCompleteById),
                () -> verify(questionStatusRules).onlyMemberOfProjectTeamCanMarkSection(isA(ApplicationResource.class),
                        isA(UserResource.class))
        );
    }

    @Test
    public void testPermissionForMarkSectionAsNotRequired() {
        Long sectionId = 1L;
        Long applicationId = 2L;
        Long markedAsCompleteById = 3L;
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource()
                .build());
        assertAccessDenied(
                () -> classUnderTest.markSectionAsNotRequired(sectionId, applicationId, markedAsCompleteById),
                () -> verify(questionStatusRules).onlyMemberOfProjectTeamCanMarkSection(isA(ApplicationResource.class),
                isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends SectionStatusService> getClassUnderTest() {
        return SectionStatusServiceImpl.class;
    }
}