package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.*;

public class CompetitionKeyStatisticsServiceSecurityTest extends
        BaseServiceSecurityTest<CompetitionKeyStatisticsService> {


    @Override
    protected Class<? extends CompetitionKeyStatisticsService> getClassUnderTest() {
        return CompetitionKeyStatisticsServiceImpl.class;
    }

    @Test
    public void getReadyToOpenKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getReadyToOpenKeyStatisticsByCompetition(1L),
                COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getOpenKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getOpenKeyStatisticsByCompetition(1L),
                COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getClosedKeyStatisticsByCompetition(1L),
                COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getInAssessmentKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInAssessmentKeyStatisticsByCompetition(1L),
                COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getFundedKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getFundedKeyStatisticsByCompetition(1L),
                COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }
}
