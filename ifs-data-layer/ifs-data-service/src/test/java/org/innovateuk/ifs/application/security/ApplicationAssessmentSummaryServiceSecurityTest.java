package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class ApplicationAssessmentSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationAssessmentSummaryService> {

    @Test
    public void testGetAvailableAssessorsAllowedIfGlobalCompAdminOrProjectFinanceRole() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() ->
                classUnderTest.getAvailableAssessors(1L,0,20, 0L), COMP_ADMIN, PROJECT_FINANCE);
    }
    @Test
    public void testGetAssignedAssessorsAllowedIfGlobalCompAdminOrProjectFinanceRole() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAssignedAssessors(1L), COMP_ADMIN, PROJECT_FINANCE);
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
        public ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationAssessorPageResource> getAvailableAssessors(long applicationId, int pageIndex, int pageSize, Long filterInnovationArea) {
            return null;
        }

        @Override
        public ServiceResult<List<ApplicationAssessorResource>> getAssignedAssessors(long applicationId) {
            return null;
        }
    }
}