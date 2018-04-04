package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.finance.totals.service.CompetitionFinanceTotalsSender;
import org.innovateuk.ifs.finance.totals.service.CompetitionFinanceTotalsSenderImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.SYSTEM_MAINTAINER;

public class CompetitionFinanceTotalsSenderSecurityTest extends
        BaseServiceSecurityTest<CompetitionFinanceTotalsSender> {
    @Override
    protected Class<? extends CompetitionFinanceTotalsSender> getClassUnderTest() {
        return CompetitionFinanceTotalsSenderImpl.class;
    }

    @Test
    public void testSendFinanceTotalsForCompetition() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() ->
                classUnderTest.sendFinanceTotalsForCompetition(1L), SYSTEM_MAINTAINER);
    }
}