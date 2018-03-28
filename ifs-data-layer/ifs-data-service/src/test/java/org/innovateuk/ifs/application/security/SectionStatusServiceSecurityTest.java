package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(newApplicationResource().build());
        assertAccessDenied(
                () -> classUnderTest.markSectionAsComplete(sectionId, applicationId, markedAsCompleteById),
                () -> verify(questionStatusRules).onlyMemberOfProjectTeamCanMarkSectionAsComplete(isA(ApplicationResource.class), isA(UserResource.class))
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
                () -> verify(questionStatusRules).onlyMemberOfProjectTeamCanMarkSectionAsInComplete(isA(ApplicationResource.class), isA(UserResource.class))
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
                () -> verify(questionStatusRules).onlyMemberOfProjectTeamCanMarkSectionAsNotRequired(isA(ApplicationResource.class), isA(UserResource.class))
        );
    }

    @Override
    protected Class<? extends SectionStatusService> getClassUnderTest() {
        return TestSectionStatusService.class;
    }

    public static class TestSectionStatusService implements SectionStatusService {
        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;


        @Override
        public ServiceResult<Map<Long, Set<Long>>> getCompletedSections(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Set<Long>> getCompletedSections(long applicationId, long organisationId) {
            return null;
        }

        @Override
        public ServiceResult<List<ValidationMessages>> markSectionAsComplete(long sectionId, long applicationId, long markedAsCompleteById) {
            return null;
        }

        @Override
        public ServiceResult<Void> markSectionAsNotRequired(long sectionId, long applicationId, long markedAsCompleteById) {
            return null;
        }

        @Override
        public ServiceResult<Void> markSectionAsInComplete(long sectionId, long applicationId, long markedAsInCompleteById) {
            return null;
        }

        @Override
        public ServiceResult<List<Long>> getIncompleteSections(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> childSectionsAreCompleteForAllOrganisations(Section parentSection, long applicationId, Section excludedSection) {
            return null;
        }
    }
}
