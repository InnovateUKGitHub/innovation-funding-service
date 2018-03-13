package org.innovateuk.ifs.form.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.security.SectionServiceSecurityTest.TestSectionService.ARRAY_SIZE_FOR_POST_FILTER_TESTS;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Module: innovation-funding-service
 **/
public class SectionServiceSecurityTest extends BaseServiceSecurityTest<SectionService> {

    private ApplicationLookupStrategy applicationLookupStrategy;
    private SectionPermissionRules sectionPermissionRules;

    @Before
    public void lookupPermissionRules() {
        sectionPermissionRules = getMockPermissionRulesBean(SectionPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void testPermissionForGetByCompetitionIdVisibleForAssessment() {
        classUnderTest.getByCompetitionIdVisibleForAssessment(1L);
        verify(sectionPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).userCanReadSection(isA(SectionResource.class), isA(UserResource.class));
    }

    @Override
    protected Class<? extends SectionService> getClassUnderTest() {
        return TestSectionService.class;
    }

    public static class TestSectionService implements SectionService {
        static final int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

        @Override
        public ServiceResult<SectionResource> getById(Long sectionId) {
            return null;
        }

        @Override
        public ServiceResult<Set<Long>> getQuestionsForSectionAndSubsections(Long sectionId) {
            return null;
        }

        @Override
        public ServiceResult<List<SectionResource>> getSectionsByCompetitionIdAndType(Long competitionId, SectionType type) {
            return null;
        }

        @Override
        public ServiceResult<SectionResource> getNextSection(Long sectionId) {
            return null;
        }

        @Override
        public ServiceResult<SectionResource> getNextSection(SectionResource section) {
            return null;
        }

        @Override
        public ServiceResult<SectionResource> getPreviousSection(Long sectionId) {
            return null;
        }

        @Override
        public ServiceResult<SectionResource> getPreviousSection(SectionResource section) {
            return null;
        }

        @Override
        public ServiceResult<SectionResource> getSectionByQuestionId(Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<List<SectionResource>> getByCompetitionId(Long CompetitionId) {
            return null;
        }

        @Override
        public ServiceResult<List<SectionResource>> getByCompetitionIdVisibleForAssessment(Long competitionId) {
            return serviceSuccess(newSectionResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }
    }
}
