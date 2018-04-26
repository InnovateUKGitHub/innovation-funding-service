package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.finance.totals.service.AllFinanceTotalsSender;
import org.innovateuk.ifs.finance.totals.service.AllFinanceTotalsSenderImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.SYSTEM_MAINTAINER;

public class AllFinanceTotalsSenderSecurityTest extends BaseServiceSecurityTest<AllFinanceTotalsSender> {
    @Override
    protected Class<? extends AllFinanceTotalsSender> getClassUnderTest() {
        return AllFinanceTotalsSenderImpl.class;
    }

    @Test
    public void testSendAllFinanceTotals_canOnlyBeRunBySystemRegistrar() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() ->
                classUnderTest.sendAllFinanceTotals(), SYSTEM_MAINTAINER);

    }
}
