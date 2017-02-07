package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class ApplicationAssessmentSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationAssessmentSummaryService> {

    @Test
    public void testGetAssessorsAllowedIfGlobalCompAdminOrProjectFinanceRole() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAssessors(1L), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void testGetApplicationAssessmentSummaryIfGlobalCompAdminOrProjectFinanceRole() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getApplicationAssessmentSummary(1L), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Override
    protected Class<? extends ApplicationAssessmentSummaryService> getClassUnderTest() {
        return ApplicationAssessmentSummaryServiceTest.class;
    }

    public static class ApplicationAssessmentSummaryServiceTest implements ApplicationAssessmentSummaryService {

        @Override
        public ServiceResult<List<ApplicationAssessorResource>> getAssessors(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId) {
            return null;
        }
    }
}