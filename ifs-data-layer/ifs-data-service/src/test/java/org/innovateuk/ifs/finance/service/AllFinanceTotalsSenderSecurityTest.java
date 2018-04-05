package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.totals.service.AllFinanceTotalsSender;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.SYSTEM_MAINTAINER;

public class AllFinanceTotalsSenderSecurityTest extends BaseServiceSecurityTest<AllFinanceTotalsSender> {
    @Override
    protected Class<AllFinanceTotalsSenderSecurityTest.TestAllFinanceTotalsSender> getClassUnderTest() {
        return AllFinanceTotalsSenderSecurityTest.TestAllFinanceTotalsSender.class;
    }

    @Test
    public void testSendAllFinanceTotals_canOnlyBeRunBySystemRegistrar() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() ->
                classUnderTest.sendAllFinanceTotals(), SYSTEM_MAINTAINER);

    }

    public static class TestAllFinanceTotalsSender implements AllFinanceTotalsSender {

        @Override
        public ServiceResult<Void> sendAllFinanceTotals() {
            return serviceSuccess();
        }
    }
}
