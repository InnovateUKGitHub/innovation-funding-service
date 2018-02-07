package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.sync.service.AllFinanceTotalsSender;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.SYSTEM_REGISTRATION_USER;

public class AllFinanceTotalsSenderSecurityTest extends BaseServiceSecurityTest<AllFinanceTotalsSender> {
    @Override
    protected Class<AllFinanceTotalsSenderSecurityTest.TestAllFinanceTotalsSender> getClassUnderTest() {
        return AllFinanceTotalsSenderSecurityTest.TestAllFinanceTotalsSender.class;
    }

    @Test
    public void testSendAllFinanceTotals_canOnlyBeRunBySystemRegistrar() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() ->
                classUnderTest.sendAllFinanceTotals(), SYSTEM_REGISTRATION_USER);

    }

    public static class TestAllFinanceTotalsSender implements AllFinanceTotalsSender {

        @Override
        public ServiceResult<Void> sendAllFinanceTotals() {
            return serviceSuccess();
        }
    }
}
