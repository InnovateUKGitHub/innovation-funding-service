package org.innovateuk.ifs.finance.service;


import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.totals.service.CompetitionFinanceTotalsSender;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.SYSTEM_MAINTAINER;

public class CompetitionFinanceTotalsSenderSecurityTest extends BaseServiceSecurityTest<CompetitionFinanceTotalsSender> {
    @Override
    protected Class<CompetitionFinanceTotalsSenderSecurityTest.TestCompetitionFinanceTotalsSender> getClassUnderTest() {
        return CompetitionFinanceTotalsSenderSecurityTest.TestCompetitionFinanceTotalsSender.class;
    }

    @Test
    public void testSendFinanceTotalsForCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() ->
                classUnderTest.sendFinanceTotalsForCompetition(1L), SYSTEM_MAINTAINER);

    }

    public static class TestCompetitionFinanceTotalsSender implements CompetitionFinanceTotalsSender {

        @Override
        public ServiceResult<Void> sendFinanceTotalsForCompetition(Long competitionId) {
            return serviceSuccess();
        }
    }
}