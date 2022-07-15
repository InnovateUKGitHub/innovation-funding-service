package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.junit.Test;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.user.resource.Role.*;

public class ApplicationSummaryServiceSecurityTest extends BaseServiceSecurityTest<ApplicationSummaryService> {

    @Test
    public void getApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getApplicationSummariesByCompetitionId(1L, null, 0, 20, empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT);
    }

    @Test
    public void getSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER, AUDITOR);
    }

    @Test
    public void getNotSubmittedApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getNotSubmittedApplicationSummariesByCompetitionId(1L, null, 0, 20),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER, AUDITOR);
    }

    @Test
    public void getWithFundingDecisionApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getWithFundingDecisionApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER, AUDITOR);
    }

    @Test
    public void getIneligibleApplicationSummariesByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getIneligibleApplicationSummariesByCompetitionId(1L, null, 0, 20, empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER, AUDITOR);
    }

    @Test
    public void getAllSubmittedApplicationIdsByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAllSubmittedApplicationIdsByCompetitionId(1L, empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER, AUDITOR);
    }

    @Test
    public void getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(1L, empty(), empty(), empty(), empty()),
                PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, STAKEHOLDER, AUDITOR);
    }

    @Override
    protected Class<? extends ApplicationSummaryService> getClassUnderTest() {
        return ApplicationSummaryServiceImpl.class;
    }
}
