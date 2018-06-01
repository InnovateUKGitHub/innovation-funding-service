package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyApplicationStatisticsService;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyApplicationStatisticsServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.*;

public class CompetitionKeyApplicationStatisticsServiceSecurityTest extends
        BaseServiceSecurityTest<CompetitionKeyApplicationStatisticsService> {

    @Override
    protected Class<? extends CompetitionKeyApplicationStatisticsService> getClassUnderTest() {
        return CompetitionKeyApplicationStatisticsServiceImpl.class;
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
    public void getFundedKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getFundedKeyStatisticsByCompetition(1L),
                COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }
}
