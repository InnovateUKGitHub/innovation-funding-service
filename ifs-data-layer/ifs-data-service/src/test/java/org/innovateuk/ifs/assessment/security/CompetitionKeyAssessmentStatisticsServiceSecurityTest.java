package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.CompetitionKeyAssessmentStatisticsService;
import org.innovateuk.ifs.assessment.transactional.CompetitionKeyAssessmentStatisticsServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.*;

public class CompetitionKeyAssessmentStatisticsServiceSecurityTest extends
        BaseServiceSecurityTest<CompetitionKeyAssessmentStatisticsService> {


    @Override
    protected Class<? extends CompetitionKeyAssessmentStatisticsService> getClassUnderTest() {
        return CompetitionKeyAssessmentStatisticsServiceImpl.class;
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
}
