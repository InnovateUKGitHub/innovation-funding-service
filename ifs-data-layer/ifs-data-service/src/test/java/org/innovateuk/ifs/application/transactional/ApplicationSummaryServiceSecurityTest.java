package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.junit.Test;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.user.resource.Role.*;

public class ApplicationSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationSummaryService> {

    @Test
    public void test_getApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getApplicationSummariesByCompetitionId(1L, null, 0, 20, empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT);
    }

    @Test
    public void test_getSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    @Test
    public void test_getNotSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    @Test
    public void test_getWithFundingDecisionApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getWithFundingDecisionApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    @Test
    public void test_getIneligibleApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getIneligibleApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    @Test
    public void test_getApplicationTeamByApplicationId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getApplicationTeamByApplicationId(1L),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    @Test
    public void test_getAllSubmittedApplicationIdsByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAllSubmittedApplicationIdsByCompetitionId(1L, empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    @Test
    public void test_getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(1L, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);
    }

    @Override
    protected Class<? extends ApplicationSummaryService> getClassUnderTest() {
        return ApplicationSummaryServiceImpl.class;
    }
}
