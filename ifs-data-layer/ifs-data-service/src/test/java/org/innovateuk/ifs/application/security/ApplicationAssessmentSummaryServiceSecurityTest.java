package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryService;
import org.innovateuk.ifs.application.transactional.ApplicationAssessmentSummaryServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class ApplicationAssessmentSummaryServiceSecurityTest extends
        BaseServiceSecurityTest<ApplicationAssessmentSummaryService> {

    @Test
    public void getAvailableAssessorsAllowedIfGlobalCompAdminOrProjectFinanceRole() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAvailableAssessors(1L, 0, 20, "", Sort.ASSESSOR), COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void getAssignedAssessorsAllowedIfGlobalCompAdminOrProjectFinanceRole() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAssignedAssessors(1L), COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void getApplicationAssessmentSummaryIfGlobalCompAdminOrProjectFinanceRole() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getApplicationAssessmentSummary(1L), COMP_ADMIN,
                PROJECT_FINANCE);
    }

    @Override
    protected Class<? extends ApplicationAssessmentSummaryService> getClassUnderTest() {
        return ApplicationAssessmentSummaryServiceImpl.class;
    }
}