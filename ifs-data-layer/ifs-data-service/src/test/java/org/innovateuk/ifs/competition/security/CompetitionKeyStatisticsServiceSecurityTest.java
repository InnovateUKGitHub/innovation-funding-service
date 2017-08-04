package org.innovateuk.ifs.competition.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class CompetitionKeyStatisticsServiceSecurityTest extends BaseServiceSecurityTest<CompetitionKeyStatisticsService> {


    @Override
    protected Class<? extends CompetitionKeyStatisticsService> getClassUnderTest() {
        return TestCompetitionKeyStatisticsService.class;
    }

    @Test
    public void getReadyToOpenKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getReadyToOpenKeyStatisticsByCompetition(1L), COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getOpenKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getOpenKeyStatisticsByCompetition(1L), COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getClosedKeyStatisticsByCompetition(1L), COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getInAssessmentKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInAssessmentKeyStatisticsByCompetition(1L), COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    @Test
    public void getFundedKeyStatisticsByCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getFundedKeyStatisticsByCompetition(1L), COMP_ADMIN, PROJECT_FINANCE, INNOVATION_LEAD);
    }

    public static class TestCompetitionKeyStatisticsService implements CompetitionKeyStatisticsService {
        @Override
        public ServiceResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId) {
            return null;
        }
    }
}
